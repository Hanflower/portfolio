# 패키지 불러오기
import cv2
import numpy as np

# 한줄씩 실행 단축키 : Alt + Shift + E #

# 순서
# 1. 이미지 불러오기
# 2. 이미지 사이즈 통일 (비율 유지) : def resize_img(img)

# 3. 전처리 (이진화 이미지 출력) : def preprocess_img(img)
# 3-1. 줄 기준 분리 , def preprocess_forLine(img)
# 3-2. 글자 기준 분리, 띄어쓰기 기준 분리 def preprocess_forWord(img)

# 4. baseline (줄 기울기) : def check_baseline(img)
# 5. pressure (압력) : def check_pressure(img)
# 6. size (글자의 크기) : def check_size(img)
# 7. slant (글자의 기울기) : def check_slant(img)
# 8. wordSpacing (단어 간격) : def check_wordSpacing(img)
# 9. lineSpacing (줄 간격) : def check_lineSpacing(img)

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 0401 수정 #
# check_slant 새코드
# check_wordSpacing 함수명에서 'S' 대문자
# check_lineSpacing 코드에서 cv2.imshow("close", close) 제외
# line_segmentation 코드에서 노이즈 방지용 코드 추가
# line_wordSpacing, check_wordSpacing 코드 순서 수정 (전처리 전 후 이런 ..)
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 0404 수정 #
# - check_ 시리즈 함수 순서 변경 (편의상)
# - skew_correction 함수 정의 (ipynb에서 중간 과정이나 기존 참고 링크 확인 가능)
    # 노이즈 문제, 참고한 링크대로 box를 그렸는데 이상해서 기존 check_baseline 활용하기로 했어요.
    # 각도를 심하게 설정해서 문제가 생긴 up.png의 경우에도 두줄에 걸쳐 큰 사각형이 그려질뿐, 줄 각도(baseline)에는 크게 영향이 없다고 판단해서요.
# - skew_correction 함수 check_size, check_wordSpacing, check_lineSpacing에 추가. 아래 이유.
                    # baseline : 필요 X (skew_correction에서 baseline 값 활용)
                    # pressure : 추가 전처리 없으므로 굳이 필요 X
                    # size : skew 필요 (@@)
                    # slant : 추가 전처리 없으므로 굳이 필요 X
                    # wordSpacing : skew 필요 (@@)
                    # lineSpacing : skew 필요 (@@)
# - skew_correction 추가하면서 심하게 달라진 값들이 있는지 확인했는데
    # check_lineSpacing 값이 꽤 달라진 경우가 일부 발견.(2bstrong, hb3, hbmiddle, hbstrong) 코드랑 이미지랑 살펴보다가
    # 작은 수정 제안 ↓ (그래서 check_lineSpacing 함수 2개고, 제안한 버전은 가장 마지막에 중간 확인용 print, show 주석처리 지우지않고 그대로 두었습니다.)
    # 기존은 중심 Y 좌표 차에 현재 box의 세로 크기를 빼는 계산이었는데, 중심 Y 좌표 차에 해당하는 두 box의 각 세로 크기 / 2.0 반반씩 빼는건 어떤지
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 0410 수정 #
# check_slant 코드에서 기존 6.0 기준 3.5로 내림
# check_size, check_wordSpacing(line_wordSpacing) 코드에서 minAreaRect -> boundingRect로 변경
# 전부 다 np.avarage -> np.median (pressure 제외)
# lineSpacing 함수 코드 결정

# pressure, speed 고민
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 0419 수정
# check_speed 추가
# lineSpacing nan (space 리스트 길이가 0일 경우, return 0)
# find_linenum 함수 줄단위로 인식된 객체를 세는데, 'ㅣ'같은걸 길게 써서 다음줄이랑 연결된 경우 문제 발생.
        # guess_size, line_segmentation은 다른 방법이라서 여기를 보완하고 find_linenum 함수는 사용 X. (삭제)
