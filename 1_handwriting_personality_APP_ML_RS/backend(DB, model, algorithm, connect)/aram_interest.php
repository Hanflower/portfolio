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
	$contentTypeName1 = isset($_POST["contentTypeName1"]) ? $_POST["contentTypeName1"] : "";
	$contentTypeName2 = isset($_POST["contentTypeName2"]) ? $_POST["contentTypeName2"] : "";
	$contentTypeName3 = isset($_POST["contentTypeName3"]) ? $_POST["contentTypeName3"] : "";
	
	
	/* 테스트용 (success true 반환)
	$userID = "kim3";
	$contentTypeName1 = "";
	$contentTypeName2 = "";
	$contentTypeName3 = "소설";
	*/
	
	// 각 contentTypeName에 맞는 contentTypeID 가져오기
	$statement1 = mysqli_prepare($conn, "SELECT contentTypeID FROM CONTENT_TYPE WHERE contentTypeName IN (?, ?, ?)");
	mysqli_stmt_bind_param($statement1, "sss", $contentTypeName1, $contentTypeName2, $contentTypeName3);
	mysqli_stmt_execute($statement1);
	
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $contentTypeID);
	

	$idResponse = array();
	while(mysqli_stmt_fetch($statement1)) {
		$idResponse[] = $contentTypeID; // idResponse에 관심장르id 요소 추가
	}
	
	
	
	// userID와 contentTypeID (유저의 관심장르) 테이블에 저장
	
	for($x=0 ; $x<count($idResponse) ; $x++)
	{
		$contentTypeID = $idResponse[$x];
		$statement2 = mysqli_prepare($conn, "INSERT INTO USER_INTEREST VALUES (?,?)");
		mysqli_stmt_bind_param($statement2, "si", $userID, $contentTypeID); 
		mysqli_stmt_execute($statement2);
	  }
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	//mysqli_close($conn);
?>