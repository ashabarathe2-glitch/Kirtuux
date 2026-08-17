# Legacy/reference version of the original Termux assistant.
# The Android app is implemented natively in Java under app/src/main/java.
import os, requests

API_KEY = os.environ.get("GEMINI_API_KEY")
MODELS = ["gemini-2.5-flash", "gemini-2.0-flash", "gemini-flash-latest"]
SYSTEM_PROMPT = """Tum Kirtuux ho — ek friendly, intelligent AI assistant.
Hindi, Marathi aur English mix karke naturally baat karo.
Warm, friendly aur clear raho."""
history=[]

def reply(text):
    history.append({"role":"user","parts":[{"text":text}]})
    for model in MODELS:
        url=f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={API_KEY}"
        r=requests.post(url,json={"system_instruction":{"parts":[{"text":SYSTEM_PROMPT}]},"contents":history},timeout=30)
        if r.status_code==200:
            out=r.json()["candidates"][0]["content"]["parts"][0]["text"]
            history.append({"role":"model","parts":[{"text":out}]})
            return out
    history.pop()
    return "Sabhi models busy hain. Thodi der baad try karo."

if __name__=="__main__":
    print("Kirtuux ready. exit likhkar band karo.")
    while True:
        x=input("You: ")
        if x.lower()=="exit": break
        print("Kirtuux:",reply(x))
