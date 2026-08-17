# Kirtuux architecture

- MainActivity: UI, chat and buttons.
- AssistantBrain: Gemini API, conversation history and model fallback.
- VoiceEngine: Android SpeechRecognizer + TextToSpeech.
- VoiceService: foreground microphone service and wake phrase attempt.
- main.py / voice.py: original Termux reference implementation.
- GitHub Actions: remote APK build because Android SDK command-line binaries downloaded for Linux are x86_64 while many Android phones running Termux are ARM64.
