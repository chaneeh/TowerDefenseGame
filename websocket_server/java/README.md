학습 목적으로 java로 변환 작업을 진행하고 있습니다. prod에는 python 버전 서버가 사용되고 있습니다.


### ws server 테스트 코드 작성

#### 1. 테스트 코드 위치
아래 위치에 테스트 코드 작성하면 됩니다

```markdown
src
└── tests
    └── java
        └── com
            └── example
                └── helloword     <---- 테스트 코드 작성
                    ├── controller
                    │   └── WebSocketControllerTest.java
                    └── service
                        └── WebSocketServiceTest.java

```

#### 2. Example Test Code

현재  **controller** 와 **service**에 대해 테스트 코드가 작성되어 있습니다.

**Controller Test:**

```java
package com.example.helloworld.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.assertj.core.api.Assertions.assertThat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloWorldApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void helloWorldEndpointReturnsHelloWorld() {
        // "/hello" 엔드포인트에 GET 요청을 보내고 결과를 받아옵니다.
        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);
        
        // 응답이 "Hello World!"인지 확인합니다.
        assertThat(response.getBody()).isEqualTo("Hello World!");
    }
}

```

**Service utils Test:**

```java
...
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
            )
            ... // get add multiple examples
        );
    }

    @ParameterizedTest
    @MethodSource("createMlFeatureTestCases")
    void testCreateMlFeature(MLFeatureTestCase testCase) {
        List<Integer> result = mlService.createMlFeature(testCase.getTestInputTowerStatus(), testCase.getTestInputCurrentWaveIndex());
        assertEquals(testCase.getExpectedOutput(), result);
    }

```

#### 3. Running the Tests

```bash
mvn test
```

### Additional Notes:
- prod와 test 버전을 다르게 관리하고 싶다면 `application-test.properties` 을 활용해도 됩니다.