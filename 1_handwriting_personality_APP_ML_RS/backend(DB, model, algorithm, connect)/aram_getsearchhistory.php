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
	
	// 테스트용 
	/*
	$userID = 'ww';
	*/
	
	/* 반환 결과
	{
		"search": [
			{
				"searchText": "ff",
				"searchDate": "2023-08-08 12:25:52"
			},
			{
				"searchText": "ee",
				"searchDate": "2023-08-08 12:25:12"
			},
			{
				"searchText": "dd",
				"searchDate": "2023-08-08 12:22:54"
			},
			{
				"searchText": "cc",
				"searchDate": "2023-08-08 12:22:21"
			},
			{
				"searchText": "bb",
				"searchDate": "2023-08-08 12:00:09"
			}
		]
	}
	*/


	$statement = mysqli_prepare($conn, "SELECT searchText, searchDate FROM USER_SEARCH WHERE userID = ? ORDER BY searchDate DESC LIMIT 5");
	mysqli_stmt_bind_param($statement, "s", $userID); 
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $searchText, $searchDate);

	$response = array();
	
	if($statement){
		while(mysqli_stmt_fetch($statement)) {
			array_push($response,
				array('searchText' => $searchText,
				'searchDate' => $searchDate
			));
		}
		
		header("Content-Type:application/json; charset=utf8");
		$json = json_encode(array("search"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
		echo $json;
	}
	else {
		echo "error";
	};
	
	
	mysqli_close($conn);
?>