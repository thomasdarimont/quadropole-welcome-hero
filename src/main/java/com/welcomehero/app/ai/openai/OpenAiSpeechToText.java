package com.welcomehero.app.ai.openai;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.welcomehero.app.ai.SpeechToText;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.autoconfigure.openai.OpenAiChatProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiSpeechToText implements SpeechToText {

    private final OpenAiChatProperties openAiChatProperties;

    @Override
    public String transcribe(String lang, String base64DecodedAudio) {
        return transcribe(lang, decodeBase64AsByteArrayResource(base64DecodedAudio));
    }

    public String transcribe(String lang, Resource audioBlobResource) {
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + openAiChatProperties.getApiKey()) //
                .body(formData) //
                .retrieve();

        try {
            TranscribeResponse transcription = response.body(TranscribeResponse.class);
            return transcription.getText();
        } catch (Exception ex) {
            log.error("Did not understand", ex);
            return "DID_NOT_UNDERSTAND";
        }
    }

    @Data
    static class TranscribeResponse {

        String text;

        Map<String, Object> properties;

        @JsonAnySetter
        public void onUnknownProperty(String key, Object value) {
            if (properties == null) {
                properties = new HashMap<>();
            }
            properties.put(key, value);
        }
    }


    private static ByteArrayResource decodeBase64AsByteArrayResource(String blob) {
        var audioBlobBase64 = blob.substring(blob.indexOf(',') + 1);
        var decodedBytes = Base64.getDecoder().decode(audioBlobBase64);
        return new ByteArrayResource(decodedBytes) {
            @Override
            public String getFilename() {
                return "audio.webm";
            }
        };
    }

}
