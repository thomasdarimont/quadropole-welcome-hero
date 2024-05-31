package com.welcomehero.app.ai.openai;

import com.welcomehero.app.ai.SpeechToText;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OpenAiFacade {

    private final ChatClient chatClient;

    private final SpeechToText speechToText;

    private final PromptAugmentation promptAugmentation;

    public OpenAiFacade(ChatClient.Builder chatClientBuilder, SpeechToText speechToText, PromptAugmentation promptAugmentation) {
        this.chatClient = chatClientBuilder.build();
        this.speechToText = speechToText;
        this.promptAugmentation = promptAugmentation;
    }

    public String generateText(String lang, String input) {

        var prompt = promptAugmentation.generate(lang, input);
        var response = chatClient.prompt(prompt);

        return response.call().content();
    }

    public String speechToText(String lang, String base64DecodedAudio) {
        return speechToText.transcribe(lang, base64DecodedAudio);
    }

    // fake dynamic model update
    public void updateInstructions(String instruction) {
        promptAugmentation.setAdditionalInstruction(instruction);
    }
}
