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
	
	/* 테스트 : 해당하는 post와 이걸 북마크한.. 등 관련 기록도 같이 삭제
	$postID = "37";
	*/
	
	// 서버에 있는 이미지 삭제
	$statement1 = mysqli_prepare($conn, "SELECT writingImage FROM USER_POST WHERE postID = ?");
	mysqli_stmt_bind_param($statement1, "i", $postID);  
	mysqli_stmt_execute($statement1);
	
	mysqli_stmt_store_result($statement1);
	mysqli_stmt_bind_result($statement1, $writingImage); 
	
	while(mysqli_stmt_fetch($statement1)) {
		$imagepath = $writingImage;
	}
	
	$filename = substr($imagepath, 37);
	unlink("post_img/".$filename);
	
	
	# 받은 postID에 해당하는 post 삭제
	$statement = mysqli_prepare($conn, "DELETE FROM USER_POST WHERE postID = ?");
	mysqli_stmt_bind_param($statement, "i", $postID); 
	mysqli_stmt_execute($statement);
	
	
	
	//
	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
	
	mysqli_close($conn);
?>