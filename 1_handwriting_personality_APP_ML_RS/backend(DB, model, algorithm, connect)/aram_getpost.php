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
	
	/* 테스트
	phpmyadmin 페이지에서 USER_POST 임시로 삽입 (또는 aram_post.php test 먼저 실행해서 값을 삽입하고, 그 내용이 출력되는지 확인)
	*/
	
	# CONTENTS 테이블이랑 USER_POST 테이블 조인해서 가져오기 # postID, dailyQuest 제외 가져옴 (0523 postID 추가로 받기)
	$statement = mysqli_query($conn, "SELECT userID, userName, postText, writingImage, postDate, contentTitle, contentWriter, postID FROM USER_POST INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID ORDER BY postDate DESC"); 

	$response = array();
	
	if($statement){
		while($row = mysqli_fetch_array($statement)) {
			array_push($response,
				array('userID' => $row[0],
				'userName' => $row[1],
				'postText' => $row[2],
				'writingImage' => $row[3],
				'postDate' => $row[4],
				'contentTitle' => $row[5],
				'contentWriter' => $row[6],
				'postID' => $row[7]
			));
		}
		
		header("Content-Type:application/json; charset=utf8");
		$json = json_encode(array("posts"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
		
		echo $json;
	}
	else {
		echo "error";
	};
	
	
	mysqli_close($conn);
?>