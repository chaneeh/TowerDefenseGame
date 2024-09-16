# 프로젝트 테스트 가이드

이 문서는 해당 ml server 서버의 테스트 코드 작성 및 실행 방법에 대한 가이드를 제공합니다.

## 1. 가상환경 설정 및 패키지 설치

먼저, `venv` 가상환경을 활성화하고 필요한 패키지를 설치합니다.

```bash
source venv/bin/activate
pip install pytest httpx
pip freeze > requirements.txt
```

## 2. 테스트 디렉토리 구조 설정
테스트 코드를 작성하기 위해 tests 폴더를 추가하고, 테스트 파일을 생성합니다. 예시 디렉토리 구조는 다음과 같습니다:

```bash
ml_server/q_learning/
│
├── app/
│   ├── main.py
│   ├── ml_model.py
│   └── ...
├── tests/           <- add test code here
│   └── test_main.py
├── Dockerfile
└── requirements.txt
```

## 3.테스트 코드 작성
tests 폴더에 테스트 로직을 추가합니다. 예시로 test_main.py에 다음과 같은 코드를 작성할 수 있습니다.
```python
from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)

def test_read_root():
    response = client.get("/ml-server-prod/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}
    
def test_prediction():
    state = (2, 0, 3, 0, 0, 5, 0, 0, 5, 0, 0, 5, 1)
    epsilon = 0.0
    response = client.post("/ml-server-prod/predict", json={"state": state, "epsilon": epsilon})
    assert response.status_code == 200
```

## 4.테스트 실행 및 Git에 커밋
가상환경에서 pytest 명령어를 사용하여 테스트를 실행하고, 테스트가 통과하면 Git에 푸시합니다.

```bash
python3 -m pytest tests
```


