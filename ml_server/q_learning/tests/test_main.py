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
    
def test_search_action_space():
    state = (0, 4, 1, 0, 0, 5, 0, 0, 5, 0, 0, 5, 1)
    response = client.post("/ml-server-prod/search_action_space", json={"state": state})
    assert response.status_code == 200
