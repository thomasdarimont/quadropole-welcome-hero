package com.welcomehero.app.ai;

import org.springframework.core.io.Resource;

public interface SpeechToText {

    String transcribe(String lang, Resource audioBlobResource);

    String transcribe(String lang, String base64DecodedAudio);
}
