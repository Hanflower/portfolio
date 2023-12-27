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
	
	/* 테스트용
	$userID = 'kim5'; 
	$postID = 29; // "24", 24 다 가능
	*/
	
	$statement = mysqli_prepare($conn, "DELETE FROM POST_BOOKMARK WHERE postID = ? AND userID = ?");
	mysqli_stmt_bind_param($statement, "is", $postID, $userID); 
	mysqli_stmt_execute($statement);
	
	
	// 조건만족하는 행이 없으면 그냥 삭제를 안할 뿐 success true 반환하므로 필요없을거 같아서. 
	/*
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	*/
	
	//mysqli_close($conn);
?>