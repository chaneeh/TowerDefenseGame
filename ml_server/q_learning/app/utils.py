import boto3
from .config import config

s3_client = boto3.client(
    's3',
    aws_access_key_id=config.get('AWS_S3_ACCESS_KEY'),
    aws_secret_access_key=config.get('AWS_S3_SECRET_KEY')
)