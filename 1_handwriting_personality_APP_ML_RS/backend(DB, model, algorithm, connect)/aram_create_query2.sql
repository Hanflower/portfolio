
/*
UNIQUE 제약 조건 추가
*/
ALTER TABLE CONTENT_TYPE
MODIFY COLUMN contentTypeName varchar(20) unique;

/*
USER_POST table 이미 create했다면, 아래 코드로 컬럼 추가
*/
/*
ALTER TABLE USER_POST
ADD userName varchar(20) not null AFTER userID;
*/
/*
CONTENTS table 이미 create했따면, 아래 코드로 컬럼 삭제
*/
/*
ALTER TABLE CONTENTS DROP COLUMN contentFulltext;
*/


CREATE TABLE CONTENTS(
	contentID int not null auto_increment,
	contentTitle varchar(100) not null,
	contentWriter varchar(30) not null,
	contentTypeID int not null,
	PRIMARY KEY(contentID),
	FOREIGN KEY(contentTypeID)
	REFERENCES CONTENT_TYPE(contentTypeID) ON UPDATE CASCADE
);

CREATE TABLE USER_POST(
	postID int not null auto_increment,
	userID varchar(20) not null,
	userName varchar(20) not null,
	postText varchar(100) not null,
	writingImage varchar(250) not null unique,
	postDate datetime not null default now(),
	dailyQuest tinyint(1) not null default 0,
	contentID int not null,
	PRIMARY KEY(postID),
	FOREIGN KEY(userID)
	REFERENCES USER_INFO(userID) ON UPDATE CASCADE,
	FOREIGN KEY(contentID)
	REFERENCES CONTENTS(contentID) ON UPDATE CASCADE
);
/*
	FOREIGN KEY(userName)
	REFERENCES USER_INFO(userName) ON UPDATE CASCADE,
*/

CREATE TABLE USER_ANALYSIS(
	userID varchar(20) not null,
	pressure float not null,
	`size` float not null,
	wordSpacing float not null,
	lineSpacing float not null,
	slant float not null,
	baseline float not null,
	speed float not null,
	FOREIGN KEY(userID)
	REFERENCES USER_INFO(userID) ON UPDATE CASCADE
);
