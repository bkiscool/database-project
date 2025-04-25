mysql -u blkey -pielee1Ea<<EOFMYSQL
use blkey;
show tables;

DROP TABLE IF EXISTS RA;
DROP TABLE IF EXISTS Assignment;
DROP TABLE IF EXISTS Room;
DROP TABLE IF EXISTS Building;
DROP TABLE IF EXISTS Student;

CREATE TABLE Building (buildingID CHAR(3) PRIMARY KEY, name CHAR(20) NOT NULL, address CHAR(20) NOT NULL, hasAC BOOL, hasDining BOOL);
CREATE TABLE Room (buildingID CHAR(3), roomNumber CHAR(3), numBedrooms INT NOT NULL, privateBathrooms BOOL, hasKitchen BOOL, PRIMARY KEY (buildingID, roomNumber), FOREIGN KEY (buildingID) REFERENCES Building(buildingID) ON DELETE CASCADE ON UPDATE CASCADE);
CREATE TABLE Student (studentID CHAR(5) PRIMARY KEY, name CHAR(25), wantsAC BOOL, wantsDining BOOL, wantsPrivateBathroom BOOL);
CREATE TABLE Assignment (studentID CHAR(5) PRIMARY KEY, buildingID CHAR(3), roomNumber CHAR(3), FOREIGN KEY (buildingID, roomNumber) REFERENCES Room(buildingID, roomNumber) ON DELETE CASCADE ON UPDATE CASCADE, FOREIGN KEY (studentID) REFERENCES Student(studentID) ON DELETE CASCADE ON UPDATE CASCADE);
CREATE TABLE RA (studentID CHAR(5) PRIMARY KEY, stipend DOUBLE(5, 2), emergencyContact CHAR(14), FOREIGN KEY (studentID) REFERENCES Student(studentID) ON DELETE CASCADE ON UPDATE CASCADE);

INSERT INTO Building VALUES ("001", "Theodora's", "777 Bathroom Way", 0, 0);
INSERT INTO Building VALUES ("002", "Walton Hall", "324 N Stadium Dr", 1, 0);
INSERT INTO Building VALUES ("003", "Pomfret Hall", "31 S Stadium Dr", 1, 1);

INSERT INTO Room VALUES ("001", "001", 1, 1, 0);
INSERT INTO Room VALUES ("001", "100", 1, 1, 0);
INSERT INTO Room VALUES ("001", "101", 1, 1, 0);

INSERT INTO Room VALUES ("002", "100", 1, 0, 0);
INSERT INTO Room VALUES ("002", "200", 1, 0, 0);
INSERT INTO Room VALUES ("002", "201", 1, 0, 0);

INSERT INTO Room VALUES ("003", "200", 3, 1, 1);
INSERT INTO Room VALUES ("003", "201", 3, 1, 1);
INSERT INTO Room VALUES ("003", "300", 4, 1, 1);

INSERT INTO Student VALUES ("00001", "Lord Frank", 0, 0, 1);
INSERT INTO Student VALUES ("00002", "Nico Robin", 1, 0, 0);
INSERT INTO Student VALUES ("00003", "Weiss Schnee", 1, 1, 1);

INSERT INTO Assignment VALUES ("00001", "001", "001");

INSERT INTO RA VALUES ("00001", 450.00, "(222) 222-2222");

EOFMYSQL
