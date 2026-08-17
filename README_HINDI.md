# Kirtuux — Personal AI Voice Assistant

यह पूरा Android project है। इसमें Gemini chat, voice input, text-to-speech, conversation memory, model fallback, background voice service और "Hey Kirtuux" style wake phrase handling का आधार शामिल है।

## जरूरी बात
आपके Termux में Android SDK के `adb/aapt` x86_64 binaries ARM64 फोन पर नहीं चलेंगे। इसलिए APK build के लिए इस project में GitHub Actions workflow दिया गया है। Termux से code तैयार/push करें और GitHub Actions से APK बनाएं।

## Gemini API key
1. Google AI Studio से API key बनाएं।
2. GitHub repository में Settings → Secrets and variables → Actions → New repository secret.
3. Name: `GEMINI_API_KEY`
4. Value: अपनी key.
5. Workflow चलाएं।

App में API key build time पर `BuildConfig.GEMINI_API_KEY` में जाती है। Public APK में API key पूरी तरह secret नहीं मानी जा सकती; production के लिए अपना backend proxy बेहतर है।

## Local build
`local.properties` बनाएं और `GEMINI_API_KEY=...` डालें। Android Studio/compatible Gradle environment से build करें।

## Termux
Termux में project files edit और git push कर सकते हैं। Official SDK के Linux binaries ARM64 Termux पर architecture mismatch दे सकते हैं, इसलिए `./gradlew assembleDebug` सीधे फोन पर सफल होना guaranteed नहीं है।

## Features
- Gemini chat
- Hindi/Marathi/English mixed assistant personality
- Model fallback: gemini-2.5-flash → gemini-2.0-flash → gemini-flash-latest
- Network error/429/503 fallback
- Conversation history
- Voice input
- Text-to-speech
- Background foreground service
- Wake phrase detection attempt ("hey kirtuux", "kirtuux")
- Start/stop listening controls
- Clean Material-style UI
- GitHub Actions APK build
