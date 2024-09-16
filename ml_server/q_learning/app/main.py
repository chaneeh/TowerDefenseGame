from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List, Tuple

from .config import config
from .ml_model import Model

app = FastAPI()
model = Model()

@app.get(f"/{config.get('api_path')}/health")
async def health_check():
    return {"status": "ok"}

class PredictRequest(BaseModel):
    state : Tuple[int, ...]
    epsilon : float = 0.0 

class PredictResponse(BaseModel):
    action : int

@app.post(f"/{config.get('api_path')}/predict", response_model=PredictResponse)
def predict(request: PredictRequest) -> PredictResponse:
    print("request: ", request)

    action = model.valid_act(request.state, request.epsilon)
    
    return PredictResponse(action = action)



class MLTrainRequest(BaseModel):
    states: list[Tuple[int, ...]]
    actions: list[int]
    rewards: list[float]

class MLTrainResponse(BaseModel):
    train_result: float

@app.post(f"/{config.get('api_path')}/train", response_model=MLTrainResponse)
def train(request: MLTrainRequest) -> MLTrainResponse:

    train_result = model.train_step(
        states = request.states,
        actions = request.actions,
        rewards = request.rewards
    )

    model.save_to_s3()

    return MLTrainResponse(
        train_result = train_result
    )



class SetEpsilonRequest(BaseModel):
    epsilon: float

@app.post(f"/{config.get('api_path')}/set_epsilon")
def set_epsilon(request: SetEpsilonRequest):
    model.set_epsilon(request.epsilon)
    return {"message": "Epsilon set successfully"}



class LoadModelRequest(BaseModel):
    ml_model_name: str

@app.post(f"/{config.get('api_path')}/load_model")
def load_model(request: LoadModelRequest):
    try:
        model.load_from_s3(
            ml_model_name = LoadModelRequest.ml_model_name
        )
        return True 
    except:
        return False



class SearchActionSpaceRequest(BaseModel):
    state : Tuple[int, ...]

class SearchActionSpaceResponse(BaseModel):
    action : list[float]

@app.post(f"/{config.get('api_path')}/search_action_space", response_model=SearchActionSpaceResponse)
def search_action_space(request: SearchActionSpaceRequest) -> SearchActionSpaceResponse:
    try:
        action = model.search_action_space(request.state)
        return SearchActionSpaceResponse(action=action)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))