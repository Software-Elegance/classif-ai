
var cacheName = 'classif-ai-pwa';
var filesToCache = [
  // '/',
  "/classif-ai/",
  // '/classif-ai/templates/index.ftlh',
  "/classif-ai/assets/media/svg/brand-logos/tower.svg",
  "/classif-ai/assets/css/style.bundle.css",

  // '/settings.ftlh',
  // "/settings/",
  "/classif-ai/settings/",
  // '/classif-ai/templates/settings.ftlh',
  "/classif-ai/assets/js/ml5/utils.js",
  "/classif-ai/assets/js/ml5/events.js",

  // '/index.html',
  // '/simple/index.html',
  // '/simple/css/style.css',
  '/classif-ai/favicon.ico',
  '/classif-ai/assets/js/main.js'
];

/* Start the service worker and cache all of the app's content */
self.addEventListener('install', function(e) {
  e.waitUntil(
    caches.open(cacheName).then(function(cache) {
      return cache.addAll(filesToCache);
    })
  );
});

/* Serve cached content when offline */
self.addEventListener('fetch', function(e) {
  e.respondWith(
    caches.match(e.request).then(function(response) {
      return response || fetch(e.request);
    })
  );
});