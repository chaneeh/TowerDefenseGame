# Manifests

이 폴더는 Argo CD를 통해 Kubernetes 클러스터에 애플리케이션을 배포할 때 사용되는 Kubernetes manifests 파일들을 포함하고 있습니다. Argo CD의 Code Deploy 과정에서 필요한 모든 설정 파일들이 이곳에 저장됩니다.

## 폴더 구조

`manifests` 폴더에는 Kubernetes 리소스 정의 파일들이 위치하며, 다음과 같은 파일들이 포함되어 있습니다:

```plaintext
manifests/
│
├── ml_server_{prod/train}_{deployment/service}.yaml  # ml server pod의 Deployment, Service 정의
├── websocket_{prod/train}_{deployment/service}.yaml  # ws server pod의 Deployment, Service 정의
├── simulator_celery_prod_deployment.yaml             # simulator 역할을 하는 celery worker pod의 Deployment 정의
├── simulator_scaling_keda.yaml                       # simulator pod scaling하는 scaledobject 정의
├── simulator_fastapi_prod_deployment.yaml            # task enqueue server pod의 Deployment 정의
├── ingress.yaml                                      # Ingress 리소스 정의
└── secret.yaml                                       # Secret 리소스 정의
```