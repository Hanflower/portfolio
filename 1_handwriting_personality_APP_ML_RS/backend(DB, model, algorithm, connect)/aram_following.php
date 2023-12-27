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
	$followingID = isset($_POST["followingID"]) ? $_POST["followingID"] : "";
	
	
	/* 테스트용 : userID 또는 followingID가 USER_INFO에 없으면 오류 반환 / userID & followingID 조합이 이미 테이블에 있으면 = 이미 해당 유저가 다른 특정 유저를 팔로잉한 기록이 있으면 역시 오류 반환
	$userID = 'kim3'; 
	$followingID = 'kim1111'; 
	*/
	
	$statement = mysqli_prepare($conn, "INSERT INTO USER_FOLLOWING(userID, followingID) VALUES (?,?)");
	mysqli_stmt_bind_param($statement, "ss", $userID, $followingID); 
	mysqli_stmt_execute($statement);
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//mysqli_close($conn);
?>