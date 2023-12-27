#!C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

# -*- coding: utf-8 -*-

import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

# 필요한 패키지 불러오기
import pandas as pd
import json 
import ast
import numpy as np

from numpy import dot
from numpy.linalg import norm

import pymysql
from datetime import datetime

# 유사도 함수
def cos_sim(A, B):
    return dot(A, B)/(norm(A)*norm(B))

# 추천 알고리즘
def contentbased_recommend(userID, recent_contentID, interest_typeList) : # recent_contentID는 정수, interest_typeList는 정수가 있는 리스트
    
    recent_contentID = int(recent_contentID) # 문자열로 넘어온 값 int 타입으로 변환
    interest_typeList = ast.literal_eval(interest_typeList) # 문자열로 넘어온 값 List 타입으로 변환
    
    # embedding_df 파일 불러오기
    embedding_df = pd.read_csv('C:/xampp/htdocs/try_aram/py_script/Aram_embedding.txt', sep='|')
    
    embedding_df['embedding1'] = embedding_df['embedding1'].apply(lambda x : np.fromstring(x[1:-1], dtype=float, sep=' ')) # embedding [**, **,...] 타입 np.ndarray로 제대로 변환
    embedding_df['embedding2'] = embedding_df['embedding2'].apply(lambda x : np.fromstring(x[1:-1], dtype=float, sep=' '))
    
    # 최근에 포스팅한 콘텐츠ID와 유사도 측정
    embedding1 = embedding_df[embedding_df.contentID == recent_contentID]['embedding1'].values[0]
    sim_scores1 = list(embedding_df.apply(lambda x: cos_sim(x['embedding1'], embedding1), axis=1))

    embedding2 = embedding_df[embedding_df.contentID == recent_contentID]['embedding2'].values[0]
    sim_scores2 = list(embedding_df.apply(lambda x: cos_sim(x['embedding2'], embedding2), axis=1))

    final_simscores = []
    num = range(len(sim_scores2))
    for num, i, j in zip(num, sim_scores1, sim_scores2) :
        final_simscores.append((num, 0.6*i + 0.4*j))

    top_rank = sorted(final_simscores, key=lambda x: x[1], reverse=True)[1:31] # 0번째는 입력값으로 받은 recent_contentID

    # 상위 30개의 작품 인덱스
    sim_indices = [top[0] for top in top_rank]
    top_df = embedding_df.iloc[sim_indices]

    # 최근에 포스팅한 콘텐츠ID와 유사한 상위 30개 중 관심장르에 해당하는 정보 반환 
    recommend_result_df = top_df[top_df['contentTypeID'].isin(interest_typeList)][['contentTitle', 'contentWriter']]


    # 여러 포스팅들 중에 해당하는 콘텐츠에 대해 쓴 포스팅 우선 정렬하기 위해 받은 기본 포스팅 정보 데이터프레임 형식으로 가공
    
    # sql에 접속해서 USER_POST 전체 정보 받아오기
    con = pymysql.connect(host='localhost', user='aram_temp', password='aram_12345!', db='sample', charset='utf8') # 한글처리 (charset = 'utf8')
    cur = con.cursor()
 
    sql = '''WITH emoticon_table AS 
(
    SELECT postID,
    	SUM(CASE WHEN emoticon=4 THEN 1 ELSE 0 END) AS 'love',
		SUM(CASE WHEN emoticon=3 THEN 1 ELSE 0 END) AS 'surprise',
		SUM(CASE WHEN emoticon=2 THEN 1 ELSE 0 END) AS 'angry',
        SUM(CASE WHEN emoticon=1 THEN 1 ELSE 0 END) AS 'sad'
	FROM POST_EMOTICON
	GROUP BY postID
)

SELECT userID, userName, postText, writingImage, postDate, contentTitle, contentWriter, postID,
		IFNULL((SELECT love FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS love,
		IFNULL((SELECT surprise FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS surprise,
		IFNULL((SELECT angry FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS angry,
        IFNULL((SELECT sad FROM emoticon_table e WHERE e.postID = USER_POST.postID), 0) AS sad,
        (SELECT COUNT(*) FROM POST_BOOKMARK b WHERE b.postID = USER_POST.postID) AS num_of_bookmark,
		(SELECT EXISTS (SELECT * FROM POST_BOOKMARK bb WHERE userID = %s AND (bb.postID = USER_POST.postID))) as bookmarked,
        IFNULL((SELECT emoticon FROM POST_EMOTICON ee WHERE ee.userID = %s AND (ee.postID = USER_POST.postID)), 0) AS emoticoned,
		(IF(USER_POST.userID = %s, 1, 0)) as mine
FROM USER_POST INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID
ORDER BY postDate DESC'''
    cur.execute(sql, (userID, userID, userID))
    rows = cur.fetchall()

    # 데이터프레임으로 변환
    post_df = pd.DataFrame(rows, columns=['userID', 'userName', 'postText', 'writingImage', 'postDate', 'contentTitle', 'contentWriter', 'postID', 'love', 'surprise', 'angry', 'sad', 'num_of_bookmark', 'bookmarked', 'emoticoned', 'mine'])
    con.close()

    # recommend_result_df와 정보가 동일하면 1, 나머지 0 (#1#)
    # post_df['recommend']  = post_df.apply(lambda x : 1 if len(recommend_result_df.query("contentTitle == '" + x['contentTitle'] + "' and contentWriter == '" + x['contentWriter'] + "'")) > 0 else 0, axis=1)
    
    # recommend_result_df와 정보가 동일하면 1, 나머지 0 + 자기자신 제외 조건 추가 (#2#)
    post_df['recommend']  = post_df.apply(lambda x : 1 if (x["userID"] != userID) and len(recommend_result_df.query("contentTitle == '" + x['contentTitle'] + "' and contentWriter == '" + x['contentWriter'] + "'")) > 0 else 0, axis=1)
    
    # 정렬
    sort_df = post_df.sort_values(by=['recommend'], ascending=False)
    
    # postDate 날짜 이상한 숫자로 변환되는거 방지
    sort_df['postDate'] = sort_df['postDate'].apply(lambda x : x.strftime("%Y-%m-%d %H:%M:%S"))
    
    # 다시 json 형태로 출력
    print(sort_df.drop('recommend', axis=1).to_json(orient='records'))

# 실제 실행    
contentbased_recommend(sys.argv[1], sys.argv[2], sys.argv[3]) 