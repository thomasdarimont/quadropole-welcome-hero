package com.welcomehero.app.api;

import lombok.Builder;

@Builder
public record SpeechToTextOutput(String language, String text, String error) {
}
