function $(selector) {
    return document.querySelector(selector);
}

function $all(selector) {
    return [...document.querySelectorAll(selector)];
}

function showScreen(screenId) {

    $all(".screen").forEach(screen => {
        screen.style.display = "none";
    });

    $("#" + screenId).style.display = "block";
}


function blobToBase64(blob, callback) {
    const reader = new FileReader();
    reader.readAsDataURL(blob);
    reader.onloadend = function () {
        const base64data = reader.result;
        callback(base64data);
    }
}

function sendRequest(url, requestOptions) {

    let requestData = {
        timeout: 10000, method: "GET", headers: {
            "Accept": "application/json", 'Content-Type': 'application/json'
        }, ...requestOptions
    }

    return fetch(url, requestData);
}

window.onload = function () {

    $all('.language').forEach(langElement => {
        langElement.addEventListener('click', (event) => {
            let language = event.target.dataset.lang;
            console.log('Selected Language:', language);
            $("#selectedLang").innerText = event.target.innerText;
            welcomeHero.startNewConversation(language);
        });
    });

    showScreen("welcome");

    let audioRecorder = new AudioRecorder();
    audioRecorder.onRecordingAvailable =  (audioBlob) => {
        blobToBase64(audioBlob, async (base64String) => {
            console.log("Base64 String:", base64String);

            // post data to server
            let payload = {
                "language": welcomeHero.currentLanguage, //
                "audioBlob": base64String //
            };

            await sendRequest("/api/speech-to-text", {
                method: 'POST', //
                body: JSON.stringify(payload) //
            }).then(response => response.json()) //
                .then(async speechToTextResult => {
                    console.log("Transcription:", speechToTextResult);

                    $("#answer").innerText = welcomeHero.translate("thinking_caption");

                    let assistantInput = {
                        input: speechToTextResult.text, //
                        language: speechToTextResult.language,
                    };

                    // $("#stt").innerText = speechToTextResult.text;

                    let assistantResponse = await sendRequest("/api/generate", //
                        {
                            method: 'POST', //
                            body: JSON.stringify(assistantInput) //
                        }).then(response => response.json()); //

                    console.log(assistantResponse);

                    let assistantText = assistantResponse.text;

                    $("#answer").innerText = assistantText;
                    $("#answer_retention_indicator").style.display = "block";

                    window.setTimeout(() => {
                        // clear answer automatically
                        $("#answer").innerText = "";
                    }, 10* 1000);


                }) //
                .catch(error => {
                    console.error("Error sending audio:", error);
                });
        });
    };

    window.audioRecorder = audioRecorder;

    window.welcomeHero = new Welcomehero();
};
