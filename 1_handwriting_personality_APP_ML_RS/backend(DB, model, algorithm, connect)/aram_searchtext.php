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
	$searchText = isset($_POST["searchText"]) ? $_POST["searchText"] : "";
	
	// 테스트 : {"success":true} 반환 및 데이터 삽입 확인 완료
	/*
	$userID = 'ww';
	$searchText = '검색어';
	*/

	$statement = mysqli_prepare($conn, "INSERT INTO USER_SEARCH(userID, searchText) VALUES (?,?)");
	mysqli_stmt_bind_param($statement, "ss", $userID, $searchText); 
	mysqli_stmt_execute($statement);
	
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	
	mysqli_close($conn);
?>