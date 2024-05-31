package com.welcomehero.app.ai.openai;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
@Setter
@Component
public class PromptAugmentation {

    private String promptPrefix;

    private String promptSuffix;

    private String additionalInstruction;

    @PostConstruct
    public void init() {
        promptPrefix = readFile("prompts/assistant.prefix.prompt");
        promptSuffix = readFile("prompts/assistant.postfix.prompt");
    }

    public Prompt generate(String lang, String input) {

        var promptBuilder = new StringBuilder();

        promptBuilder.append(promptPrefix);
        promptBuilder.append("\n");


        String contextHints = getContextHints(lang);
        promptBuilder.append(contextHints);
        promptBuilder.append("\n");

        promptBuilder.append(input);
        promptBuilder.append("\n");
        if (additionalInstruction != null) {
            promptBuilder.append(additionalInstruction);
            promptBuilder.append("\n");
        }
        promptBuilder.append(promptSuffix);

        return new Prompt(promptBuilder.toString());
    }

    public String getContextHints(String lang) {

        StringBuilder promptBuilder = new StringBuilder();

        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Berlin"));
        promptBuilder.append("The current system time is: ");
        promptBuilder.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).format(zonedDateTime));
        promptBuilder.append("\n");

        promptBuilder.append("Today is: ");
        promptBuilder.append(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.GERMAN));
        promptBuilder.append("\n");

        promptBuilder.append("The language of the user is: ");
        promptBuilder.append(lang);
        promptBuilder.append("\n");
        promptBuilder.append("The response you generate should always be in the user's language!");
        promptBuilder.append("\n");

        return promptBuilder.toString();
    }

    private static String readFile(String path) {
        try {
            File resource = new ClassPathResource(path).getFile();
            String content = new String(Files.readAllBytes(resource.toPath()));
            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