# 위 함수 삭제하면서 해당 함수가 사용된 부분 수정. preprocess_forWord, check_size, check_wordSpacing 함수.
        # 16장 baby data set 값 변화 X. 그 외 예외 사진 처리 O.
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# 0507 수정
# check_slant, preprocess_forWord, check_size, line_wordspacing, check_wordSpacing 수정 (수집한 데이터에서 나온 예외 보완)
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #

# 1. 이미지 불러오기
def load_img(filePath):
    rawImg = cv2.imread(filePath, flags=cv2.IMREAD_COLOR)
    if rawImg is None:
        raise Exception("이미지 읽기 오류")
        return None
    else:
        return rawImg


# 2. 이미지 사이즈 통일 (비율 유지) : def resize_img(img)

def resize_img(img):
    width_len = 3500
    resize_ratio = width_len / img.shape[1]
    resizeImg = cv2.resize(img, (width_len, int(img.shape[0] * resize_ratio)))

    return resizeImg


# 3. 전처리 (이진화 이미지 출력) : def preprocess_img(img)
def preprocess_img(img):  # 여기서 img는 resize된 img

    # 정규화로 화질 개선
    normImg = cv2.normalize(img, None, 120, 255, cv2.NORM_MINMAX)

    # 대상 선명하게 표현
    kernel = np.array([[0, -1, 0],
                       [-1, 5, -1],
                       [0, -1, 0]])

    sharpImg = cv2.filter2D(normImg, -1, kernel)

    # 이미지 grayscale로 변경
    grayImg = cv2.cvtColor(sharpImg, cv2.COLOR_BGR2GRAY)

    # 적응형 임계값 이진화
    block_size = 89
    C = 8
    threshImg = cv2.adaptiveThreshold(grayImg, 255, cv2.ADAPTIVE_THRESH_GAUSSIAN_C, cv2.THRESH_BINARY_INV, block_size,
                                      C)

    # 노이즈 제거
    noiseImg = cv2.medianBlur(threshImg, 7)

    noise_kernel = cv2.getStructuringElement(cv2.MORPH_RECT, (4, 4))
    removeImg = cv2.morphologyEx(noiseImg, cv2.MORPH_OPEN, noise_kernel)  # MORPH_OPEN : 객체 밖의 노이즈 제거

    return removeImg


# 3-1. 줄 기준 분리 , def preprocess_forLine(img)
def preprocess_forLine(img):
    mask = np.ones((5, 115), np.uint8)
    close = cv2.morphologyEx(img, cv2.MORPH_CLOSE, mask, iterations=3)

    return close


# 3-2. 글자 기준 분리, 띄어쓰기 기준 분리 def preprocess_forWord(img)
def guess_size(img, line_num):
    guess_size = 0

    hist = np.sum(img, axis=1)

    guess_size = np.sum(hist != 0) / line_num

    if guess_size >= 140:
        return True
    else:
        return False


def preprocess_forWord(img, try_k=None):

    temp_mask = np.ones((15, 15), np.uint8)
    tempImg = cv2.morphologyEx(img, cv2.MORPH_CLOSE, temp_mask, iterations=3)
    line_img = line_segmentation(tempImg)  # @@@@ 0416
    line_num = len(line_img)  # @@@@ 0416

    val = guess_size(img, line_num)
    if val:
        k = 22
    else:
        k = 12

    if try_k is not None:
        k = try_k

    mask = np.ones((15, k), np.uint8)
    erodeImg = cv2.morphologyEx(img, cv2.MORPH_CLOSE, mask, iterations=3)

    return erodeImg

