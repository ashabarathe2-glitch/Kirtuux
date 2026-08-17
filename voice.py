# Reference voice module for future Termux/Python experiments.
# Android production voice input is implemented by VoiceEngine.java.
def wake_phrase_detected(text: str) -> bool:
    t=text.lower()
    return "kirtuux" in t or "hey kirtuux" in t
