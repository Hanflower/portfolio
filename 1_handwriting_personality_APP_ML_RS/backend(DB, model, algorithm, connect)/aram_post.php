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
	
	// 이미지
	$image = $_POST["upload"];
	$filename = "IMG".rand().".png";
	file_put_contents("post_img/".$filename, base64_decode($image)); // 실제 서버 폴더에 파일 저장
	
	$writingImage = "http://52.78.75.70/try_aram/post_img/".$filename; // 경로 문자열로 테이블에 저장
	
	
	// 나머지 변수
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : ""; 
	# $userName = isset($_POST["userName"]) ? $_POST["userName"] : ""; # userName 추가
	$postText = isset($_POST["postText"]) ? $_POST["postText"] : "";
    $dailyQuest = isset($_POST["dailyQuest"]) ? $_POST["dailyQuest"] : ""; # 비어있으면 sql에서 0 처리
	$contentTitle = isset($_POST["contentTitle"]) ? $_POST["contentTitle"] : ""; # 유저가 선택한 출처 정보
	$contentWriter = isset($_POST["contentWriter"]) ? $_POST["contentWriter"] : ""; # 유저가 선택한 출처 정보
	
	
	/* 테스트용 (success true 반환)
	# 위에 // 이미지부터 // 나머지 변수까지 전체 주석처리 후. 아래 활성화. 
	$writingImage = "http://10.0.2.2:80/try_aram/post_img/y3.png"; # 수정해야할수도
	$userID = "kim2";
	$userName = "김이";
	$postText = "글글글";
	$dailyQuest = "1"; # 자동 0 처리 확인 완. 
	$contentTitle = "동백꽃";
	$contentWriter = "김유정";
	*/
	
	# 기입한 출처에 맞는 contentID 찾기
	$statement1 = mysqli_prepare($conn, "SELECT contentID FROM CONTENTS WHERE contentTitle = ? and contentWriter = ?");
	mysqli_stmt_bind_param($statement1, "ss", $contentTitle, $contentWriter);
	mysqli_stmt_execute($statement1);
	
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $contentID);
	
	while(mysqli_stmt_fetch($statement1)) {
		$contentID = $contentID; 
	}
	
	
	# 받은 userID의 userName 찾기
	$statement2 = mysqli_prepare($conn, "SELECT userName FROM USER_INFO WHERE userID = ?");
	mysqli_stmt_bind_param($statement2, "s", $userID);
	mysqli_stmt_execute($statement2);
	
	mysqli_stmt_store_result($statement2);
	mysqli_stmt_bind_result($statement2, $userName);
	
	while(mysqli_stmt_fetch($statement2)) {
		$userName = $userName; 
	}
	
	
	# USER_POST에 포스팅 정보 삽입
	$statement3 = mysqli_prepare($conn, "INSERT INTO USER_POST(userID, userName, postText, writingImage, dailyQuest, contentID) VALUES (?,?,?,?,?,?)");
	mysqli_stmt_bind_param($statement3, "ssssii", $userID, $userName, $postText, $writingImage, $dailyQuest, $contentID); 
	mysqli_stmt_execute($statement3);
	
	// 
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//mysqli_close($conn);
	
	// ================================================ //
	// 추가 버전
	// 오늘의 글귀일 경우, 7가지 특성 분석 (aram_insert_feature.php 연동)
	if (($response["success"] == true) and ($dailyQuest == 1)) {
		usleep(100000);
		include('aram_insert_feature.php');
	}
?>