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
	
	// 테스트용 {"tf": [0]} 또는 {"tf": [1]} 반환
	/*
	$userID = 'kim4';
	$followingID = 'kim2';
	*/

	$statement = mysqli_prepare($conn, "SELECT EXISTS (SELECT * FROM USER_FOLLOWING WHERE userID = ? AND followingID = ?) AS following");
	mysqli_stmt_bind_param($statement, "ss", $userID, $followingID); 
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $following);

	$response = array();
	
	if($statement){
		while(mysqli_stmt_fetch($statement)) {
			array_push($response, $following);
		}
		
		header("Content-Type:application/json; charset=utf8");
		$json = json_encode(array("tf"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
		echo $json;
	}
	else {
		echo "error";
	};
	
	
	mysqli_close($conn);
?>