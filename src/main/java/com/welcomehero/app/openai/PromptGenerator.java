package com.welcomehero.app.openai;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.prompt.Prompt;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Getter
@Setter
@Component
public class PromptGenerator {

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

        ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("Europe/Berlin"));
        promptBuilder.append("Die aktuelle Systemzeit ist: ");
        promptBuilder.append(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).format(zonedDateTime));
        promptBuilder.append("\n");

        promptBuilder.append("Die Sprache des Benutzers ist: ");
        promptBuilder.append(lang);
        promptBuilder.append("\n");
        promptBuilder.append("Die von dir generierte Antwort soll immer in der Sprache des Benutzers erfolgen!");
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

    @NotNull
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
