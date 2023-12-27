# -*- coding: utf-8 -*-
#!C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

import sys
import json
import pandas as pd
import numpy as np
import pymysql

from sklearn.metrics.pairwise import cosine_similarity

def collaborative_recommend(target_user, ratings_matrix) :

    # 코사인 유사도 계산
    user_cosim = cosine_similarity(ratings_matrix.fillna(0))
    # print(user_cosim)
    user_cosim = pd.DataFrame(user_cosim, columns=ratings_matrix.index, index=ratings_matrix.index)
    # print(user_cosim)

    n = 3 # 유사한 사용자 3명 뽑기
    user_cosim_threshold = 0.5 # 유사도 0.5이상만
    similar_users = user_cosim[user_cosim[target_user]>user_cosim_threshold][target_user].sort_values(ascending=False)[:n]
    # print(similar_users)

    similar_users_ratings = ratings[ratings['userID'].isin(similar_users.index)]
    post_ids = similar_users_ratings['postID'].unique() # 유사한 사용자들이 포스트한 postID들
    user_scraped = ratings[ratings['userID'] == target_user]['postID'].values
    user_posted = user_post[user_post['userID'] == target_user]['postID'].values

    # 유사한 사용자가 포스팅한 것들에서 자신이 스크랩, 포스팅 제외 추천
    recommend_postIDs = list(set(post_ids) - set(user_scraped) - set(user_posted))

    # print(recommend_postIDs)

    con = pymysql.connect(host='localhost', user='aram_temp', password='aram_12345!', db='sample', charset='utf8') # 한글처리 (charset = 'utf8')
    cur = con.cursor()
 
    # postID, userID, userName, postText, writingImage, postDate, dailyQuest, contentID	
    # 제목, 작가 가져오려고 contents랑 join
    sql = "SELECT postID, userID, userName, postText, writingImage, postDate, contentTitle, contentWriter FROM USER_POST INNER JOIN CONTENTS ON USER_POST.contentID = CONTENTS.contentID ORDER BY postDate DESC"
    cur.execute(sql)
    rows = cur.fetchall()

    # 데이터프레임으로 변환
    post_df = pd.DataFrame(rows, columns=['postID','userID', 'userName', 'postText', 'writingImage', 'postDate', 'contentTitle', 'contentWriter'])
    con.close()

    #postID가 같으면 1 아니면 0
    post_df['recommend'] = post_df['postID'].apply(lambda x: 1 if x in recommend_postIDs else 0)

    # 정렬
    #post_df.loc[post_df['postID'].isin(recommend_list), 'recommend'] = 1
    sort_df = post_df.sort_values(by=['recommend'], ascending=False)

    # postDate 날짜 이상한 숫자로 변환되는거 방지
    sort_df['postDate'] = sort_df['postDate'].apply(lambda x : x.strftime("%Y-%m-%d %H:%M:%S"))

    # 다시 json 형태로 출력
    print(sort_df.drop(['recommend', 'postID'], axis=1).to_json(orient='records'))


# 실제 실행
json_data = json.loads(sys.argv[1])

# user_post와 post_bookmark 데이터프레임으로 변환
user_post = pd.DataFrame(json_data['user_post'])
post_bookmark = pd.DataFrame(json_data['post_bookmark'])
userID = json_data['userID']

ratings = pd.DataFrame({'userID': post_bookmark['userID'],
                        'postID': post_bookmark['postID'],
                        'rating': 1})

ratings_matrix = ratings.pivot_table(index='userID', columns='postID', values='rating')
# print(ratings_matrix)

# 추천 알고리즘
collaborative_recommend(userID, ratings_matrix)