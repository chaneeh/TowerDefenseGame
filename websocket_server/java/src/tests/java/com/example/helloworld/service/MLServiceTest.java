package com.example.helloworld.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@SpringBootTest
public class MLServiceTest {

    @Autowired
    private MLService mlService; // 실제 서비스 객체

    // MLFeatureTestCase 클래스: createMlFeature 테스트 케이스를 위한 구조
    public static class MLFeatureTestCase {
        private Map<String, Map<String, String>> testInputTowerStatus;
        private int testInputCurrentWaveIndex;
        private List<Integer> expectedOutput;

        public MLFeatureTestCase(Map<String, Map<String, String>> testInputTowerStatus, int testInputCurrentWaveIndex, List<Integer> expectedOutput) {
            this.testInputTowerStatus = testInputTowerStatus;
            this.testInputCurrentWaveIndex = testInputCurrentWaveIndex;
            this.expectedOutput = expectedOutput;
        }

        public Map<String, Map<String, String>> getTestInputTowerStatus() {
            return testInputTowerStatus;
        }

        public int getTestInputCurrentWaveIndex() {
            return testInputCurrentWaveIndex;
        }

        public List<Integer> getExpectedOutput() {
            return expectedOutput;
        }
    }

    // MapActionTestCase 클래스: mapActionToGameState 테스트 케이스를 위한 구조
    public static class MapActionTestCase {
        private String agent;
        private int actionType;
        private Map<String, Map<String, String>> towerStatus;
        private Map<String, Object> expectedOutput;

        public MapActionTestCase(String agent, int actionType, Map<String, Map<String, String>> towerStatus, Map<String, Object> expectedOutput) {
            this.agent = agent;
            this.actionType = actionType;
            this.towerStatus = towerStatus;
            this.expectedOutput = expectedOutput;
        }

        public String getAgent() {
            return agent;
        }

        public int getActionType() {
            return actionType;
        }

        public Map<String, Map<String, String>> getTowerStatus() {
            return towerStatus;
        }

        public Map<String, Object> getExpectedOutput() {
            return expectedOutput;
        }
    }

    // createMlFeature 테스트 케이스 제공 메서드
    static Stream<MLFeatureTestCase> createMlFeatureTestCases() {
        return Stream.of(
            new MLFeatureTestCase(
                Map.of(
                    "1", Map.of("type1", "2", "type2", "2"),
                    "2", Map.of("type1", "2", "type2", "2"),
                    "3", Map.of("type1", "2", "type2", "2"),
                    "4", Map.of("type1", "2", "type2", "2")
                ),
                5,
                Arrays.asList(2, 0, 3, 2, 0, 3, 2, 0, 3, 2, 0, 3, 5)
            ),
            new MLFeatureTestCase(
                Map.of(
                    "1", Map.of("type1", "1", "type2", "2"),
                    "2", Map.of("type1", "2", "type2", "0"),
                    "3", Map.of("type1", "1", "type2", "1"),
                    "4", Map.of("type1", "0", "type2", "0")
                ),
                3,
                Arrays.asList(1, 1, 3, 1, 0, 4, 0, 2, 3, 0, 0, 5, 3)
            ),
            new MLFeatureTestCase(
                Map.of(
                    "1", Map.of("type1", "0", "type2", "0"),
                    "2", Map.of("type1", "0", "type2", "0"),
                    "3", Map.of("type1", "0", "type2", "0"),
                    "4", Map.of("type1", "0", "type2", "0")
                ),
                10,
                Arrays.asList(0, 0, 5, 0, 0, 5, 0, 0, 5, 0, 0, 5, 10)
            )
        );
    }

    // mapActionToGameState 테스트 케이스 제공 메서드
    static Stream<MapActionTestCase> mapActionToGameStateTestCases() {
        return Stream.of(
            new MapActionTestCase(
                "agent_1", 0,
                Map.of(
                    "1", Map.of("type1", "2", "type2", "1"),
                    "2", Map.of("type1", "2", "type2", "0"),
                    "3", Map.of("type1", "1", "type2", "0"),
                    "4", Map.of("type1", "0", "type2", "0")
                ),
                Map.of("agent", "agent_1", "action", "stay")
            ),
            new MapActionTestCase(
                "agent_1", 1,
                Map.of(
                    "1", Map.of("type1", "2", "type2", "1"),
                    "2", Map.of("type1", "2", "type2", "0"),
                    "3", Map.of("type1", "1", "type2", "0"),
                    "4", Map.of("type1", "0", "type2", "0")
                ),
                Map.of("agent", "agent_1", "action", "upgrade", "level", "1", "type", "type1")
            ),
            new MapActionTestCase(
                "agent_2", 3,
                Map.of(
                    "1", Map.of("type1", "2", "type2", "1"),
                    "2", Map.of("type1", "2", "type2", "0"),
                    "3", Map.of("type1", "1", "type2", "2"),
                    "4", Map.of("type1", "0", "type2", "0")
                ),
                Map.of("agent", "agent_2", "action", "upgrade", "level", "3", "type", "type2")
            )
        );
    }

    // createMlFeature에 대한 ParameterizedTest
    @ParameterizedTest
    @MethodSource("createMlFeatureTestCases")
    void testCreateMlFeature(MLFeatureTestCase testCase) {
        List<Integer> result = mlService.createMlFeature(testCase.getTestInputTowerStatus(), testCase.getTestInputCurrentWaveIndex());
        assertEquals(testCase.getExpectedOutput(), result);
    }

    // mapActionToGameState에 대한 ParameterizedTest
    @ParameterizedTest
    @MethodSource("mapActionToGameStateTestCases")
    void testMapActionToGameState(MapActionTestCase testCase) {
        Map<String, Object> result = mlService.mapActionToGameState(testCase.getAgent(), testCase.getActionType(), testCase.getTowerStatus());
        assertEquals(testCase.getExpectedOutput(), result);
    }
}
