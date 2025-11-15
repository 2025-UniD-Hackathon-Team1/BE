package practice.deploy.report.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import practice.deploy.report.dto.upstage.UpstageMessage;
import practice.deploy.report.dto.upstage.UpstageRequest;
// UpstageResponse 대신 AI가 생성한 최종 JSON 구조를 받기 위한 DTO를 가정합니다.
import practice.deploy.report.dto.response.FinalAnalysisJson;
import com.fasterxml.jackson.databind.ObjectMapper; // Jackson ObjectMapper 추가
import practice.deploy.report.dto.upstage.UpstageResponse;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UpstageApiService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String getMinSleepTimeJson(String finalPrompt) {
        log.info("Starting Upstage API call for min sleep time analysis.");

        UpstageRequest requestBody = new UpstageRequest(
                "solar-pro2",
                List.of(new UpstageMessage("user", finalPrompt)),
                3000
        );

        try {
            Mono<UpstageResponse> responseMono = webClient.post()
                    .uri("/chat/completions")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(UpstageResponse.class);

            UpstageResponse upstageResponse = responseMono.block();

            if (upstageResponse == null || upstageResponse.choices() == null || upstageResponse.choices().isEmpty()) {
                throw new RuntimeException("API 응답은 받았으나, choices 필드에 내용이 없습니다.");
            }

            String llmGeneratedJson = upstageResponse.choices().get(0).message().content();

            if (llmGeneratedJson == null || llmGeneratedJson.isEmpty()) {
                throw new RuntimeException("LLM 응답 내용(content)이 비어있습니다.");
            }

            String cleanJson = llmGeneratedJson.replaceAll("```json|```", "").trim();

            FinalAnalysisJson finalResult = objectMapper.readValue(cleanJson, FinalAnalysisJson.class);

            return finalResult.leastSleepTime();

        } catch (JsonProcessingException e) {
            throw new RuntimeException("AI 응답 JSON 파싱 실패 (내부 JSON 형식 오류).", e);
        } catch (WebClientResponseException e) {
            String errorMessage = String.format("Upstage API 호출 오류. Status: %d. %s", e.getStatusCode().value(), e.getStatusText());
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 예상치 못한 오류 발생: " + e.getMessage(), e);
        }
    }
}