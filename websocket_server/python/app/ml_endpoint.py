from pydantic import BaseModel
from typing import Tuple, Dict
import httpx
from fastapi import HTTPException

from .config import config

class PredictRequest(BaseModel):
    state : Tuple[int, ...]
    epsilon : float = 0.0 

class PredictResponse(BaseModel):
    action : int

def create_ml_feature(tower_status_feature:dict[str, dict[str, str]], current_wave_index:int) -> Tuple[int, ...]:
    feature_list = []
    for level_feature in [1,2,3,4]:
        feature_list_by_level = [0, 0, 5] # [num of types with num 2, num of types with num 1, num of types with num 0]
        for key, value in tower_status_feature.get(str(level_feature), {}).items():
            if value == "2":
                feature_list_by_level[0] = feature_list_by_level[0] + 1
            elif value == "1":
                feature_list_by_level[1] = feature_list_by_level[1] + 1
        feature_list_by_level[2] = 5 - (feature_list_by_level[0] + feature_list_by_level[1])
        feature_list.extend(feature_list_by_level)
    feature_list = feature_list + [current_wave_index]
    return feature_list

def map_action_to_game_state(agent:str, action_type:int, tower_status:dict[str, dict[str, str]]) -> Dict:    
    def _find_tower_type_to_upgrade(tower_status, level, count = 2) -> int:
        for type, type_count in tower_status[str(level)].items():
            if int(type_count) == count:
                return type
        return None
    
    if action_type == 0:
        action_item = {
            'agent' : agent, 
            "action" : "stay"
        }
        return action_item
    else:
        action_item = {
            'agent' : agent, 
            'action' : "upgrade", 
            'level' : str(action_type), 
            'type' : str(_find_tower_type_to_upgrade(tower_status, action_type))
        }
        return action_item
    
async def ml_request_endpoint(data_model: dict) -> Tuple[dict, PredictRequest, PredictResponse]:
    ml_server_url = config.get("ML_SERVER_URL")

    ml_request = PredictRequest(
        state = create_ml_feature(data_model["current_towerstatus"], data_model["currentWaveIndex"])
    )

    async with httpx.AsyncClient() as client:
        try:
            print(f"ML request: {ml_request.model_dump()}")
            response = await client.post(f"{ml_server_url}/predict", json=ml_request.model_dump())
            response.raise_for_status()
        except Exception as e:
            print("Unexpected error occurred: ", e)
            raise HTTPException(status_code=500, detail=f"Unexpected error occurred: {e}")
        
    ml_response = PredictResponse(**response.json())
    print(f"ML action: {ml_response.model_dump()}")

    action_item = map_action_to_game_state(data_model["agent"], ml_response.action, data_model["current_towerstatus"])

    return action_item, ml_request, ml_response