# database session
from databases import Database
from sqlalchemy import create_engine, MetaData
from sqlalchemy.orm import sessionmaker, Session, declarative_base
from sqlalchemy.pool import NullPool

# database table
from sqlalchemy import Table, Column, Integer, String, ARRAY, TIMESTAMP
from datetime import datetime, timedelta, timezone

from .config import config

DATABASE_URL = config.get("postgres_host")

engine = create_engine(
    DATABASE_URL,
    #pool_size=1,
    poolclass = NullPool,
    #max_overflow=10,
)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db() -> Session:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


Base = declarative_base()

class SimulatorTaskInfo(Base):
    __tablename__ = "simulator_tasks_info"
    id = Column(Integer, primary_key=True, index=True)
    update_timestamp = Column(TIMESTAMP, nullable=False)
    keda_target_value = Column(Integer, nullable=False)
    redis_queue_length = Column(Integer, nullable=False)
    celery_active_count = Column(Integer, nullable=False)


def db_insert_data(data:dict):
    db_log = SimulatorTaskInfo(
        update_timestamp = (datetime.now(timezone.utc) + timedelta(hours=9)),
        keda_target_value = data["redis_queue_length"] + data["celery_active_count"],
        redis_queue_length = data["redis_queue_length"],
        celery_active_count = data["celery_active_count"]
    )

    db = SessionLocal()
    try:
        db.add(db_log)
        db.commit()
        db.refresh(db_log)
    finally:
        db.close()

    return



