# OpenJDK 17 기반
FROM openjdk:17

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 결과물 복사
COPY build/libs/practice.jar app.jar

# 컨테이너 시작 시 실행할 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
