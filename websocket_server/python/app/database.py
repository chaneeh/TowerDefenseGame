# database session
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from sqlalchemy.pool import NullPool

# database table
from sqlalchemy import Column, Integer, String, ARRAY, TIMESTAMP
from datetime import datetime, timedelta

from .config import config

DATABASE_URL = config.get("DB_HOST")

engine = create_engine(
    DATABASE_URL,
    poolclass = NullPool,
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

class GameActionLog(Base):
    __tablename__ = config.get("Table_GameActionLog")
    id = Column(Integer, primary_key=True, index=True)
    client_id = Column(String, nullable=False)
    game_start_timestamp = Column(TIMESTAMP, nullable=False)
    client_timestamp = Column(TIMESTAMP, nullable=False)
    server_timestamp = Column(TIMESTAMP, nullable=False)
    state = Column(ARRAY(Integer), nullable=False)
    action = Column(Integer, nullable=False)

class GameActionLogReward(Base):
    __tablename__ = config.get("Table_GameActionLogReward")
    id = Column(Integer, primary_key=True, index=True)
    client_id = Column(String, nullable=False)
    game_start_timestamp = Column(TIMESTAMP, nullable=False)
    client_timestamp = Column(TIMESTAMP, nullable=False)
    server_timestamp = Column(TIMESTAMP, nullable=False)
    rewards = Column(ARRAY(Integer), nullable=False)


def db_insert_data(table_name:str, data:dict):
    if table_name == "game_action_log":
        db_log = GameActionLog(
            client_id = data["client_id"],
            game_start_timestamp = data["game_start_timestamp"],
            client_timestamp = data["client_timestamp"],
            server_timestamp = (datetime.utcnow() + timedelta(hours=9)),
            state = data["state"],
            action = data["action"],
        )
    elif table_name == "game_action_log_reward":
        db_log = GameActionLogReward(
            client_id = data["client_id"],
            game_start_timestamp = data["game_start_timestamp"],
            client_timestamp = data["client_timestamp"],
            server_timestamp = (datetime.utcnow() + timedelta(hours=9)),
            rewards = data["rewards"],
        )
    else: # other tables
        pass

    db = SessionLocal()
    try:
        db.add(db_log)
        db.commit()
        db.refresh(db_log)
    finally:
        db.close()

    return



