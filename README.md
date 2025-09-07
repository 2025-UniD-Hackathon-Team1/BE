## Price Watcher 
> 상품의 가격과 정보를 주기적으로 수집하여 검색/상세/가격 변동 이력을 확인할 수 있는 서비스

### ✨ 주요 기능
- 상품 검색
- 상품 상세 조회
- 상품 가격 변동 이력 확인 

### 🕹️ 아키텍쳐 동작 과정 
1. EventBridge → Lambda (매 시간 실행)
2. Lambda → S3 저장
3. Spring Batch → RDS 반영
4. API 제공

### 🛠️ 기술 스택
- Backend : Spring Boot, Spring Batch, JPA
- Database : MySQL
- Infra : AWS Lambda, EventBridge, S3, EC2,
  