# 4. baseline (줄 기울기) : def check_baseline(img)
def check_baseline(img):
    close = preprocess_forLine(img)
    contours, hierarchy = cv2.findContours(close, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    angleArray = []
    for cnt in contours:
        if cv2.contourArea(cnt) > 15000:  # 자잘한거 인식 안하고 글씨만 인식하기 위해서
            (pos, size, angle) = cv2.minAreaRect(cnt)
            if (size[0] < size[1]):  # width < height
                angleArray.append(90 - angle)
            else:
                angleArray.append(-angle)

    return np.median(angleArray)

def skew_correction(img) :

    angle = - check_baseline(img)

    # rotate the image to deskew it
    (h, w) = img.shape[:2]
    center = (w // 6, h // 6) #  (w // 2, h // 2)
    M = cv2.getRotationMatrix2D(center, angle, 1.0)
    rotatedImg = cv2.warpAffine(img, M, (w, h), flags=cv2.INTER_CUBIC, borderMode=cv2.BORDER_REPLICATE)

    return rotatedImg

# 5. pressure (압력) : def check_pressure(img)
def check_pressure(img):
    grayImg = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)  # 그레이스케일

    mask = preprocess_img(img)
    inverted_mask = 255 - mask  # 배경이 흰색, 글자가 검정인

    while cv2.mean(grayImg, mask=inverted_mask)[0] < 240:
        grayImg = cv2.add(grayImg, 1)

    pressure = np.median(grayImg[mask == 255])

    return pressure

# 6. size (글자의 크기) : def check_size(img)
def check_size(img):
    skewImg = skew_correction(img)

    # 매개변수 조정
    temp_mask = np.ones((15, 15), np.uint8)
    tempImg = cv2.morphologyEx(skewImg, cv2.MORPH_CLOSE, temp_mask, iterations=3)
    line_img = line_segmentation(tempImg)  # @@@@ 0416
    line_num = len(line_img)  # @@@@ 0416

    val = guess_size(skewImg, line_num)
    if val:
        area = 3000
    else:
        area = 1000

    # size 담을 리스트
    mean_size = []

    img = preprocess_forWord(skewImg)

    # 객체 외곽선 탐지
    contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, 3)

    if line_num == len(contours):  # 단어 단위로 박스가 제대로 안 그려졌을 경우
        img = preprocess_forWord(skewImg, 8)
        contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, 3)

    # 객체를 감싸는 최소 사각형 (크기와 기울기 계산의 기본이 될)
    for cnt in contours:
        if cv2.contourArea(cnt) > area:  # 너무 작은 box 제외
            x, y, w, h = cv2.boundingRect(cnt)

            mean_size.append(h)

    return np.median(mean_size)


# 7. slant (글자의 기울기) : def check_slant(img)
def check_slant(img):  # 따로 추가전처리 필요 X (0331.ver)

    mean_slant = []

    contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, 3)

    # 객체를 감싸는 최소 사각형 (크기와 기울기 계산의 기본이 될)
    for cnt in contours:
        rect = cv2.minAreaRect(cnt)

        if (cv2.contourArea(cnt) > 300):
            if (rect[1][1] * 3.5 < rect[1][0]) or (rect[1][0] * 3.5 < rect[1][1]):
                if rect[2] < 45.0:
                    mean_slant.append(-rect[2])
                else:
                    mean_slant.append(90.0 - rect[2])

    # box를 못 그려서 길이가 0일 때 else로 조건 낮춰서 한 번 더 검사
    if len(mean_slant) != 0:
        return np.median(mean_slant)
    else:
        for cnt in contours:
            rect = cv2.minAreaRect(cnt)

            if (cv2.contourArea(cnt) > 300):
                if (rect[1][1] * 2.5 < rect[1][0]) or (rect[1][0] * 2.5 < rect[1][1]):  # 원래 3.5
                    if rect[2] < 45.0:
                        mean_slant.append(-rect[2])
                    else:
                        mean_slant.append(90.0 - rect[2])

        return np.median(mean_slant)


