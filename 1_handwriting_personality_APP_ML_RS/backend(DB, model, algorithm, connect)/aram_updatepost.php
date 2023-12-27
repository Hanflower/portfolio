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
	
	// 아이디 받기
	$postID = isset($_POST["postID"]) ? $_POST["postID"] : "";
	
	// 이미지 제외한 나머지 변수
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : ""; 
	//$userName = isset($_POST["userName"]) ? $_POST["userName"] : ""; # userName 추가
	$postText = isset($_POST["postText"]) ? $_POST["postText"] : "";
    //$dailyQuest = isset($_POST["dailyQuest"]) ? $_POST["dailyQuest"] : ""; # 비어있으면 sql에서 0 처리
	$contentTitle = isset($_POST["contentTitle"]) ? $_POST["contentTitle"] : ""; # 유저가 선택한 출처 정보
	$contentWriter = isset($_POST["contentWriter"]) ? $_POST["contentWriter"] : ""; # 유저가 선택한 출처 정보
	
	/* 테스트 : 
	$postID = "5";
	$userID = "kim2";
	$postText = "수정했어요";
	//$dailyQuest = 1;
	$contentTitle = "동백꽃";
	$contentWriter = "김유정";
	*/
	
	
	# 받은 userID의 userName 찾기
	$statement1 = mysqli_prepare($conn, "SELECT userName FROM USER_INFO WHERE userID = ?");
	mysqli_stmt_bind_param($statement1, "s", $userID);
	mysqli_stmt_execute($statement1);
	
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $userName);
	
	while(mysqli_stmt_fetch($statement1)) {
		$userName = $userName; 
	}
	
	
	# 기입한 출처에 맞는 contentID 찾기
	$statement2 = mysqli_prepare($conn, "SELECT contentID FROM CONTENTS WHERE contentTitle = ? and contentWriter = ?");
	mysqli_stmt_bind_param($statement2, "ss", $contentTitle, $contentWriter);
	mysqli_stmt_execute($statement2);
	
	mysqli_stmt_store_result($statement2);
	mysqli_stmt_bind_result($statement2, $contentID);
	
	while(mysqli_stmt_fetch($statement2)) {
		$contentID = $contentID; 
	}
	
	
	# 기존 경로 찾기 -> 이미지 새로 덮어쓰기
	$statement3 = mysqli_prepare($conn, "SELECT writingImage FROM USER_POST WHERE postID = ?");
	mysqli_stmt_bind_param($statement3, "i", $postID);
	mysqli_stmt_execute($statement3);
	
	mysqli_stmt_store_result($statement3);
	mysqli_stmt_bind_result($statement3, $writingImage);
	
	while(mysqli_stmt_fetch($statement3)) {
		$writingImage = $writingImage; 
	}
	
	// 테스트 (php만 테스트할 때 이미지 관련 아래 3줄 주석처리해야함)
	//echo $writingImage; 
	
	$image = $_POST["upload"];
	$filename = substr($writingImage, 37); # post_img/이후 ~.png 부분
	file_put_contents("post_img/".$filename, base64_decode($image)); // 실제 서버 폴더에 파일 저장 (덮어쓰기)
	
	
	# 받은 postID에 해당하는 post 수정
	$statement = mysqli_prepare($conn, "UPDATE USER_POST SET userID = ?, userName = ?, postText = ?, contentID = ? WHERE postID = ?");
	mysqli_stmt_bind_param($statement, "sssii", $userID, $userName, $postText, $contentID, $postID); 
	mysqli_stmt_execute($statement);
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	mysqli_close($conn);
?>