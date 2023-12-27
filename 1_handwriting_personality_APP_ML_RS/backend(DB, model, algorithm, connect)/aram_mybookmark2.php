<?php
	$servername = "localhost"; //서버명
	$username = "aram_temp"; // 사용자명 
	$password = "aram_12345!"; // 비밀번호 
	$dbname = "sample"; // DB명
	
	// MySQL 연결
	$conn = mysqli_connect($servername, $username, $password, $dbname);
	mysqli_query($conn, "SET NAMES utf8"); // 한글 데이터 처리
	
	// 연결 체크
	if(!$conn) {
		die("연결 오류 : ".mysqli_connect_error());
	}
	
	// 아이디 받기
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
	
	/* 테스트 : 북마크한게 없는 유저는 빈 내용으로 반환 {"posts": [] }
	$userID = "ww";
	*/
	
	
	# INNER JOIN 2개. 하나는 북마크기록이 있는 USER_POST 기록을 찾기 위함. 하나는 contentID로부터 Title, Writer 연결해서 출력하기 위함. 
	$statement = mysqli_prepare($conn, "
WITH emoticon_table AS 
(
    SELECT postID,
    	SUM(CASE WHEN emoticon=4 THEN 1 ELSE 0 END) AS 'love',
		SUM(CASE WHEN emoticon=3 THEN 1 ELSE 0 END) AS 'surprise',
		SUM(CASE WHEN emoticon=2 THEN 1 ELSE 0 END) AS 'angry',
        SUM(CASE WHEN emoticon=1 THEN 1 ELSE 0 END) AS 'sad'
	FROM POST_EMOTICON
	GROUP BY postID
),
books AS (SELECT postID FROM POST_BOOKMARK WHERE userID = ?)

SELECT userID, userName, postText, writingImage, postDate, contentTitle, contentWriter, USER_POST.postID,
		IFNULL((SELECT love FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS love,
		IFNULL((SELECT surprise FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS surprise,
		IFNULL((SELECT angry FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS angry,
        IFNULL((SELECT sad FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS sad,
        (SELECT COUNT(*) FROM POST_BOOKMARK b WHERE b.postID = USER_POST.postID) AS num_of_bookmark,
		(SELECT EXISTS (SELECT * FROM POST_BOOKMARK bb WHERE userID = ? AND (bb.postID = USER_POST.postID))) as bookmarked,
        IFNULL((SELECT emoticon FROM POST_EMOTICON ee WHERE ee.userID = ? AND (ee.postID = USER_POST.postID)), 0) AS emoticoned,
		(IF(USER_POST.userID = ?, 1, 0)) as mine
FROM USER_POST INNER JOIN books ON USER_POST.postID = books.postID INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID
ORDER BY postDate DESC;");
	mysqli_stmt_bind_param($statement, "ssss", $userID, $userID, $userID, $userID); 
	mysqli_stmt_execute($statement);
	
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID, $userName, $postText, $writingImage, $postDate, $contentTitle, $contentWriter, $postID, $love, $surprise, $angry, $sad, $num_of_bookmark, $bookmarked, $emoticoned, $mine);
	
	$response = array();
	
	while(mysqli_stmt_fetch($statement)) {
		array_push($response,
				array('userID' => $userID,
				'userName' => $userName,
				'postText' => $postText,
				'writingImage' => $writingImage,
				'postDate' => $postDate,
				'contentTitle' => $contentTitle,
				'contentWriter' => $contentWriter,
				'postID' => $postID,
				'love' => $love,
				'surprise' => $surprise,
				'angry' => $angry,
				'sad' => $sad,
				'num_of_bookmark' => $num_of_bookmark,
				'bookmarked' => $bookmarked,
				'emoticoned' => $emoticoned,
				'mine' => $mine
			));
	}
	
	header("Content-Type:application/json; charset=utf8");
	$json = json_encode(array("posts"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
	echo $json;
	
	mysqli_close($conn);
?>