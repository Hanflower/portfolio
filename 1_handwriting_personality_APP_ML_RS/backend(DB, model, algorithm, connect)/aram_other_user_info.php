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
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : "";
	
	/* 테스트 
	$userID = "kim2";
	*/
	
	/* 테스트 결과
	{
		"myinfo": [
			{
				"userID": "kim2",
				"userName": "kim2",
				"following": 1,
				"followers": 1,
				"posts": 6
			}
		]
	}
	*/
	
	
	# userID, userName, following(해당 유저가 팔로잉하고 있는 사용자 수), followers(해당 유저를 팔로잉하고 있는 사용자 수), posts(해당 유저가 쓴 글 수) 반환
	$statement = mysqli_prepare($conn, "SELECT USER_INFO.userID, USER_INFO.userName,
		(SELECT COUNT(userID) FROM USER_FOLLOWING f WHERE f.userID = USER_INFO.userID) AS following,
        (SELECT COUNT(followingID) FROM USER_FOLLOWING f WHERE f.followingID = USER_INFO.userID) AS followers,
        (SELECT COUNT(postID) FROM USER_POST p WHERE p.userID = USER_INFO.userID) AS posts
FROM USER_INFO WHERE USER_INFO.userID = ?;");
	mysqli_stmt_bind_param($statement, "s", $userID); 
	mysqli_stmt_execute($statement);
	
	
	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID, $userName, $following, $followers, $posts);
	
	$response = array();
	
	while(mysqli_stmt_fetch($statement)) {
		array_push($response,
				array('userID' => $userID,
				'userName' => $userName,
				'following' => $following,
				'followers' => $followers,
				'posts' => $posts
			));
	}
	
	header("Content-Type:application/json; charset=utf8");
	$json = json_encode(array("myinfo"=>$response), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);
	echo $json;
	
	mysqli_close($conn);
?>