# 8. wordSpacing (단어 간격) : def check_wordSpacing(img)
def line_segmentation(img):
    line_img = []
    start_idx = 0
    avoid_noise = False  # noise 방지용

    hist = np.sum(img, axis=1)

    for i in range(len(hist)):
        if hist[i] >= 15000: avoid_noise = True

        if (avoid_noise == True) and (hist[i] != 0) and (hist[i + 1] < 6000):  # 행을 따라 내려가는데, 색이 있다가 없어진 순간
            line_img.append(img[start_idx: i + 2, ])  # i+1까지만 하면 되는데, 픽셀 한칸 더 여유를 두고 이미지 자르기
            avoid_noise = False  # 다시 reset
            start_idx = i + 3

    return line_img


# 분리한 한줄 이미지에 대해서 인접하는 두 box의 x 최소 최대 리턴
def line_wordspacing(img, val):
    all_wordspacing = []
    box_minmax = []

    if val:
        area = 3000
    else:
        area = 1000

    # 객체 외곽선 탐지
    contours, hierarchy = cv2.findContours(img, cv2.RETR_EXTERNAL, 3)

    # 정렬
    contours = sorted(contours, key=lambda x: cv2.minAreaRect(x)[0][0])  # 중심 좌표 x 기준 정렬

    # 객체를 감싸는 최소 사각형 (크기와 기울기 계산의 기본이 될)
    for cnt in contours:
        if cv2.contourArea(cnt) > area:  # 너무 작은 box 제외
            x,y,w,h = cv2.boundingRect(cnt)

            # 현 box의 x 최소 x 최대
            box_minmax.append(x)
            box_minmax.append(x+w)

    # 모든 box의 x 최소 x 최대를 쭉 모았을 때
    for i in range(1, len(box_minmax)):  # 1부터 ~
        if i % 2 == 0:  # 2, 4, 6, ...

            spacing = box_minmax[i] - box_minmax[i - 1]
            if spacing < 0:  # # 여기서 만약 겹쳐서 음수가 나오는 경우엔 그냥 냅둘지 0으로 처리할지 -> 0으로 처리
                spacing = 0

            all_wordspacing.append(spacing)

    return all_wordspacing


def check_wordSpacing(img):

    skewImg = skew_correction(img)
    img = preprocess_forWord(skewImg)  # @@@@ 0416
    line_img = line_segmentation(img)  # @@@@ 0416
    line_num = len(line_img)  # @@@@ 0416

    mean_wordspacing = []

    val = guess_size(img, line_num)  # @@@@ 0416

    for i in range(line_num):
        mean_wordspacing.extend(line_wordspacing(line_img[i], val))

    if len(mean_wordspacing) != 0:
        return np.median(mean_wordspacing)
    else:
        img = preprocess_forWord(skewImg, 8)
        line_img = line_segmentation(img)

        line_num = len(line_img)

        mean_wordspacing = []

        val = guess_size(img, line_num)

        for i in range(line_num):
            mean_wordspacing.extend(line_wordspacing(line_img[i], val))

        return np.median(mean_wordspacing)

# 9. lineSpacing (줄 간격) : def check_lineSpacing(img)
def check_lineSpacing(img) :

    img = skew_correction(img)

    close = preprocess_forLine(img)
    contours, hierarchy = cv2.findContours(close, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    # centerY : 첫번째는 차이 구할 것이 없으니까 넘어가기 위한 장치
    centerY=0
    space=[]

    for cnt in contours :
        if cv2.contourArea(cnt) > 15000 : # 자잘한거 인식 안하고 글씨만 인식하기 위해서
            (pos, size, angle) = cv2.minAreaRect(cnt)
            pos = (int(pos[0]), int(pos[1]))

            # 글자 크기 재기
            if size[0] > size[1]:
              height = size[1]
            else:
              height = size[0]

            # (중심 y좌표 차이) - (글자 세로 크기)
            if (centerY) :
                space.append(centerY - pos[1] - height / 2.0 - centerH / 2.0)

            centerY = pos[1]
            centerH = height

    if len(space) == 0:
        return 0

    return np.median(space)

def check_speed(time, num) :
    return time / num