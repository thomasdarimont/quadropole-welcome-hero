package com.welcomehero.app.api;

import com.welcomehero.app.openai.OpenAiFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final OpenAiFacade openAi;

    @PutMapping("instructions/additional")
    public ResponseEntity<?> updateInstructions(@RequestBody String input) {

        log.info("> updateInstructions - instruction: <{}>", input);

        openAi.updateInstructions(input);

        log.info("< updateInstructions");
        return ResponseEntity.ok().build();
    }

    @PostMapping("/speech-to-text")
    public ResponseEntity<SpeechToTextOutput> speechToText(@RequestBody SpeechToTextInput input) {

        log.info("> speechToText - language: <{}>", input.language());

        var recognizedText = openAi.speechToText(input.language(), input.audioBlob());
        var speechToTextOutput = SpeechToTextOutput.builder().language(input.language()).text(recognizedText).build();

        log.info("< speechToText - language: <{}> text: <{}>", input.language(), speechToTextOutput.text());

        return ResponseEntity.ok(speechToTextOutput);
    }

    @PostMapping("/generate")
    public ResponseEntity<AssistantOutput> generate(@RequestBody AssistantInput input) {

        log.info("> generate - language: <{}> input: <{}>", input.language(), input.input());

        var generatedText = openAi.generateText(input.language(), input.input());
        var assistantOutput = new AssistantOutput(input.language(), generatedText);

        log.info("< generate - language: <{}> input: <{}>", input.language(), generatedText);

        return ResponseEntity.ok(assistantOutput);
    }
}
