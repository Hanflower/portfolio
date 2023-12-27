<?php
	header("Content-Type:text/html;charset=utf-8");
	
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

    // 안드에서 userID 받아오기
	$userID = isset($_POST["userID"]) ? $_POST["userID"] : ""; 

    //test
	# $userID = 'ww'; # 테스트할 ID
	
	// 로그인한 userID의 OCEAN가져오기
	$statement1 = mysqli_prepare($conn, "SELECT userCharacterTypeO, userCharacterTypeC, userCharacterTypeE, userCharacterTypeA, userCharacterTypeN FROM user_info WHERE userID = ?");
	mysqli_stmt_bind_param($statement1, 's', $userID);
	mysqli_stmt_execute($statement1);

	$result1 = mysqli_stmt_get_result($statement1);
	$user_character = mysqli_fetch_array($result1, MYSQLI_ASSOC); // MYSQLI_ASSOC : 연관배열로 만들어주는 파라미터

    /* 특성 잘 받아왔는지 확인용 */
	// echo "<pre>";
	// print_r($user_character);
	// echo "</pre>";

	// 특성분석 OCEAN 값이 같은 사용자들이 북마크한 postID 가져오기
	$query = "SELECT userID, postID FROM post_bookmark WHERE userID IN (SELECT userID FROM user_info WHERE userCharacterTypeO = ? AND userCharacterTypeC = ? AND userCharacterTypeE = ? AND userCharacterTypeA = ? AND userCharacterTypeN = ?)";
	$statement2 = mysqli_prepare($conn, $query);
	mysqli_stmt_bind_param($statement2, 'iiiii', $user_character['userCharacterTypeO'], $user_character['userCharacterTypeC'], $user_character['userCharacterTypeE'], $user_character['userCharacterTypeA'], $user_character['userCharacterTypeN']);
	mysqli_stmt_execute($statement2);

	$result2 = mysqli_stmt_get_result($statement2);
	$post_bookmark = mysqli_fetch_all($result2, MYSQLI_ASSOC);

	/* 특성 분석 같은 자들 post_bookmark 확인용 */
	// echo "<pre>";
	// print_r($post_bookmark);
	// echo "</pre>";

    # py 파일에서 cv 값이 3이라서 ratings 테이블 값이 3개는 있어야 함
	if(empty($user_character) || count($post_bookmark) < 3) // 원래 3
	{
		include('aram_recommend_contentbased2.php');
	}
	else
	{
		// user_post에서 userID, postID를 가져오기 (파이썬 파일에서 자기 자신이 쓴 post를 삭제하기 위함)
		$query = "SELECT userID, postID FROM user_post";
		$statement3 = mysqli_prepare($conn, $query);
		mysqli_stmt_execute($statement3);

		$result3 = mysqli_stmt_get_result($statement3);
		$user_post = mysqli_fetch_all($result3, MYSQLI_ASSOC);

		// echo "<pre>";
		// print_r($user_post);
		// echo "</pre>";

		$data = json_encode(array(
			'user_post' => $user_post,
			'post_bookmark' => $post_bookmark,
			'userID' => $userID
		));
	
		$json_data = json_encode($data);

		// JSON 데이터를 문자열로 변환한 뒤 파이썬 스크립트에 인자로 전달
		$command = "C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe py_script\aram_recommend_svd2.py {$json_data} 2>&1";
		$output = array();
		exec($command, $output);

		// echo "<pre>";
		// print_r($output);
		// echo "</pre>";

		# 정렬된 post json 받아오기
		header("Content-Type:application/json; charset=utf8");
		$post_sort = json_encode(array("posts"=>json_decode($output[0])), JSON_PRETTY_PRINT + JSON_UNESCAPED_UNICODE);

    	echo $post_sort;
	
		mysqli_close($conn);
	}
		// mysqli_close($conn);
	// mysqli_close($conn);
?>