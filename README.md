# Tower-Defense Game Project

이 레포지토리는 타워 디펜스 게임의 서버, 머신러닝 서버 그리고 시뮬레이터 아키텍쳐를 담고 있습니다. 시뮬레이팅을 통해 여러 타워 디펜스 시나리오를 테스트하여 머신러닝 모델을 업데이트 하였고, 실시간으로 ai agent와 경쟁할수 있는 게임입니다.

## Features

### Game
https://tower-defense-ai.chaneeh-games.com 

unity를 이용하여 타워디펜스 웹 게임을 구현하였습니다
### Server
웹 게임에서 메인 서버와 wss 프로토콜로 통신하였고, agent 행동에 관련한 추론은 fastapi로 구성된 ml server pod에서 담당하였습니다. 
학습된 ml model들은 s3를 모델 저장소로 사용하였고, ml model 관련 util 함수들은 test 코드를 이용하여 가독성 및 안전성을 높였습니다.
![Architecture Diagram](./images/tower_defense_prod_server_architecture.png)



### Simulator

**Agent action**을 모델링하기 위해 `Q Table`을 사용하였고, **모든 에피소드**(5 tower wave)가 끝난 뒤 보상을 계산하여 업데이트를 하였습니다.

#### Train Process

데이터 수집 과정은 다음과 같이 이루어집니다:

1. `Simulator Enqueue` 서버에 **add task 요청**을 하면, **Redis**에 task가 enqueue됩니다.
2. 
   > 💡 **요청된 simulator 횟수**만큼 (`Redis queue length` + `Active Celery worker`) **Unity simulator code**가 포함된 `Celery worker`가 **자동으로 스케일링**됩니다.
3. 각 `Celery worker image`에 포함된 **Unity build code**는 **headless 모드**로 실행되어, 환경, action, reward 데이터를 수집합니다.

#### 성능 및 리소스 사용

- **각 simulator당** 평균 **1.7 core**를 사용하며, **6분**이 소요됩니다.
- **학습 과정 각 스텝에서** 평균적으로 **80개의 simulator pod**를 병렬로 생성합니다.

![Architecture Diagram](./images/tower_defense_train_simulator_architecture.png)
