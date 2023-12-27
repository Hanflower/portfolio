/*
USER_INFO table 이미 create했다면, 아래 코드로 컬럼 추가
*/
/*
ALTER TABLE USER_INFO
ADD userGender tinyint(1) not null AFTER userEmail;

UPDATE USER_INFO SET `userGender`=1; 
*/
/*
USER_INTEREST table 이미 create했다면, 아래 코드로 컬럼 추가
*/
/*
ALTER TABLE USER_INTEREST ADD PRIMARY KEY(userID, contentTypeID);
*/

CREATE TABLE USER_INFO (
	userID varchar(20) not null,
	userName varchar(20) not null,
	userPassword varchar(20) not null,
	userEmail varchar(100),
	userGender tinyint(1) not null,
	userCharacterTypeO int, 
	userCharacterTypeC int,
	userCharacterTypeE int,
	userCharacterTypeA int,
	userCharacterTypeN int,
	PRIMARY KEY(userID)
);

CREATE TABLE CONTENT_TYPE (
	contentTypeID int not null auto_increment,
	contentTypeName varchar(20) not null,
	PRIMARY KEY(contentTypeID) 
);

CREATE TABLE USER_INTEREST (
	userID varchar(20) not null,
	contentTypeID int not null,
	PRIMARY KEY(userID, contentTypeID),
	FOREIGN KEY(userID)
	REFERENCES USER_INFO(userID) ON UPDATE CASCADE,
	FOREIGN KEY(contentTypeID)
	REFERENCES CONTENT_TYPE(contentTypeID) ON UPDATE CASCADE
);
/*
ON UPDATE CASCADE : 부모 데이터 업데이트 시 자식 데이터도 업데이트
*/

INSERT INTO CONTENT_TYPE(contentTypeName) VALUES ('시');
INSERT INTO CONTENT_TYPE(contentTypeName) VALUES ('소설');
INSERT INTO CONTENT_TYPE(contentTypeName) VALUES ('가사');
