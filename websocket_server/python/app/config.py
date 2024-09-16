import os
from dotenv import load_dotenv 

class Config:
    def __init__(self, deploy_mode):
        self.deploy_mode = deploy_mode
        self.load_config()

    def load_config(self):
        if self.deploy_mode == 'prod':
            self.config = self.prod_config()
        elif self.deploy_mode == 'train':
            self.config = self.train_config()
        else:
            raise ValueError(f"Unknown deploy mode: {self.deploy_mode}")

    def prod_config(self):
        return {
            "api_path" : os.getenv("WSS_SERVER_PROD_PATH"),
            "ML_SERVER_URL" : os.getenv('ML_SERVER_PROD_URL'),
            "Table_GameActionLog" : "game_action_log",
            "Table_GameActionLogReward" : "game_action_log_reward",
            "DB_HOST" : os.getenv('DB_HOST')
        }
    
    def train_config(self):
        return {
            "api_path" : os.getenv("WSS_SERVER_TRAIN_PATH"),
            "ML_SERVER_URL" : os.getenv('ML_SERVER_TRAIN_URL'),
            "Table_GameActionLog" : "game_action_log_train",
            "Table_GameActionLogReward" : "game_action_log_reward_train",
            "DB_HOST" : os.getenv('DB_HOST')
        }

    def get(self, key):
        return self.config.get(key, None)

load_dotenv()
DEPLOY_MODE = os.getenv('DEPLOY_MODE')
config = Config(DEPLOY_MODE)