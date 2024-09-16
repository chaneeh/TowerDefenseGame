import os
from dotenv import load_dotenv 

class Config:
    def __init__(self, deploy_mode):
        self.deploy_mode = deploy_mode
        self.load_config()
    
    def load_config(self):
        if self.deploy_mode == 'prod':
            self.config = self.prod_config()
        else:
            raise ValueError('Invalid deploy_mode')
        
    def prod_config(self):
        return {
            "api_path" : "simulator-prod",
            "redis_host" :  os.getenv("REDIS_HOST"),
            "redis_port" : os.getenv("REDIS_PORT"),
            "postgres_host" : os.getenv("DB_HOST")
        }
    
    def get(self, key):
        return self.config.get(key, None)
    
load_dotenv()
DEPLOY_MODE = os.getenv('DEPLOY_MODE')
config = Config(DEPLOY_MODE)