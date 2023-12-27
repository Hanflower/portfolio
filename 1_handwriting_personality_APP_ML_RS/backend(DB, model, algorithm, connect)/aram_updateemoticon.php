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
	
	
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
	$postID = isset($_POST["postID"]) ? $_POST["postID"] : "";
	$emoticon = isset($_POST["emoticon"]) ? $_POST["emoticon"] : "";
	
	
	/* 테스트용 : postID가 USER_POST에 없으면 오류 반환 / userID & postID 조합이 이미 테이블에 있으면 = 이미 해당 유저가 해당 글에 남긴 이모티콘 기록이 있으면 역시 오류 반환
	$userID = 'ww';
	$postID = 1;
	$emoticon = 2;
	*/
	
	$statement = mysqli_prepare($conn, "UPDATE POST_EMOTICON SET emoticon = ? WHERE postID = ? AND userID = ?");
	mysqli_stmt_bind_param($statement, "iis", $emoticon, $postID, $userID);  
	mysqli_stmt_execute($statement);
	
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//mysqli_close($conn);
?>