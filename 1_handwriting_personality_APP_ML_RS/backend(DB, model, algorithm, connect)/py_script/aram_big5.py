#!C:\Users\Administrator\AppData\Local\Programs\Python\Python312\python.exe

# -*- coding: utf-8 -*-

import sys
import io

sys.stdout = io.TextIOWrapper(sys.stdout.detach(), encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.detach(), encoding='utf-8')

# ===== 7가지 특징이 담긴 여러 행 받아서 평균치의 (중앙값치의) OCEAN 결과 반환
import numpy as np

import pandas as pd
import joblib
import json # json.loads()
from pickle import load

def check_ocean(feature_string, gender) :

    gender = int(gender) # 문자열로 넘어온 값 int 타입으로 변환
    
    # 7가지 특징 평균
    feature_data = json.loads(feature_string)
    df = pd.DataFrame(feature_data)
    
    # df 행 수가 일정 수 이하면 그냥 return / 아니면 평균 내고 다음 단계 진행
    #if df.shape[0] < 3 :
    #    print(10) # trash value
    #    print(10)
    #    print(10)
    #    print(10)
    #    print(10)
    #    return
    #else : 
    #    user_val = [list(df.mean())] 
    user_val = [list(df.mean())]
    
    # 모델로 예측 (각 5개)
    val = pd.DataFrame(user_val, columns = ['size', 'slant', 'wordSpacing', 'pressure', 'baseline', 'lineSpacing', 'speed']) 
    
    
    if (val['lineSpacing'].values[0] <0) or (val['speed'].values[0] <0) : # 예외처리 (특히 뒤에 변환하는 변수 : lineSpacing, speed 그리고 baseline + 90) // size와 wordSpacing도 추가하느냐
        predict_O = 10 # trash value
        predict_C = 10
        predict_E = 10
        predict_A = 10
        predict_N = 10
        
    elif (val['baseline'].values[0]+90 <0) :
        predict_O = 10 # trash value
        predict_C = 10
        predict_E = 10
        predict_A = 10
        predict_N = 10
        
    else : # 정상처리
        # 모델 불러오기 (각 5개)
        if gender : # 1 (여) 
            # 저장한 스케일러 불러오기
            O_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/f_O_scaler.pkl', 'rb'))
            C_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/f_C_scaler.pkl', 'rb'))
            E_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/f_E_scaler.pkl', 'rb'))
            A_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/f_A_scaler.pkl', 'rb'))
            N_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/f_N_scaler.pkl', 'rb'))

            # 저장한 모델 불러오기
            O_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/f_O_model.pkl') 
            C_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/f_C_model.pkl')
            E_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/f_E_model.pkl')
            A_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/f_A_model.pkl')
            N_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/f_N_model.pkl')
            
            # 변환
            val['lineSpacing'] = np.sqrt(val['lineSpacing'])# 제곱근 변환
            
            # 표준화
            O_val = val.drop(["slant"], axis=1)
            O_scaler_val = O_scaler.transform(O_val) 
            
            C_scaler_val = C_scaler.transform(val)
            
            E_val = val.drop(["wordSpacing", "baseline"], axis=1)
            E_scaler_val = E_scaler.transform(E_val)
            
            A_val = val.drop(["slant", "lineSpacing"], axis=1)
            A_scaler_val = A_scaler.transform(A_val)
            
            N_val = val.drop(["baseline"], axis=1)
            N_scaler_val = N_scaler.transform(N_val)
            
            # 예측
            predict_O = O_model.predict(O_scaler_val)[0]
            predict_C = C_model.predict(C_scaler_val)[0] 
            predict_E = E_model.predict(E_scaler_val)[0]
            predict_A = A_model.predict(A_scaler_val)[0]
            predict_N = N_model.predict(N_scaler_val)[0]
        
        else : # (남) 
            # 저장한 스케일러 불러오기
            O_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/m_O_scaler.pkl', 'rb'))
            C_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/m_C_scaler.pkl', 'rb'))
            E_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/m_E_scaler.pkl', 'rb'))
            A_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/m_A_scaler.pkl', 'rb'))
            N_scaler = load(open('C:/xampp/htdocs/try_aram/py_script/m_N_scaler.pkl', 'rb'))

            # 저장한 모델 불러오기
            O_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/m_O_model.pkl') 
            C_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/m_C_model.pkl')
            E_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/m_E_model.pkl')
            A_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/m_A_model.pkl')
            N_model = joblib.load('C:/xampp/htdocs/try_aram/py_script/m_N_model.pkl')
            
            # 변환
            val['baseline'] = np.log(val['baseline']+90) # 음수 보정 후 로그 변환
            val['lineSpacing'] = np.sqrt(val['lineSpacing'])# 제곱근 변환
            val['speed'] = np.log(val['speed']) # 로그 변환
            
            # 표준화
            O_val = val.drop(["slant"], axis=1)
            O_scaler_val = O_scaler.transform(O_val) 
            
            C_val = val.drop(["slant", "baseline"], axis=1)
            C_scaler_val = C_scaler.transform(C_val)
            
            E_val = val.drop(["baseline"], axis=1)
            E_scaler_val = E_scaler.transform(E_val)
            
            A_val = val.drop(["baseline", "lineSpacing"], axis=1)
            A_scaler_val = A_scaler.transform(A_val)
            
            N_val = val.drop(["lineSpacing"], axis=1)
            N_scaler_val = N_scaler.transform(N_val)
            
            # 예측
            predict_O = O_model.predict(O_scaler_val)[0]
            predict_C = C_model.predict(C_scaler_val)[0] 
            predict_E = E_model.predict(E_scaler_val)[0]
            predict_A = A_model.predict(A_scaler_val)[0]
            predict_N = N_model.predict(N_scaler_val)[0]
            

    
    print(predict_O)
    print(predict_C)
    print(predict_E)
    print(predict_A)
    print(predict_N)
    
check_ocean(sys.argv[1], sys.argv[2]) # sys.argv[1] : feature_string // sys.argv[2] : userGender