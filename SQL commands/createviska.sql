CREATE TABLE siga5828.Label
(
	id_label		SMALLINT		NOT NULL CHECK (id_label >= 0),
	pavadinimas		VARCHAR(35)		NOT NULL,
	veiklos_metai_pradzia	CHAR(4)	NOT NULL,
	veiklos_metai_pabaiga	CHAR(4) DEFAULT EXTRACT(YEAR FROM CURRENT_DATE),
	PRIMARY KEY (id_label)
);

CREATE TABLE siga5828.Artist
(
	--id_artist		SMALLINT		NOT NULL CHECK (id_artist >= 0),
    id_artist 		SERIAL,
	pavadinimas		VARCHAR(35)		NOT NULL,
	salis			VARCHAR(25)		DEFAULT 'Unknown',
	veiklos_metai_pradzia	CHAR(4)	NOT NULL,
	veiklos_metai_pabaiga	CHAR(4)	DEFAULT EXTRACT(YEAR FROM CURRENT_DATE),
	PRIMARY KEY (id_artist)
);

CREATE TABLE siga5828.Priklauso
(
	id_artist		SMALLINT	NOT NULL,
	id_label		SMALLINT	NOT NULL,
	CONSTRAINT Iartist FOREIGN KEY (id_artist) REFERENCES siga5828.Artist ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT Ilabel FOREIGN KEY (id_label)	REFERENCES siga5828.Label ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE siga5828.Album
(
	id_album		SMALLINT	NOT NULL CHECK (id_album >= 0),
	trukme			TIME		NOT NULL,
	zanras			VARCHAR(20) NOT NULL,
	pavadinimas		VARCHAR(35)	NOT NULL,
	metai			CHAR(4)		DEFAULT EXTRACT(YEAR FROM CURRENT_DATE) CONSTRAINT IsleidimoMetai CHECK (CAST(metai AS INTEGER) > 1900),
	PRIMARY KEY (id_album)
);

CREATE TABLE siga5828.Song
(
	id_song			SMALLINT 	NOT NULL CHECK (id_song >= 0),
	trukme			TIME 		NOT NULL,
	pavadinimas		VARCHAR(50) NOT NULL,
	bpm				SMALLINT 	NOT NULL CHECK (bpm >= 20 AND bpm <= 500),
	PRIMARY KEY (id_song)
);

CREATE TABLE siga5828.Turi
(
	id_album		SMALLINT	NOT NULL,
	id_song		SMALLINT	NOT NULL,
	CONSTRAINT Ialbum FOREIGN KEY (id_album)	REFERENCES siga5828.Album ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT Isong FOREIGN KEY (id_song)	REFERENCES siga5828.Song ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE siga5828.Sukure
(
	id_artist		SMALLINT	NOT NULL,
	id_album		SMALLINT	NOT NULL,
	CONSTRAINT Ialbum FOREIGN KEY (id_album)	REFERENCES siga5828.Album ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT Iartist FOREIGN KEY (id_artist)	REFERENCES siga5828.Artist ON DELETE CASCADE ON UPDATE CASCADE
);
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--	Jeigu atlikejas priklauso kazkuriai irasu bendrovei tai keisti jo vardo neimanoma
CREATE OR REPLACE FUNCTION nekeistiVardo() RETURNS trigger AS
$$
	BEGIN
		IF (SELECT COUNT(*) FROM siga5828.Artist A, siga5828.Priklauso B
			WHERE A.id_artist = NEW.id_artist) <> 0 
		THEN
			RAISE EXCEPTION 'Šis atlikėjas priklauso kažkuriai įrašų bendrovei, todėl jo pavadinimas nekeičiamas';
		END IF;
		RETURN NEW;
	END;
$$
LANGUAGE plpgsql;
CREATE TRIGGER nekeisti
	BEFORE UPDATE 
	ON siga5828.Artist
	FOR EACH ROW 
	EXECUTE PROCEDURE nekeistiVardo();

-- trigeris, užtikrinantis, kad joks atlikejas negali
-- priklausyti daugiau nei trims irasu bendrovems:
CREATE FUNCTION MaxSkaicius()
RETURNS TRIGGER AS
$$
	BEGIN
		IF (SELECT COUNT(*) FROM siga5828.Priklauso A
		 	WHERE A.id_artist=NEW.id_artist) >= 3
		THEN
		 	RAISE EXCEPTION 'Virsytas irasu bendroviu skaicius';
		END IF;
		RETURN NEW;
	END; 
$$
LANGUAGE plpgsql ;
CREATE TRIGGER MaxSkaicius
	BEFORE INSERT OR UPDATE
	ON siga5828.Priklauso
	FOR EACH ROW
	EXECUTE PROCEDURE MaxSkaicius(); 

--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--Randa atlikeja ir jo albumus
CREATE VIEW siga5828.AtlikejuAlbumai AS
	SELECT A.pavadinimas AS "Albumo pavadinimas", B.pavadinimas AS "Atlikejas"
	FROM siga5828.Album A, siga5828.Artist B, siga5828.Sukure C
	WHERE A.id_album = C.id_album AND B.id_artist = C.id_artist;

--Randa atlikejo veiklos metus(kaip ilgai tesiasi jo karjera)
CREATE VIEW siga5828.AtlikejoVeiklosMetai AS
	SELECT id_artist, pavadinimas, (CAST(veiklos_metai_pabaiga AS INTEGER) - CAST(veiklos_metai_pradzia AS INTEGER)) 
	AS "aktyvumo metai"
	FROM siga5828.Artist;

--Materializuota lentele, pateikia, kiek atlikeju priklauso irasu bendrovei
CREATE MATERIALIZED VIEW siga5828.AtlikejuKiekis
	AS SELECT A.id_label, A.pavadinimas, count(B.id_artist) 
	FROM siga5828.Label A, siga5828.Artist B, siga5828.Priklauso C
	WHERE A.id_label = C.id_label AND B.id_artist = C.id_artist
	GROUP BY A.id_label
WITH DATA;

REFRESH MATERIALIZED VIEW siga5828.AtlikejuKiekis;


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

--indeksas paspartinantis paiešką pagal dainos pavadinimą
CREATE INDEX Dainai ON siga5828.Song(pavadinimas);

--indeksas paspartinantis paiešką pagal įrašų bendrovės veiklos pradžią ir pabaigą
CREATE INDEX IrBendrovei ON siga5828.Label(veiklos_metai_pradzia, veiklos_metai_pabaiga);

--unikalus indeksas užtikrinantis kad gali egzistuoti tik 1 irsasu bendrove tuo pavadinimu
CREATE UNIQUE INDEX VienasLabel ON siga5828.Label(pavadinimas)