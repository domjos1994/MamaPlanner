CREATE TABLE IF NOT EXISTS family (
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    birthDate DATE NOT NULL,
    gender VARCHAR(1) NOT NULL,
    profilePicture BLOB DEFAULT NULL,
    color INTEGER DEFAULT 0,
    timeStamp INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS events (
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    family INTEGER DEFAULT 0,
    start INTEGER NOT NULL,
    end INTEGER default 0,
    timeStamp INTEGER NOT NULL,
    FOREIGN KEY(family) REFERENCES family(ID) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS notifications (
    ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    months INTEGER DEFAULT 0,
    days INTEGER DEFAULT 0,
    hours INTEGER DEFAULT 0,
    event INTEGER NOT NULL,
    timeStamp INTEGER NOT NULL,
    FOREIGN KEY(event) REFERENCES events(ID) ON DELETE CASCADE ON UPDATE CASCADE
);