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
	
	/* 테스트
	phpmyadmin 페이지에서 USER_POST 임시로 삽입 (또는 aram_post.php test 먼저 실행해서 값을 삽입하고, 그 내용이 출력되는지 확인)
{
    "posts": [
        {
            "userID": "ww",
            "userName": "ww",
            "postText": "하하하",
            "writingImage": "http:\/\/10.0.2.2:80\/try_aram\/post_img\/y3.png",
            "postDate": "2023-05-19 13:15:44",
            "contentTitle": "허생전(許生傳)",
            "contentWriter": "채만식",
            "postID": 2,
            "love": "0",
            "surprise": "0",
            "angry": "0",
            "sad": "1",
            "num_of_bookmark": 0,
            "bookmarked": 0,
            "emoticoned": 0,
            "mine": 1
        },
        {
            "userID": "kim2",
            "userName": "kim2",
            "postText": "수정했어요",
            "writingImage": "http:\/\/10.0.2.2:80\/try_aram\/post_img\/y2.png",
            "postDate": "2023-05-19 09:07:02",
            "contentTitle": "동백꽃",
            "contentWriter": "김유정",
            "postID": 5,
            "love": "0",
            "surprise": "1",
            "angry": "0",
            "sad": "0",
            "num_of_bookmark": 1,
            "bookmarked": 1,
            "emoticoned": 3,
            "mine": 0
        },
	*/
	
	# 0609 해당 유저의 북마크 여부(bookmarked) 추가로 반환 + 0612 해당 유저 글 여부(글 수정&삭제 버튼 노출 여부 결정하려고) + 0613 해당 유저의 이모티콘 기록(emoticoned) 추가로 반환
	/*
	이모티콘 4(love) 3(surprise) 2(angry) 1(sad) 순 배치
	emoticoned 컬럼이 0을 반환하면 기록이 없다는 것. null.
	*/
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
)

SELECT userID, userName, postText, writingImage, postDate, contentTitle, contentWriter, postID,
		IFNULL((SELECT love FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS love,
		IFNULL((SELECT surprise FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS surprise,
		IFNULL((SELECT angry FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS angry,
        IFNULL((SELECT sad FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS sad,
        (SELECT COUNT(*) FROM POST_BOOKMARK b WHERE b.postID = USER_POST.postID) AS num_of_bookmark,
		(SELECT EXISTS (SELECT * FROM POST_BOOKMARK bb WHERE userID = ? AND (bb.postID = USER_POST.postID))) as bookmarked,
        IFNULL((SELECT emoticon FROM POST_EMOTICON ee WHERE ee.userID = ? AND (ee.postID = USER_POST.postID)), 0) AS emoticoned,
		(IF(USER_POST.userID = ?, 1, 0)) as mine
FROM USER_POST INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID
ORDER BY postDate DESC;");
	mysqli_stmt_bind_param($statement, "sss", $userID, $userID, $userID); 
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