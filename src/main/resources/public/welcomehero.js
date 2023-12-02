class WelcomeHero {
    constructor() {
        this.currentLanguage = "de";
        this.translations = { //
            "de": {
                "conversation_caption": "Was möchten Sie wissen?",
                "press_button_caption": "Drücken Sie auf das Mikrofon und fragen Sie mich.",
                "thinking_caption": "... ich denke nach ..."
            },  //
            "en": {
                "conversation_caption": "What would you like to know?",
                "press_button_caption": "Press the microphone and ask me.",
                "thinking_caption": "... I think ..."
            },  //
            "fr": {
                "conversation_caption": "Que voulez-vous savoir ?",
                "press_button_caption": "Appuyez sur le micro et demandez-moi.",
                "thinking_caption": "... je pense ..."
            }, //
            "es": {
                "conversation_caption": "¿Qué le gustaría saber?",
                "press_button_caption": "Pulsa el micrófono y pregúntame.",
                "thinking_caption": "... Creo que..."
            }
        }
    }

    startNewConversation(lang) {
        this.updateLanguage(lang);
        $("#answer").innerText = "";
        showScreen("conversation");
        $("#currentDate").innerText = new Intl.DateTimeFormat(lang, {
            dateStyle: 'full'
        }).format(new Date());
    }

    updateLanguage(lang) {
        this.currentLanguage = lang;
        this.updateTranslations()
    }

    translate(key) {
        let lang = this.currentLanguage;
        let translatedTexts = this.translations[lang];
        return translatedTexts[key] || '!' + key + "!";
    }

    updateTranslations() {
        let lang = this.currentLanguage;
        let translatedTexts = this.translations[lang];

        for (let key in translatedTexts) {
            let element = $("#" + key);
            if (element) {
                element.innerText = translatedTexts[key];
            }
        }
    }
}