<?php

	header("Content-Type:text/html;charset=utf-8");
	
	// 안드에서 userID 받아오기
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : ""; 
	
	/* test
	$userID = "aaa";
	*/
	
	/* 결과 예시
	{
		"big5": {
			"userCharacterTypeO": "1",
			"userCharacterTypeC": "0",
			"userCharacterTypeE": "0",
			"userCharacterTypeA": "1",
			"userCharacterTypeN": "1"
		}
	}
	*/
	
	// USER_ANALYSIS 테이블에서 해당 userID의 지금까지 데이터 가져오기
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
	
	$statement1 = mysqli_prepare($conn, "SELECT * FROM USER_ANALYSIS WHERE userID = ?");
	mysqli_stmt_bind_param($statement1, "s", $userID); 
	mysqli_stmt_execute($statement1);
	
	//
	$response = array();
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $userID, $pressure, $size, $wordSpacing, $lineSpacing, $slant, $baseline, $speed); 
	
	while(mysqli_stmt_fetch($statement1)) {
		array_push($response,
				array(
				$size,
				$slant,
				$wordSpacing,
				$pressure,
				$baseline,
				$lineSpacing,
				$speed
		));
	}
	
	$feature_data = json_encode($response, JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
	$feature_string = preg_replace("/\s+/", "", $feature_data); // 공백 제거
	
	
	# 받은 userID의 userGender 찾기
	$statement2 = mysqli_prepare($conn, "SELECT userGender FROM USER_INFO WHERE userID = ?");
	mysqli_stmt_bind_param($statement2, "s", $userID);
	mysqli_stmt_execute($statement2);
	
	mysqli_stmt_store_result($statement2);
	mysqli_stmt_bind_result($statement2, $userGender);
	
	while(mysqli_stmt_fetch($statement2)) {
		$userGender = $userGender; 
	}
	
	// 파이썬에 넘기고 OCEAN 값 받아오기
	exec("C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe py_script\aram_big5.py {$feature_string} {$userGender}", $output); # 2>&1
	
	// USER_INFO에 OCEAN 저장
	$statement3 = mysqli_prepare($conn, "UPDATE `USER_INFO` SET `userCharacterTypeO`=?,`userCharacterTypeC`=?,`userCharacterTypeE`=?,`userCharacterTypeA`=?,`userCharacterTypeN`=? WHERE userID = ?");
	mysqli_stmt_bind_param($statement3, "iiiiis", $output[0], $output[1], $output[2], $output[3], $output[4], $userID); 
	mysqli_stmt_execute($statement3);
	
	// 안드에 결과 출력 (분석탭에 출력)	
	$response = array(
		'userCharacterTypeO' => $output[0],
		'userCharacterTypeC' => $output[1],
		'userCharacterTypeE' => $output[2],
		'userCharacterTypeA' => $output[3],
		'userCharacterTypeN' => $output[4],
	);
	
	header("Content-Type:application/json; charset=utf8");
	$json = json_encode(array("big5"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
	echo $json;
	
	//mysqli_close($conn);
?>