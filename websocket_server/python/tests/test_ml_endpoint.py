import pytest
from app.ml_endpoint import create_ml_feature, map_action_to_game_state
from typing import List, Dict
from pydantic import BaseModel

class MLFeatureTestCase(BaseModel):
    test_input_tower_status: Dict[str, Dict[str, str]]
    test_input_current_wave_index: int
    expected_output: List[int]

ml_feature_test_cases = [
    MLFeatureTestCase(
        test_input_tower_status={
            "1": {"type1": "2", "type2": "2"},
            "2": {"type1": "2", "type2": "2"},
            "3": {"type1": "2", "type2": "2"},
            "4": {"type1": "2", "type2": "2"},
        },
        test_input_current_wave_index=5,
        expected_output=[2, 0, 3, 2, 0, 3, 2, 0, 3, 2, 0, 3, 5],
    ),
    MLFeatureTestCase(
        test_input_tower_status={
            "1": {"type1": "1", "type2": "2"},
            "2": {"type1": "2", "type2": "0"},
            "3": {"type1": "1", "type2": "1"},
            "4": {"type1": "0", "type2": "0"},
        },
        test_input_current_wave_index=3,
        expected_output=[1, 1, 3, 1, 0, 4, 0, 2, 3, 0, 0, 5, 3],
    ),
    MLFeatureTestCase(
        test_input_tower_status={
            "1": {"type1": "0", "type2": "0"},
            "2": {"type1": "0", "type2": "0"},
            "3": {"type1": "0", "type2": "0"},
            "4": {"type1": "0", "type2": "0"},
        },
        test_input_current_wave_index=10,
        expected_output=[0, 0, 5, 0, 0, 5, 0, 0, 5, 0, 0, 5, 10],
    )
]

@pytest.mark.parametrize("case", ml_feature_test_cases)
def test_create_ml_feature(case: MLFeatureTestCase):
    result = create_ml_feature(case.test_input_tower_status, case.test_input_current_wave_index)
    assert result == case.expected_output




class MapActionTestCase(BaseModel):
    agent: str
    action_type: int
    tower_status: Dict[str, Dict[str, str]]
    expected_output: Dict

map_action_test_cases = [
    MapActionTestCase(
        agent="agent_1",
        action_type=0,
        tower_status={
            "1": {"type1": "2", "type2": "1"},
            "2": {"type1": "2", "type2": "0"},
            "3": {"type1": "1", "type2": "0"},
            "4": {"type1": "0", "type2": "0"},
        },
        expected_output={"agent": "agent_1", "action": "stay"},
    ),
    MapActionTestCase(
        agent="agent_1",
        action_type=1,
        tower_status={
            "1": {"type1": "2", "type2": "1"},
            "2": {"type1": "2", "type2": "0"},
            "3": {"type1": "1", "type2": "0"},
            "4": {"type1": "0", "type2": "0"},
        },
        expected_output={"agent": "agent_1", "action": "upgrade", "level": "1", "type": "type1"},
    ),
    MapActionTestCase(
        agent="agent_2",
        action_type=3,
        tower_status={
            "1": {"type1": "2", "type2": "1"},
            "2": {"type1": "2", "type2": "0"},
            "3": {"type1": "1", "type2": "2"},
            "4": {"type1": "0", "type2": "0"},
        },
        expected_output={"agent": "agent_2", "action": "upgrade", "level": "3", "type": "type2"},
    ),
    MapActionTestCase(
        agent="agent_3",
        action_type=4,
        tower_status={
            "1": {"type1": "1", "type2": "0"},
            "2": {"type1": "1", "type2": "1"},
            "3": {"type1": "1", "type2": "1"},
            "4": {"type1": "1", "type2": "2"},
        },
        expected_output={"agent": "agent_3", "action": "upgrade", "level": "4", "type": "type2"},
    )
]

@pytest.mark.parametrize("case", map_action_test_cases)
def test_map_action_to_game_state(case: MapActionTestCase):
    result = map_action_to_game_state(case.agent, case.action_type, case.tower_status)
    assert result == case.expected_output
