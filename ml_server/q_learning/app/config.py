import os
from dotenv import load_dotenv

class Config:
    def __init__(self, deploy_mode):
        self.deploy_mode = deploy_mode
        self.load_config()

    def load_config(self):
        print("DEPLOY MODE : ", self.deploy_mode)
        if self.deploy_mode == "prod":
            self.config = self.prod_config()
        elif self.deploy_mode == "train":
            self.config = self.train_config()
        else:
            raise ValueError("Invalid deploy_mode")
    
    def prod_config(self):
        return {
            "api_path" : "ml-server-prod",
            "AWS_S3_ACCESS_KEY" : os.getenv('AWS_S3_ACCESS_KEY'),
            "AWS_S3_SECRET_KEY" : os.getenv('AWS_S3_SECRET_KEY'),
            "S3_BUCKET_NAME" : os.getenv('S3_BUCKET_NAME')
        }
    
    def train_config(self):
        return {
            "api_path" : "ml-server-train",
            "AWS_S3_ACCESS_KEY" : os.getenv('AWS_S3_ACCESS_KEY'),
            "AWS_S3_SECRET_KEY" : os.getenv('AWS_S3_SECRET_KEY'),
            "S3_BUCKET_NAME" : os.getenv('S3_BUCKET_NAME')
        }
    def get(self, key):
        return self.config.get(key, None)

load_dotenv()
DEPLOY_MODE = os.getenv('DEPLOY_MODE')
config = Config(DEPLOY_MODE)