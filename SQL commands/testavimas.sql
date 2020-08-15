--check testas (albumas turi buti isleistas veliau nei 1900)
INSERT INTO siga5828.Album VALUES(505, '11:11:11', 'zanras', 'pavadinimas', '1899');

--check testas (dainos BPM turi buti tarp 20 ir 500)
INSERT INTO siga5828.Song VALUES(1007, '11:11:11' ,'pavadinimas', 10);

--testuojamas pirmas trigeris, kuris neleidzia keisti atlikejo vardo jeigu jis jau priklauso kazkuriai irasu bendrovei
UPDATE Artist
SET pavadinimas='Autekkkkkre'
WHERE pavadinimas='Autechre';

--testuojamas antras trigeris, kuris neleidzia atlikejui suteikti daugiau nei 3 irasu bendroviu (ERROR:  Virsytas irasu bendroviu skaicius)
INSERT INTO siga5828.Priklauso VALUES(2, 105);

--unique index testas, sukuriama nauja irasu bendrove su tokiu paciu pavadinimu
INSERT INTO siga5828.Label VALUES(106, 'FFRR', '1986', DEFAULT);