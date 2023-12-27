<?php

	header("Content-Type:text/html;charset=utf-8"); // 0609 이거 차이인가. 어차피 밑에 output 받으면 이거 설정 문장 있는데..
	
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
	
	
	// 안드에서 userID 받아오기
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : ""; 
	
	/* 테스트 (필요한 유저 활동 데이터가 있어야함)
	$userID = 'kim5';
	*/
	
	# recent_contentID
	$recent_contentID = '';
	$statement1 = mysqli_prepare($conn, "SELECT contentID FROM USER_POST WHERE userID = ? ORDER BY postDate DESC LIMIT 1");
	mysqli_stmt_bind_param($statement1, "s", $userID);  
	mysqli_stmt_execute($statement1);
	
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $contentID); 
	
	while(mysqli_stmt_fetch($statement1)) {
		$recent_contentID = $contentID;
	}
	
	//echo "recent_contentID : ".$recent_contentID; // 확인용
	
	if (empty($recent_contentID)) {
		include('aram_getpost2.php');
	}
	
	else {
		# interest_typeID
		$statement2 = mysqli_prepare($conn, "SELECT contentTypeID FROM USER_INTEREST WHERE userID = ?");
		mysqli_stmt_bind_param($statement2, "s", $userID);  
		mysqli_stmt_execute($statement2);
		
		$interest_typeID = array();
		mysqli_stmt_store_result($statement2);
		mysqli_stmt_bind_result($statement2, $contentTypeID); 
		
		while(mysqli_stmt_fetch($statement2)) {
			array_push($interest_typeID, $contentTypeID);
		}
		
		$interest_typeList = json_encode($interest_typeID);
		
		//echo "interest_typeID : ".$interest_typeList; // 확인용
		
		
		// 파이썬에 넘기고 정렬된 post json 받아오기
		//exec("C:\Users\ujin2\AppData\Local\Programs\Python\Python39\python.exe py_script\aram_recommend_contentbased.py {$userID} {$recent_contentID} {$interest_typeList}", $output); #  2>&1
		
		// 0609 여기도 맞춰서 바꿔봄. 일단 바꿔도 출력되는거 확인
		$command = "C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe py_script\aram_recommend_contentbased2.py {$userID} {$recent_contentID} {$interest_typeList} 2>&1";
		$output = array();
		exec($command, $output);
		
		
		header("Content-Type:application/json; charset=utf8");
		$post_sort = json_encode(array("posts"=>json_decode($output[0])), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
			
		echo $post_sort;
		
		mysqli_close($conn);
	}
	
	
?>