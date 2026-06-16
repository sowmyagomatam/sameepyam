/* Shared fetch() helpers and small UI utilities for Sameepyam.
 * Every page calls exactly one API endpoint via these helpers. No framework, no build step. */

const Sameepyam = {
  /** POST JSON, return parsed JSON. Throws on non-2xx so callers can show a warm error. */
  async postJson(url, body) {
    const res = await fetch(url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    });
    if (!res.ok) throw await Sameepyam._httpError(res);
    return res.json();
  },

  /** POST multipart form data (file uploads), return parsed JSON. */
  async postForm(url, formData) {
    const res = await fetch(url, { method: "POST", body: formData });
    if (!res.ok) throw await Sameepyam._httpError(res);
    return res.json();
  },

  /** Build an Error that carries the HTTP status and any server-provided warm `message`
   *  (e.g. the rate-limit notice), so callers can react to 429 vs a generic failure. */
  async _httpError(res) {
    let payload = null;
    try { payload = await res.json(); } catch { /* body wasn't JSON */ }
    const err = new Error(payload?.message || `Request failed (${res.status})`);
    err.status = res.status;
    err.userMessage = payload?.message || null;
    return err;
  },

  show(el) { el && el.classList.remove("hidden"); },
  hide(el) { el && el.classList.add("hidden"); },

  /** Render a warm, reassuring error with a clear next step into the given container. */
  renderError(container, nextStep = "Please check your internet and try once more.") {
    container.innerHTML =
      `<div class="error">` +
      `<div>Sorry, something didn't work just now. Nothing is wrong on your side.</div>` +
      `<div class="next-step">${nextStep}</div>` +
      `</div>`;
    Sameepyam.show(container);
  },

  /** Render a calm informational notice (not an error) — e.g. the rate-limit message.
   *  Reuses the warm .error box styling but without the "something went wrong" framing. */
  renderNotice(container, message, nextStep) {
    container.innerHTML =
      `<div class="error">` +
      `<div>${Sameepyam.escapeHtml(message)}</div>` +
      (nextStep ? `<div class="next-step">${Sameepyam.escapeHtml(nextStep)}</div>` : "") +
      `</div>`;
    Sameepyam.show(container);
  },

  escapeHtml(s) {
    return String(s ?? "")
      .replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;").replaceAll("'", "&#39;");
  },
};

/* ---- Language preference (stored in localStorage, no backend) ---- */
const SameepyamLang = {
  key: "sameepyam.language",
  get() { return localStorage.getItem(this.key) || "English"; },
  set(v) { localStorage.setItem(this.key, v); },
};

/* ---- Voice input: records audio, posts to /api/transcribe, fills a textarea ----
 * Wire a mic button with: SameepyamVoice.attach(micButton, targetTextarea, statusEl). */
const SameepyamVoice = {
  attach(button, target, statusEl) {
    if (!button || !navigator.mediaDevices) {
      button && Sameepyam.hide(button);
      return;
    }
    let recorder = null;
    let chunks = [];

    button.addEventListener("click", async () => {
      if (recorder && recorder.state === "recording") {
        recorder.stop();
        return;
      }
      try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        recorder = new MediaRecorder(stream);
        chunks = [];
        recorder.ondataavailable = (e) => chunks.push(e.data);
        recorder.onstop = async () => {
          stream.getTracks().forEach((t) => t.stop());
          button.classList.remove("recording");
          button.textContent = "🎤";
          const blob = new Blob(chunks, { type: "audio/webm" });
          const fd = new FormData();
          fd.append("file", blob, "voice.webm");
          if (statusEl) statusEl.textContent = "Listening to your recording…";
          try {
            const out = await Sameepyam.postForm("/api/transcribe", fd);
            target.value = (target.value ? target.value + " " : "") + (out.text || "");
            if (statusEl) statusEl.textContent = "";
          } catch {
            if (statusEl) statusEl.textContent = "Could not hear that — please try typing instead.";
          }
        };
        recorder.start();
        button.classList.add("recording");
        button.textContent = "⏹";
        if (statusEl) statusEl.textContent = "Recording… tap the button again to stop.";
      } catch {
        if (statusEl) statusEl.textContent = "Microphone is not available — please type instead.";
      }
    });
  },
};

/* Register the service worker so the app is installable. */
if ("serviceWorker" in navigator) {
  window.addEventListener("load", () => navigator.serviceWorker.register("/sw.js").catch(() => {}));
}