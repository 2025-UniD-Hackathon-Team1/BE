package practice.deploy.report.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import practice.deploy.report.dto.request.UpstageMessage;
import practice.deploy.report.dto.request.UpstageRequest;
// UpstageResponse 대신 AI가 생성한 최종 JSON 구조를 받기 위한 DTO를 가정합니다.
import practice.deploy.report.dto.response.FinalAnalysisJson;
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson ObjectMapper 추가
import practice.deploy.report.dto.response.UpstageResponse;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UpstageApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper; // 💡 추가: JSON 파싱을 위한 ObjectMapper 주입

    // DTO 구조를 사용하지만, 최종 응답은 JSON 문자열 전체가 아닌 '최소sleepTime' 값입니다.
    // 따라서 반환 타입을 String으로 유지합니다.
    public String getMinSleepTimeJson(String finalPrompt) { // 💡 메서드명 변경 (목표 반영)
        log.info("Starting Upstage API call for min sleep time analysis.");

        // 1. 요청 본문 구성
        UpstageRequest requestBody = new UpstageRequest(
                "solar-pro2",
                List.of(new UpstageMessage("user", finalPrompt)),
                3000
        );

        try {
            // 1. WebClient를 이용한 POST 요청 실행
            // 💡 수정: 응답 타입을 UpstageResponse.class로 지정 (가장 효율적인 방식)
            Mono<UpstageResponse> responseMono = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(UpstageResponse.class); // Upstage 표준 응답 DTO로 받습니다.

            // 2. 응답 처리 및 JSON 추출
            UpstageResponse upstageResponse = responseMono.block();

            if (upstageResponse == null || upstageResponse.choices() == null || upstageResponse.choices().isEmpty()) {
                throw new RuntimeException("API 응답은 받았으나, choices 필드에 내용이 없습니다.");
            }

            // 3. LLM이 생성한 최종 JSON 문자열 추출 (응답 본체에서 추출)
            String llmGeneratedJson = upstageResponse.choices().get(0).message().content();

            // 4. JSON 문자열 클렌징 및 파싱
            if (llmGeneratedJson == null || llmGeneratedJson.isEmpty()) {
                throw new RuntimeException("LLM 응답 내용(content)이 비어있습니다.");
            }

            // 💡 수정: JSON 마크다운(```json) 제거
            String cleanJson = llmGeneratedJson.replaceAll("```json|```", "").trim();

            // 5. 최종 목표 JSON 구조(FinalAnalysisJson)로 파싱 (주입받은 objectMapper 사용)
            FinalAnalysisJson finalResult = objectMapper.readValue(cleanJson, FinalAnalysisJson.class);

            log.info("Successfully extracted minimum sleep time: {}", finalResult.leastSleepTime());

            // 6. 최종 값만 String으로 반환
            return finalResult.leastSleepTime();

        } catch (JsonProcessingException e) {
            log.error("Final JSON Parsing Failed. Check DTO or LLM output format. Clean String: '{}'",
                    e.getMessage(), e);
            throw new RuntimeException("AI 응답 JSON 파싱 실패 (내부 JSON 형식 오류).", e);
        } catch (WebClientResponseException e) {
            log.error("Upstage API HTTP Error. Status: {}, Response: {}", e.getStatusCode().value(), e.getResponseBodyAsString(), e);
            String errorMessage = String.format("Upstage API 호출 오류. Status: %d. %s", e.getStatusCode().value(), e.getStatusText());
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            log.error("API call failed due to unexpected error.", e);
            throw new RuntimeException("API 호출 중 예상치 못한 오류 발생: " + e.getMessage(), e);
        }
    }
}