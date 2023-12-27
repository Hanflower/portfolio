# -*- coding: utf-8 -*-
#!C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

import pandas as pd
import numpy as np
import json 

# 서버에 pip install scikit-surprise 설치 해야함
from surprise import SVD 
from surprise import Dataset
from surprise import Reader
from surprise.model_selection import train_test_split
from surprise.model_selection import GridSearchCV
import pymysql

# 파라미터 튜닝, 결과가 괜찮은 n_factors를 사용하여 SVD 모델을 돌리는 것 까지 which_best_params
def which_best_params(ratings) :
    reader = Reader(rating_scale=(0, 1)) # rating의 최솟값 0, 최댓값 1
    data = Dataset.load_from_df(ratings[['userID', 'postID', 'rating']], reader=reader)

    param_grid = {'n_factors': [50, 100, 200]}

    # scikit-learn의 cv와 아주 유사하다고 함
    # n_factors : 축소 차원수(n*n의 스케일 행렬, 가운데 행렬 크기 지정)
    # n_epochs : 전체 데이터 셋을 훈련시키는 횟수
    # lr_all : 학습률

    gs = GridSearchCV(SVD, param_grid, measures=['rmse', 'mae'], cv=3)  # 데이터가 작아서 일단 cv=3
    gs.fit(data)

    best_params = gs.best_params['rmse']
    n_factors = best_params['n_factors']
    # print(n_factors)

    svd = SVD(n_factors = n_factors, random_state=0)
    svd.fit(data.build_full_trainset())

    return svd
    
# 협업 필터링
def collaborative_recommend(target_user, ratings, user_post) :
    target_data = []
    full_scrap = ratings['postID'].values
    targetuser_scrap = ratings[ratings['userID'] == target_user]['postID'].values
    targetuser_post = user_post[user_post['userID']==target_user]['postID'].values  # 본인 post 제외 코드 추가
    not_scrap = list(set(full_scrap) - set(targetuser_scrap)- set(targetuser_post)) 

    for postID in not_scrap : 
        target_data.append((target_user, postID))
    
    svd = which_best_params(ratings) 
    ## 
    # target_data_predictions = svd.test(target_data)
    ##
    target_data_predictions = []
    for target_user, item_id in target_data:
        prediction = svd.predict(target_user, item_id)
        target_data_predictions.append(prediction)

    target_data_predictions.sort(key=lambda x : x.est, reverse=True)
    # print(target_data_predictions)
    
    ## 유사도가 높은 순으로 정렬 확인  
    recommend_list = [x.iid for x in target_data_predictions[:4]]  # 유사도가 높은 순으로 상위 4개의 postID
    # print(recommend_list)
    recommend_list = list(map(str, recommend_list))

   # sql에 접속해서 USER_POST 전체 정보 받아오기
    con = pymysql.connect(host='localhost', user='aram_temp', password='aram_12345!', db='sample', charset='utf8') # 한글처리 (charset = 'utf8')
    cur = con.cursor()
 
    # postID, userID, userName, postText, writingImage, postDate, dailyQuest, contentID	
    # 제목, 작가 가져오려고 contents랑 join
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
    cur.execute(sql, (target_user, target_user, target_user))
    rows = cur.fetchall()

    # 데이터프레임으로 변환
    post_df = pd.DataFrame(rows, columns=['userID', 'userName', 'postText', 'writingImage', 'postDate', 'contentTitle', 'contentWriter', 'postID', 'love', 'surprise', 'angry', 'sad', 'num_of_bookmark', 'bookmarked', 'emoticoned', 'mine'])
    con.close()

    #postID가 같으면 1 아니면 0
    post_df['recommend'] = post_df['postID'].apply(lambda x: 1 if str(x) in recommend_list else 0)

    # 정렬
    sort_df = post_df.sort_values(by=['recommend'], ascending=False)

    # postDate 날짜 이상한 숫자로 변환되는거 방지
    sort_df['postDate'] = sort_df['postDate'].apply(lambda x : x.strftime("%Y-%m-%d %H:%M:%S"))

    # 다시 json 형태로 출력
    print(sort_df.drop(['recommend'], axis=1).to_json(orient='records'))


# 실제 실행
json_data = json.loads(sys.argv[1])

# user_post와 post_bookmark 데이터프레임으로 변환
user_post = pd.DataFrame(json_data['user_post'])
post_bookmark = pd.DataFrame(json_data['post_bookmark'])
userID = json_data['userID']

ratings = pd.DataFrame({'userID': post_bookmark['userID'],
                        'postID': post_bookmark['postID'],
                        'rating': 1})
# 추천 알고리즘
collaborative_recommend(userID, ratings, user_post)
