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
	
	/* 결과 예시
	{
		"users": [
			{
				"userID": "dddd",
				"userName": "dddd"
			},
			{
				"userID": "kim2",
				"userName": "kim2"
			},
			{
				"userID": "kim3",
				"userName": "kim3"
			},
			{
				"userID": "kim4",
				"userName": "kim4"
			},
			{
				"userID": "ww",
				"userName": "ww"
			},
			{
				"userID": "eeee",
				"userName": "이이"
			}
		]
	}
	*/

	
	$statement = mysqli_query($conn, "SELECT userID, userName FROM USER_INFO ORDER BY userName");

	$response = array();
	
	if($statement){
		while($row = mysqli_fetch_array($statement)) {
			array_push($response,
					array('userID' => $row[0],
					'userName' => $row[1]
				));
		}
		
		header("Content-Type:application/json; charset=utf8");
		$json = json_encode(array("users"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
		echo $json;
	}
	else {
		echo "error";
	};
	
	
	mysqli_close($conn);
?>