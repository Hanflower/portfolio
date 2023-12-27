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
	
	/* 테스트용 (중복아니였으므로, success true 반환)
	$userID = "kim1";
	*/
	
	/* 테스트용 (중복이므로, success false 반환)
	$userID = "kim2";
	*/
	
	$statement = mysqli_prepare($conn, "SELECT userID FROM USER_INFO WHERE userID = ?");
	mysqli_stmt_bind_param($statement, "s", $userID); 
	mysqli_stmt_execute($statement);
	
	
	//
	$response = array();
	$response["success"] = true;
	
	while(mysqli_stmt_fetch($statement)){
		$response["success"] = false;
		# $response["userID"] = $userID;
	}
	
	echo json_encode($response);
	
	//mysqli_close($conn);
?>