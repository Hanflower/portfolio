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
	$searchDate = isset($_POST["searchDate"]) ? $_POST["searchDate"] : "";
	
	/* 테스트 : {"success":true} 반환 및 삭제되는거 확인 
	$userID = "ww";
	$searchDate = "2023-08-09 08:06:25";
	*/
	
	# 해당 검색 기록 하나 삭제
	$statement = mysqli_prepare($conn, "DELETE FROM USER_SEARCH WHERE userID = ? AND searchDate = ?");
	mysqli_stmt_bind_param($statement, "ss", $userID, $searchDate); 
	mysqli_stmt_execute($statement);
	
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	mysqli_close($conn);
?>