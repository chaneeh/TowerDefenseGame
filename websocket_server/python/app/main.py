from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from typing import Optional
from pydantic import BaseModel, Field, field_validator
from datetime import datetime, timedelta

import json
from .config import config
from .database import db_insert_data

from .ml_endpoint import ml_request_endpoint

app = FastAPI()

@app.get(f"/{config.get('api_path')}/health")
async def health_check():
    return {"status": "ok!"}


class TowerDefenseMessage(BaseModel):
    agent: str
    type_1: str
    type_2: str
    currentGold: int 
    currentWaveIndex: int 
    strategy: str 
    data: str
    client_id: str 
    client_timestamp_unix: str
    client_timestamp: datetime = Field(default=None, init=False)
    game_start_timestamp_unix: str 
    game_start_timestamp: datetime = Field(default=None, init=False)
    current_towerstatus: dict = Field(default=None, init=False)

    def model_post_init(self, __context):
        self.client_timestamp = (datetime.fromtimestamp((float(self.client_timestamp_unix) / 1000.0)) + timedelta(hours=9))
        self.game_start_timestamp = (datetime.fromtimestamp((float(self.game_start_timestamp_unix) / 1000.0)) + timedelta(hours=9))
        self.current_towerstatus = json.loads(self.data) if self.type_1 == 'tower' else None

    @field_validator('agent')
    def check_agent(cls, v):
        if v not in (['PlayerStats', 'TowerSpawner', 'EnemySpawner'] + ['PlayerStatsAI', 'TowerSpawnerAI', 'EnemySpawnerAI']):
            raise ValueError('agent check failed')
        return v

    @field_validator('type_1')
    def check_type_1(cls, v):
        if v not in ['gold', 'tower', 'wave']:
            raise ValueError('type_1 check failed')
        return v

    @field_validator('strategy')
    def check_strategy(cls, v):
        if v not in ['greedy', 'ml']:
            raise ValueError('strategy check failed')
        return v

class TowerDefenseResponse(BaseModel):
    agent: str
    action: str
    level: Optional[str] = None
    type: Optional[str] = None

    @field_validator('agent')
    def check_agent(cls, v):
        if v not in ['PlayerStatsAI', 'TowerSpawnerAI', 'EnemySpawnerAI']:
            raise ValueError('agent check failed')
        return v
    
    @field_validator('action')
    def check_action(cls, v):
        if v not in ['buy', 'upgrade', 'stay']:            
            raise ValueError('action check failed')
        return v

async def handle_gold_msg(data_model: TowerDefenseMessage, websocket: WebSocket):
    response = TowerDefenseResponse(
        agent = data_model.agent,
        action = "buy" if int(data_model.data) > 0 else "stay"
    )
    await websocket.send_text(response.model_dump_json())
    print(f"Send to client: {str(response.model_dump_json())}")


async def handle_tower_ml_msg(data_model: TowerDefenseMessage, websocket: WebSocket):

    action_item, ml_request, ml_response = await ml_request_endpoint(data_model.dict())

    response = TowerDefenseResponse(
        agent = action_item['agent'],
        action = action_item['action'],
        level = action_item.get('level', None),
        type = action_item.get('type', None)
    )

    await websocket.send_text(response.json())
    print(f"Send to client: {str(response.json())}")

    db_insert_data(
        table_name="game_action_log",
        data={
            'client_id' : data_model.client_id,
            'game_start_timestamp' : data_model.game_start_timestamp,
            'client_timestamp' : data_model.client_timestamp,
            'state' : ml_request.state,
            'action' : ml_response.action
        }
    )


async def handle_wave_msg(data_model: TowerDefenseMessage, websocket: WebSocket):
    wave_time_list = data_model.data.split(" ")
    wave_reward = []
    current_wave_reward = 0
    for wave_time_str in wave_time_list:
        current_wave_reward = (40 - float(wave_time_str))
        wave_reward.append(current_wave_reward)

    db_insert_data(
        table_name="game_action_log_reward",
        data={
            'client_id' : data_model.client_id,
            'game_start_timestamp' : data_model.game_start_timestamp,
            'client_timestamp' : data_model.client_timestamp,
            'rewards' : wave_reward
        }
    )

def is_agent_ai(agent):
    if agent == "PlayerStatsAI":
        return True
    if agent == "TowerSpawnerAI":
        return True
    if agent == "EnemySpawnerAI":
        return True
    return False

@app.websocket(f"/{config.get('api_path')}/ws/{{client_id}}")
async def websocket_endpoint(websocket: WebSocket, client_id: str):
    await websocket.accept()
    print(f"client {client_id} connected")
    try:
        while True:
            data_text = await websocket.receive_text()
            raw_data_text = data_text.replace('\\', '')
            print(f"Receive client: {raw_data_text}")

            try:
                data_model = TowerDefenseMessage.parse_raw(data_text)
            except Exception as e:
                print(f"Invalid message: {e}")
                continue

            if is_agent_ai(data_model.agent) == True:
                if data_model.type_1 == "gold":
                    await handle_gold_msg(data_model, websocket)
                
                if data_model.type_1 == "tower":
                    if data_model.strategy == "ml":
                        await handle_tower_ml_msg(data_model, websocket)
                    else:
                        print(f"Unknown strategy: {data_model.strategy}")
                        pass
                
                if data_model.type_1 == "wave":
                    await handle_wave_msg(data_model, websocket)

            else:
                pass

    except WebSocketDisconnect:
        print("Client disconnected")