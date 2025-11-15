package practice.deploy.report.util;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.annotation.PostConstruct;
import practice.deploy.report.dto.upstage.UpstageMessage;
import practice.deploy.report.dto.upstage.UpstageRequest;
import practice.deploy.report.dto.upstage.UpstageResponse;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UpstageService {

    private final WebClient webClient;

    public UpstageService(WebClient upstageWebClient) {
        this.webClient = upstageWebClient;
    }

    @PostConstruct
    public void testUpstageApiCall() {
        System.out.println("------------------------------------");
        System.out.println("--- Upstage API 연결 테스트 시작 ---");
        System.out.println("------------------------------------");

        String testPrompt = "너는 친절한 AI 조수야. Spring Boot 환경에서 WebClient를 사용하여 너를 호출했어. 환영 메시지를 짧게 알려줘.";

        UpstageRequest request = new UpstageRequest(
                "solar-mini",
                List.of(new UpstageMessage("user", testPrompt)),
                256
        );

        // 2. API 호출
        webClient.post()
                .uri("/chat/completions")
                .bodyValue(request)
                .retrieve()
                // 에러 응답 처리 (HTTP 4xx, 5xx 에러 발생 시)
                .onStatus(status -> status.isError(), clientResponse -> {
                    System.err.println("❌ API 호출 실패: HTTP Status " + clientResponse.statusCode());
                    // 에러 본문을 읽어와서 상세 오류를 출력합니다.
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("API Error Body: " + errorBody)));
                })
                .bodyToMono(UpstageResponse.class)
                .doOnError(e -> {
                    System.err.println("❌ WebClient 통신 오류 발생: " + e.getMessage());
                    System.err.println(">> API 키, URL, 또는 네트워크 연결을 확인하세요.");
                })
                .subscribe(response -> {
                    // 3. 응답 결과 출력
                    if (response != null && !response.choices().isEmpty()) {
                        String generatedText = response.choices().get(0).message().content();
                        System.out.println("✅ API 호출 성공 및 응답 수신");
                        System.out.println("--- AI 응답 결과 ---");
                        System.out.println(generatedText);
                        System.out.println("---------------------");
                    } else {
                        System.err.println("⚠️ API 호출은 성공했으나, AI 응답 내용이 비어있습니다.");
                    }
                });
    }
}