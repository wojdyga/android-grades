-- Table 'Studenci'
-- 
-- ---
-- ---

DROP TABLE IF EXISTS 'Studenci';
		
CREATE TABLE 'Studenci' (
  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,
  'Nazwisko' CHAR(40) NOT NULL DEFAULT 'NULL',
  'Imie' CHAR(30) NOT NULL DEFAULT 'NULL',
  'NrIndeksu' CHAR(10) NULL DEFAULT NULL,
  'KluczSkrzynki' CHAR(8) NULL DEFAULT NULL
);

-- ---
-- Table 'Grupa'
-- 
-- ---

DROP TABLE IF EXISTS 'Grupa';
		
CREATE TABLE 'Grupa' (
  'id' INTEGER NULL  PRIMARY KEY DEFAULT NULL,
  'Przedmiot' CHAR(50) NULL DEFAULT NULL,
  'Czas' CHAR(16) NULL DEFAULT NULL,
  'Miejsce' CHAR(16) NULL DEFAULT NULL,
  'GrupaDziek' CHAR(16) NOT NULL DEFAULT NULL
);

-- ---
-- Table 'StudentWGrupie'
-- 
-- ---

DROP TABLE IF EXISTS 'StudentWGrupie';
		
CREATE TABLE 'StudentWGrupie' (
  'id' INTEGER NULL  PRIMARY KEY DEFAULT NULL,
  'IdStudenta' INTEGER NULL DEFAULT NULL REFERENCES 'Studenci' ('id'),
  'IdGrupy' INTEGER NULL DEFAULT NULL REFERENCES 'Grupa' ('id')
);

-- ---
-- Table 'Oceny'
-- 
-- ---

DROP TABLE IF EXISTS 'Oceny';
		
CREATE TABLE 'Oceny' (
  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,
  'Tresc' CHAR(30) NOT NULL DEFAULT 'NULL',
  'IdGrupy' INTEGER NULL DEFAULT NULL REFERENCES 'Grupa' ('id')
);

-- ---
-- Table 'OcenyStudenta'
-- 
-- ---

DROP TABLE IF EXISTS 'OcenyStudenta';
		
CREATE TABLE 'OcenyStudenta' (
  'id' INTEGER NULL PRIMARY KEY DEFAULT NULL,
  'Wartosc' CHAR(16) NOT NULL DEFAULT 'NULL',
  'Data' TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  'IdOceny' INTEGER NULL DEFAULT NULL REFERENCES 'Oceny' ('id'),
  'IdStudenta' INTEGER NULL DEFAULT NULL REFERENCES 'Studenci' ('id')
);

DROP VIEW IF EXISTS GrupyView;

CREATE VIEW GrupyView AS 
SELECT id AS _id, 
	GrupaDziek||' '||Przedmiot||' '||Czas AS Nazwa 
FROM Grupa;

DROP VIEW IF EXISTS GrupyStudentaView;

CREATE VIEW GrupyStudentaView AS SELECT 
    Grupa.id AS _id, 
    GrupaDziek||' '||Przedmiot||' '||Czas AS Nazwa, 
    Studenci.id AS idS,
    (SELECT COUNT(StudentWGrupie.id) 
     FROM StudentWGrupie 
     WHERE Grupa.id=StudentWGrupie.idGrupy 
    	AND Studenci.id=StudentWGrupie.idStudenta) AS Jest 
FROM Grupa, Studenci; 

DROP VIEW IF EXISTS StudenciView;

CREATE VIEW StudenciView AS
SELECT S.Id AS _id,
	S.Nazwisko||' '||S.Imie AS Nazwa,
	GROUP_CONCAT((SELECT grupadziek FROM grupa WHERE grupa.id=swg.idgrupy)) AS Grupy
FROM Studenci S
LEFT OUTER JOIN StudentWGrupie SWG ON S.id=SWG.IdStudenta
GROUP BY _id;


--SELECT IFNULL(OS.id,-1) _id, Oceny.Tresc, OS.Wartosc, OS.IdStudenta, Oceny.id FROM Oceny LEFT OUTER JOIN OcenyStudenta OS ON OS.idOceny=Oceny.id WHERE Oceny.IdGrupy=?
