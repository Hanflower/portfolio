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
	
	// 테스트
	/*
	$userID = 'ww';
	*/
	
	/* 결과 예시
	{
		"follower_list": [
			{
				"followerID": "kim2",
				"followerName": "kim2"
			},
			{
				"followerID": "kim3",
				"followerName": "kim3"
			}
		]
	}
	*/

	$statement = mysqli_prepare($conn, "SELECT UI.userID, UI.userName FROM USER_INFO AS UI INNER JOIN USER_FOLLOWING AS UF ON UF.userID = UI.userID WHERE UF.followingID = ?");
	mysqli_stmt_bind_param($statement, "s", $userID); 
	mysqli_stmt_execute($statement);
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $followerID, $followerName);

	
	$response = array();
	
	if($statement){
		while(mysqli_stmt_fetch($statement)) {
			array_push($response,
					array('followerID' => $followerID,
					'followerName' => $followerName
				));
		}
		
		header("Content-Type:application/json; charset=utf8");
		$json = json_encode(array("follower_list"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
		echo $json;
	}
	else {
		echo "error";
	};

	
	mysqli_close($conn);
?>