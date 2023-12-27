<?php
	// header("Content-Type:application/json");
	
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
    $userPassword = isset($_POST["userPassword"]) ? $_POST["userPassword"] : ""; 
	
	
	//테스트용 (success true와 유저 정보 반환)
	/*
	$userID = "kim2";
	$userPassword = "kim2";
	*/
	
	
	/* 테스트용 (success false 반환 - 회원가입 안 한 정보이므로 맞음)
	$userID = "kim5";
	$userPassword = "kim5";
	*/
	
	$statement = mysqli_prepare($conn, "SELECT userID FROM USER_INFO WHERE userID = ? AND userPassword = ?");
	mysqli_stmt_bind_param($statement, "ss", $userID, $userPassword); // ss = string, string
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID);
	
	$response = array();
	$response["success"] = false;
	
	while(mysqli_stmt_fetch($statement)) {
		$response["success"] = true;
		$response["userID"] = $userID;
	}
	
	echo json_encode($response);
	
	mysqli_close($conn);
?>