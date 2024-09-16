from fastapi import FastAPI
from celery.result import AsyncResult
from .worker import celery
from .database import db_insert_data
import redis

from .config import config

api_path = config.get("api_path")
app = FastAPI()
redis_client = redis.Redis(host=config.get("redis_host"),port=config.get("redis_port"))

def redis_queue_length():
    queue_length = redis_client.llen('celery')

    return queue_length

def celery_active_count():
    i = celery.control.inspect()
    active_tasks = i.active()
    active_count = sum(len(tasks) for tasks in active_tasks.values()) if active_tasks else 0

    return active_count

@app.get(f"/{api_path}/")
async def root():
    return {"message": "Hello World"}

@app.get(f"/{api_path}/health")
async def health_check():
    return {"status": "ok"}

@app.get(f"/{api_path}/add/{{game_name}}")
async def add_task(game_name: str):
    task = celery.send_task("app.add", args=[game_name])

    db_insert_data({
        "redis_queue_length": redis_queue_length(),
        "celery_active_count": celery_active_count()
    })

    return {"task_id": task.id}

@app.get(f"/{api_path}/status/{{task_id}}")
async def get_status(task_id: str):
    task_result = AsyncResult(task_id, app=celery)
    return {"task_id": task_id, "status": task_result.status, "result": task_result.result}
