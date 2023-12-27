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
	
	$postID = isset($_POST["postID"]) ? $_POST["postID"] : "";
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
	
	// 테스트용 {"tf": [0]} 또는 {"tf": [1]} 반환
	/*
	$postID = 24;
	$userID = 'kim2';
	*/
	
	$statement = mysqli_prepare($conn, "SELECT EXISTS (SELECT * FROM POST_BOOKMARK WHERE postID = ? AND userID = ?) AS bookmark");
	mysqli_stmt_bind_param($statement, "is", $postID, $userID); 
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $bookmark);

	$response = array();
	
	if($statement){
		while(mysqli_stmt_fetch($statement)) {
			array_push($response, $bookmark);
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