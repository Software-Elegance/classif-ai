
var cacheName = 'classif-ai-pwa';
var filesToCache = [
  // "/",
  "/classif-ai/",

  "/classif-ai/settings/",
  "/classif-ai/webcam/",

  "/classif-ai/assets/manifest.json",

  "/classif-ai/favicon.ico",
  "/classif-ai/assets/images/icons/hello-icon-128.png",
  "/classif-ai/assets/images/icons/hello-icon-144.png",
  "/classif-ai/assets/images/icons/hello-icon-152.png",
  "/classif-ai/assets/images/icons/hello-icon-192.png",
  "/classif-ai/assets/images/icons/hello-icon-256.png",
  "/classif-ai/assets/images/icons/hello-icon-512.png",
  "/classif-ai/assets/media/svg/brand-logos/tower.svg",

  "/classif-ai/assets/css/style.bundle.css",

  "/classif-ai/assets/js/ml5/sketch.js",
  "/classif-ai/assets/js/ml5/jobs.js",

  "assets/js/ml5/workers/anomally-worker.js",
  "assets/js/ml5/workers/persons-worker.js",
  "assets/js/ml5/workers/incidents-worker.js",

  "/classif-ai/assets/js/ml5/p5.min.js",
  "/classif-ai/assets/js/ml5/ml5.min.js",
  "/classif-ai/assets/js/ml5/valueobjects.js",

  "/classif-ai/assets/js/ml5/utils.js",
  "/classif-ai/assets/js/ml5/events.js",
  "/classif-ai/assets/js/main.js"
];

/* Start the service worker and cache all of the app's content */
self.addEventListener('install', function(e) {
    e.waitUntil(
      caches.open(cacheName).then(function(cache) {
        return cache.addAll(filesToCache);
      })
    );
  });

// /* Serve cached content when offline */
self.addEventListener('fetch', function(e) {
  //console.log('Service Worker: Fetching ' + e.request.url);
  e.respondWith(
    caches.match(e.request).then(function(response) {

      //we respond with the cached content whenever possible. As a fallback, we make a network request.
      return response || fetch(e.request);
    })
  );
});

//This one makes it blazing fast but not offline first
//https://www.geeksforgeeks.org/service-workers-in-javascript/
// self.addEventListener('fetch", e => {
//   console.log('Service Worker: Fetching');
//   e.respondWith(
//       fetch(e.request)
//       .then(response => {
//           // The response is a stream and in order the browser 
//           // to consume the response and in the same time the 
//           // cache consuming the response it needs to be 
//           // cloned in order to have two streams.
//           const resClone = response.clone();
//           // Open cache
//           caches.open(cacheName)
//               .then(cache => {
//                   // Add response to cache
//                   cache.put(e.request, resClone);
//               });
//           return response;
//       }).catch(
//           err => caches.match(e.request)
//           .then(response => response)
//       )
//   );
// });
var cacheName = 'classif-ai-pwa';
var filesToCache = [
  // "/",
  "/classif-ai/",

  "/classif-ai/settings/",
  "/classif-ai/webcam/",

  "/classif-ai/assets/manifest.json",

  "/classif-ai/favicon.ico",
  "/classif-ai/assets/images/icons/hello-icon-128.png",
  "/classif-ai/assets/images/icons/hello-icon-144.png",
  "/classif-ai/assets/images/icons/hello-icon-152.png",
  "/classif-ai/assets/images/icons/hello-icon-192.png",
  "/classif-ai/assets/images/icons/hello-icon-256.png",
  "/classif-ai/assets/images/icons/hello-icon-512.png",
  "/classif-ai/assets/media/svg/brand-logos/tower.svg",

  "/classif-ai/assets/css/style.bundle.css",

  "/classif-ai/assets/js/ml5/sketch.js",
  "/classif-ai/assets/js/ml5/jobs.js",


  "assets/js/ml5/workers/anomally-worker.js",
  "assets/js/ml5/workers/persons-worker.js",
  "assets/js/ml5/workers/incidents-worker.js",



  "/classif-ai/assets/js/ml5/p5.min.js",
  "/classif-ai/assets/js/ml5/ml5.min.js",
  "/classif-ai/assets/js/ml5/valueobjects.js",

  "/classif-ai/assets/js/ml5/utils.js",
  "/classif-ai/assets/js/ml5/events.js",
  "/classif-ai/assets/js/main.js"
];

/* Start the service worker and cache all of the app's content */
self.addEventListener('install', function(e) {
    e.waitUntil(
      caches.open(cacheName).then(function(cache) {
        return cache.addAll(filesToCache);
      })
    );
  });

// /* Serve cached content when offline */
self.addEventListener('fetch', function(e) {
  //console.log('Service Worker: Fetching ' + e.request.url);
  e.respondWith(
    caches.match(e.request).then(function(response) {

      //we respond with the cached content whenever possible. As a fallback, we make a network request.
      return response || fetch(e.request);
    })
  );
});

//This one makes it blazing fast but not offline first
//https://www.geeksforgeeks.org/service-workers-in-javascript/
// self.addEventListener('fetch", e => {
//   console.log('Service Worker: Fetching');
//   e.respondWith(
//       fetch(e.request)
//       .then(response => {
//           // The response is a stream and in order the browser 
//           // to consume the response and in the same time the 
//           // cache consuming the response it needs to be 
//           // cloned in order to have two streams.
//           const resClone = response.clone();
//           // Open cache
//           caches.open(cacheName)
//               .then(cache => {
//                   // Add response to cache
//                   cache.put(e.request, resClone);
//               });
//           return response;
//       }).catch(
//           err => caches.match(e.request)
//           .then(response => response)
//       )
//   );
// });