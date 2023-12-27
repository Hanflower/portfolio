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
	
	/* 테스트 : 올린 글이 없는 유저는 빈 내용으로 반환 {"posts": [] }
	$userID = "ww";
	*/
	
	# getpost랑 다른 점 : 받은 아이디 정보에 해당하는 것만 가져오기 
	$statement = mysqli_prepare($conn, "SELECT userID, userName, postText, writingImage, postDate, contentTitle, contentWriter FROM USER_POST INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID WHERE userID = ? ORDER BY postDate DESC");
	mysqli_stmt_bind_param($statement, "s", $userID); 
	mysqli_stmt_execute($statement);
	
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID, $userName, $postText, $writingImage, $postDate, $contentTitle, $contentWriter);
	
	$response = array();
	
	while(mysqli_stmt_fetch($statement)) {
		array_push($response,
				array('userID' => $userID,
				'userName' => $userName,
				'postText' => $postText,
				'writingImage' => $writingImage,
				'postDate' => $postDate,
				'contentTitle' => $contentTitle,
				'contentWriter' => $contentWriter
			));
	}
	
	header("Content-Type:application/json; charset=utf8");
	$json = json_encode(array("posts"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
	echo $json;
	
	mysqli_close($conn);
?>