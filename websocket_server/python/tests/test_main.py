from app.main import app
from fastapi.testclient import TestClient

client = TestClient(app)

def test_read_root():
    response = client.get("/websocket-prod/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok!"}