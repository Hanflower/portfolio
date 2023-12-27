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
	$userName = isset($_POST["userName"]) ? $_POST["userName"] : "";
    $userPassword = isset($_POST["userPassword"]) ? $_POST["userPassword"] : "";
    $userEmail = isset($_POST["userEmail"]) ? $_POST["userEmail"] : ""; 
	$userGender = isset($_POST["userGender"]) ? $_POST["userGender"] : ""; 
	
	
	/* 테스트용 (success true 반환)
	$userID = 'kim5';
	$userName = '김오';
	$userPassword = 'kim5';
	$userEmail = 'kim5@naver.com';
	$userGender = 1;
	*/
	
	$statement = mysqli_prepare($conn, "INSERT INTO USER_INFO(userID, userName, userPassword, userEmail, userGender) VALUES (?,?,?,?,?)");
	mysqli_stmt_bind_param($statement, "ssssi", $userID, $userName, $userPassword, $userEmail, $userGender); 
	mysqli_stmt_execute($statement);
	
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//mysqli_close($conn);
?>