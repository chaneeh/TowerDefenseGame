# 프로젝트 테스트 가이드

이 문서는 해당 wss 서버의 테스트 코드 작성 및 실행 방법에 대한 가이드를 제공합니다.

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
folder_dir/
│
├── app/
│   ├── main.py
│   ├── ml_endpoint.py
│   └── ...
├── tests/           <- add test code here
│   └── test_main.py
├── Dockerfile
└── requirements.txt
```

## 3.테스트 코드 작성
tests 폴더에 테스트 로직을 추가합니다. 예시로 test_ml_endpoint.py에 다음과 같은 코드를 작성할 수 있습니다.
```python
import pytest
from app.ml_endpoint import map_action_to_game_state
from typing import List, Dict
from pydantic import BaseModel

class MapActionTestCase(BaseModel):
    agent: str
    action_type: int
    tower_status: Dict[str, Dict[str, str]]
    expected_output: Dict

map_action_test_cases = [
    MapActionTestCase(
        agent="agent_1",
        action_type=0,
        tower_status={
            "1": {"type1": "2", "type2": "1"},
            "2": {"type1": "2", "type2": "0"},
            "3": {"type1": "1", "type2": "0"},
            "4": {"type1": "0", "type2": "0"},
        },
        expected_output={"agent": "agent_1", "action": "stay"},
    )
]

@pytest.mark.parametrize("case", map_action_test_cases)
def test_map_action_to_game_state(case: MapActionTestCase):
    result = map_action_to_game_state(case.agent, case.action_type, case.tower_status)
    assert result == case.expected_output
```

## 4.테스트 실행 및 Git에 커밋
가상환경에서 pytest 명령어를 사용하여 테스트를 실행하고, 테스트가 통과하면 Git에 푸시합니다.

```bash
python3 -m pytest tests
```


