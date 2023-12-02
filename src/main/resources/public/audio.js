class AudioRecorder {
    constructor() {
        this.mediaRecorder = null;
        this.onRecordingAvailable = null;
        this.audioChunks = [];
    }

    startRecording() {
        console.log("recording: start");

        $("#answer").innerText = "";
        $("#answer_retention_indicator").style.display = "none";

        navigator.mediaDevices.getUserMedia({ audio: true })
            .then(stream => {

                self.currentRecordingBlob = null;

                this.mediaRecorder = new MediaRecorder(stream, { mimeType: 'audio/webm' });
                this.mediaRecorder.start();

                this.audioChunks = [];
                this.mediaRecorder.addEventListener("dataavailable", event => {
                    this.audioChunks.push(event.data);
                });
            })
            .catch(err => console.error("Error in startRecording:", err));
    }

    stopRecording() {
        console.log("recording: stop");
        this.mediaRecorder.stop();
        this.mediaRecorder.addEventListener("stop", () => {
            const audioBlob = new Blob(this.audioChunks);
            const audioUrl = URL.createObjectURL(audioBlob);
            const audio = document.getElementById("audioPlayback");
            audio.src = audioUrl;
            // Do something with the audio blob

            this.onRecordingAvailable(audioBlob);
        });
    }
}