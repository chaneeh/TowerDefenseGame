from celery import Celery
import subprocess
from .config import config

celery = Celery(__name__, broker=f'redis://{config.get("redis_host")}:{config.get("redis_port")}/0', backend=f'redis://{config.get("redis_host")}:{config.get("redis_port")}/0')

celery.conf.update(
    worker_prefetch_multiplier=1,
    task_acks_late=True,
    task_reject_on_worker_lost=True
)

@celery.task(name="app.add", time_limit=360)
def add(game_name: str):

    unity_executable_path = "/app/app/unity/tower_defense.x86_64"
    # Unity 실행 옵션
    args = [
        unity_executable_path,
        "-batchmode",
        "-nographics",
    ]

    process = subprocess.Popen(args, stdout=subprocess.PIPE, stderr=subprocess.PIPE)

    try:
        stdout, stderr = process.communicate(timeout=300)
        print("Standard Output:")
        print(stdout.decode())
        print("Standard Error:")
        print(stderr.decode())
    except subprocess.TimeoutExpired:
        process.terminate()
        try:
            stdout, stderr = process.communicate(timeout=10)
        except subprocess.TimeoutExpired:
            process.kill()
            stdout, stderr = process.communicate()

    return game_name

