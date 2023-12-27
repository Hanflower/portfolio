#!C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

# -*- coding: utf-8 -*-

import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

# ===== php에서 aram_post.php에서 USER_POST 테이블에 삽입한 writingImage 저장경로 변수를 받아서, / 이미지 불러오고 7가지 특성 계산 후 반환 /, php에서 7가지 값이랑 아이디 USER_ANALYSIS 테이블에 삽입
import cv2
import numpy as np
import allfunction as af

def check_feature(filePath, time) :
    
    #time = 22.0 # 임시. 아마 filePath랑 같이 변수로 받아와야할 듯
    time = float(time) # 문자열로 넘어온 값 float 타입으로 변환
    num = 18.5 # 정하기

    img = af.load_img(filePath)
    resizeImg = af.resize_img(img)
    cleanImg = af.preprocess_img(resizeImg)
    
    print(af.check_size(cleanImg))
    print(af.check_slant(cleanImg))
    print(af.check_wordSpacing(cleanImg))
    print(af.check_pressure(resizeImg)) # cleanImg 아님 주의 !!!!!
    print(af.check_baseline(cleanImg))
    print(af.check_lineSpacing(cleanImg))
    print(af.check_speed(time, num))

check_feature(sys.argv[1], sys.argv[2]) # sys.argv[1] : py에서 이미지에 접근하기 위한 filePath / sys.argv[2] : 타이머로 측정한 time