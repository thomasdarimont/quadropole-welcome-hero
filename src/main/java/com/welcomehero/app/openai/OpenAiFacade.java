package com.welcomehero.app.openai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.client.AiClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiFacade {

    private static final String OPENID_TOKEN = System.getenv("SPRING_AI_OPENAI_API_KEY");

    private final AiClient aiClient;

    private final PromptGenerator promptGenerator;

    public String generateText(String lang, String input) {

        var prompt = promptGenerator.generate(lang, input);
        var response = aiClient.generate(prompt);

        return response.getGeneration().getText();
    }

    public String speechToText(String lang, String blob) {

        var audioBlobResource = decoreBase64AsByteArrayResource(blob);

        var restClient = RestClient.create();

        // see https://platform.openai.com/docs/api-reference/audio/createTranscription
        var formData = new LinkedMultiValueMap<String, Object>();
        formData.add("model", "whisper-1");
        formData.add("language", lang);
        formData.add("file", audioBlobResource);
        formData.add("temperature", "0");
        formData.add("response_format", "json");

        var response = restClient.post() //
                .uri("https://api.openai.com/v1/audio/transcriptions") //
                .header("Authorization", "Bearer " + OPENID_TOKEN) //
                .body(formData) //
                .retrieve();

        try {
            Map body = response.body(Map.class);
            return String.valueOf(body.get("text"));
        } catch (Exception ex) {
            log.error("Did not understand", ex);
            return "DID_NOT_UNDERSTAND";
        }
    }

    @NotNull
    private static ByteArrayResource decoreBase64AsByteArrayResource(String blob) {
        var audioBlobBase64 = blob.substring(blob.indexOf(',') + 1);
        var decodedBytes = Base64.getDecoder().decode(audioBlobBase64);
        return new ByteArrayResource(decodedBytes) {
            @Override
            public String getFilename() {
                return "audio.webm";
            }
        };
    }

    public void updateInstructions(String instruction) {
        promptGenerator.setAdditionalInstruction(instruction);
    }
}
