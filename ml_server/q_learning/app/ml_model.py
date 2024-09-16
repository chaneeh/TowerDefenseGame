import numpy as np
import io
import joblib
from itertools import product
from datetime import datetime

from .config import Config, config
from .utils import s3_client

state_size = (3 * 4 + 1) # [level n(1 ~ 4) X tower count(0 ~ 2), wave_index]
action_size = 4 # [stay, level 1 upgrade, level 2 upgrade, level 3 upgrade]

def generate_state_table():
    combinations_dict = {}
    combinations_list = []
    index = 0
    
    for w in range(5):
        max_threshold = 3 * w + 12
        for a1, b1, c1 in product(range(6), repeat=3):
            if a1 + b1 + c1 == 5:
                for a2, b2, c2 in product(range(6), repeat=3):
                    if a2 + b2 + c2 == 5:
                        for a3, b3, c3 in product(range(6), repeat=3):
                            if a3 + b3 + c3 == 5:
                                for a4, b4, c4 in product(range(6), repeat=3):
                                    if a4 + b4 + c4 == 5:
                                        value = 2 * (a1 + 2 * a2 + 4 * a3 + 8 * a4) + (b1 + 2 * b2 + 4 * b3 + 8 * b4)
                                        if value < max_threshold:
                                            combinations_dict[(a1, b1, c1, a2, b2, c2, a3, b3, c3, a4, b4, c4, w)] = index
                                            index += 1

                                            combinations_list.append((a1, b1, c1, a2, b2, c2, a3, b3, c3, a4, b4, c4, w))

    return combinations_dict, combinations_list

class Model:
    def __init__(self, config:Config = config, state_size:int = state_size, action_size:int = action_size, alpha=0.1, gamma=0.99):
        self.config = config
        self.state_size = state_size
        self.action_size = action_size
        self.alpha = alpha
        self.gamma = gamma
        self.initialize_Q()

        self.creation_time = datetime.now().strftime("%Y-%m-%d_%H:%M:%S")
        self.train_step_count = 0
        self.epsilon = None

    def set_epsilon(self, epsilon:float):
        self.epsilon = epsilon

    def initialize_Q(self):
        observation_dict, observation_list = generate_state_table()
        self.Q = np.zeros((len(observation_list), self.action_size))
        self.observation_dict = observation_dict
        self.observation_list = observation_list

        if self.config.get("pretrained_model") is not None:
            self.load_from_s3(ml_model_name = self.config.get("pretrained_model"))
        else:
            self.load_from_s3()


    def act(self, state:tuple, epsilon:float) -> int:
        
        epsilon = self.epsilon if self.epsilon is not None else epsilon
        
        agent_action = None
        if np.random.rand() < epsilon:
            print("Random action")
            agent_action = np.random.choice(self.action_size)
        else:
            state = self.observation_dict[state]
            agent_action = np.argmax(self.Q[state, :])
        
        return int(agent_action)
        
    def valid_act(self, state:tuple, epsilon:float) -> int:
        def _validate_act(state, action) -> bool:
            if action == 0:
                return True
            elif action == 1:
                return True if state[3 * (action - 1)] >= 1 else False
            elif action == 2:
                return True if state[3 * (action - 1)] >= 1 else False
            elif action == 3:
                return True if state[3 * (action - 1)] >= 1 else False
            else:
                return False
            
        is_validate_action = False
        while(is_validate_action == False):
            action = self.act(state, epsilon)
            is_validate_action = _validate_act(state, action)
            if is_validate_action == False:
                print(f"Invalid action {action} in state {state}, reselecting action...")
            else:
                print(f"Valid action {action} in state {state}")
        
        return action

    def train_step(self, states:list[list[int]], actions:list[int], rewards:list[float]) -> float:
        reward_diff = []
        for state, action, reward in zip(states, actions, rewards):
            G = reward
            state = self.observation_dict[state]
            reward_diff.append(abs(G - self.Q[state, action]))
            self.Q[state, action] = self.Q[state, action] + self.alpha * (G - self.Q[state, action])
        
        self.train_step_count = self.train_step_count + 1

        print(f"total updated states : {len(reward_diff)}")
        print(f"avg reward diff : {sum(reward_diff)/len(reward_diff)}")
        return sum(reward_diff)/len(reward_diff)

    def save_to_s3(self, bucket_name: str = config.get("S3_BUCKET_NAME"), key_prefix: str = "tower_defense/ml_models/q_learning"):                
        q_buffer = io.BytesIO()
        joblib.dump(self.Q, q_buffer)
        q_buffer.seek(0)

        s3_client.put_object(
            Bucket=bucket_name, 
            Key=f'{key_prefix}/{self.creation_time}_{self.train_step_count}_Q.pkl',
            Body=q_buffer.getvalue()
        )

        return 

    def load_from_s3(self, ml_model_name: str = None, bucket_name: str = config.get("S3_BUCKET_NAME"), key_prefix: str = "tower_defense/ml_models/q_learning"):
        if ml_model_name:
            target_key = f"{key_prefix}/{ml_model_name}"
        else:
            response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=key_prefix)

            objects = response['Contents']
            objects.sort(key=lambda obj: obj['LastModified'], reverse=True)

            target_key = objects[0]['Key']

        q_object = s3_client.get_object(Bucket=bucket_name, Key=target_key)
        q_buffer = io.BytesIO(q_object['Body'].read())
        self.Q = joblib.load(q_buffer)

        print(f"Latest model {target_key} loaded successfully")

        return
    
    def search_action_space(self, state: tuple) -> list[float]:
        """
        주어진 state에 해당하는 모든 action의 Q-value를 반환합니다.
        :param state: tuple, 상태를 나타내는 tuple
        :return: list, 주어진 상태에 대한 모든 행동의 Q-value
        """
        if state in self.observation_dict:
            state_index = self.observation_dict[state]
            action_values = self.Q[state_index, :]
            return action_values.tolist()
        else:
            raise ValueError(f"State {state} is not valid or not in the Q-table")