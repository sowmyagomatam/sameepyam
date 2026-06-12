/* Service worker stub — enough to make the app installable ("add to home screen").
 * Caches the static shell so the home screen loads offline. API calls always go to the network. */

const CACHE = "sameepyam-shell-v1";
const SHELL = [
  "/", "/index.html", "/scam.html", "/explain.html", "/compose.html",
  "/style.css", "/app.js", "/manifest.json",
];

self.addEventListener("install", (event) => {
  event.waitUntil(caches.open(CACHE).then((c) => c.addAll(SHELL)).then(() => self.skipWaiting()));
});

self.addEventListener("activate", (event) => {
  event.waitUntil(
    caches.keys().then((keys) =>
      Promise.all(keys.filter((k) => k !== CACHE).map((k) => caches.delete(k)))
    ).then(() => self.clients.claim())
  );
});

self.addEventListener("fetch", (event) => {
  const url = new URL(event.request.url);
  // Never cache API responses — always hit the network for fresh AI results.
  if (url.pathname.startsWith("/api/")) return;
  if (event.request.method !== "GET") return;
  event.respondWith(
    caches.match(event.request).then((cached) => cached || fetch(event.request))
  );
});