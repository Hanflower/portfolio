<?php

	header("Content-Type:text/html;charset=utf-8");
	
	// 1-1. 안드에서 넘어오는 값 (안드에서 request를 두번 각각) (보류)
	//$userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
	//$filePath = isset($_POST["writingImage"]) ? $_POST["writingImage"] : "";
	
	// 1-2. aram_posh.php에서 exec()로 넘어온다면 - USER_ANALYSIS 삽입 실패
	//print_r($argv);
	//$userID = $argv[1];
	//$filePath = "C:/xampp/htdocs/try_aram/post_img/".substr($argv[2], 37); # writingImage http://10.0.2.2:80/try_aram/post_img/y2.png 에서 앞부분 변경해야함
	
	// 1-3. aram_post.php의 include()로 실행된다면. $userID는 그대로 사용 (이 방식 선택)
	
	$filePath = "C:/xampp/htdocs/try_aram/post_img/".substr($writingImage, 37);
	
	// 타이머 측정값 받기
	$time = isset($_POST["time"]) ? $_POST["time"] : "";
	
	/* test
	$time = 22.0;
	*/
	
	//파이썬 스크립트 실행
	exec("C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe py_script\aram_insert_feature.py {$filePath} {$time}", $output);
	
	## USER_ANALYSIS 테이블에 삽입
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
	
	$statement = mysqli_prepare($conn, "INSERT INTO USER_ANALYSIS(userID, pressure, `size`, wordSpacing, lineSpacing, slant, baseline, speed) VALUES (?,?,?,?,?,?,?,?)");
	mysqli_stmt_bind_param($statement, "sddddddd", $userID, $output[3], $output[0], $output[2], $output[5], $output[1], $output[4], $output[6]); # float 'f' 없는 듯. double의 'd'만 있는 듯.
	mysqli_stmt_execute($statement);
		
	//
	$response = array();
	$response["success_analysis"] = true;
	
	echo json_encode($response);

	
	//mysqli_close($conn);
?>