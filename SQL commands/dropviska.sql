DROP TRIGGER nekeisti ON siga5828.Artist;
DROP FUNCTION nekeistiVardo();

DROP TRIGGER MaxSkaicius ON siga5828.Priklauso;
DROP FUNCTION MaxSkaicius();

DROP INDEX Dainai;
DROP INDEX IrBendrovei;
DROP INDEX VienasLabel;

DROP VIEW siga5828.AtlikejuAlbumai;
DROP VIEW siga5828.AtlikejoVeiklosMetai;
DROP MATERIALIZED VIEW siga5828.AtlikejuKiekis;

DELETE FROM siga5828.Sukure;
DELETE FROM siga5828.Turi;
DELETE FROM siga5828.Song;
DELETE FROM siga5828.Album;
DELETE FROM siga5828.Priklauso;
DELETE FROM siga5828.Artist;
DELETE FROM siga5828.Label;

DROP TABLE siga5828.Sukure;
DROP TABLE siga5828.Turi;
DROP TABLE siga5828.Song;
DROP TABLE siga5828.Album;
DROP TABLE siga5828.Priklauso;
DROP TABLE siga5828.Artist;
DROP TABLE siga5828.Label;