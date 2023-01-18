const VERSION = 'v1.1';
const STRATEGY = 'STALE_WHILE_REVALIDATE';    //DEFAULT, STALE_WHILE_REVALIDATE,NETWORK_FIRST, CACHE_FIRST, CACHE_ONLY, NETWORK_ONLY

var cacheName = 'classif-ai-pwa';
var filesToCache = [
  // "/",
  "/classif-ai/",

  "/classif-ai/settings/",
  "/classif-ai/webcam/",

  "/classif-ai/assets/manifest.json",

  "/classif-ai/favicon.ico",
  "/classif-ai/assets/images/person_holding_monitor.png",
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
    log("Installing")
    e.waitUntil(
      caches.open(cacheName).then(function(cache) {
        return cache.addAll(filesToCache);
      })
    );
  });


self.addEventListener('activate', event => {
  log("Activating");

  log("Fetching. Strategy = " + STRATEGY);

  event.waitUntil(
    caches.keys().then(cacheNames => Promise.all(
      cacheNames
        .filter(c => c !== cacheName)
        .map(c => caches.delete(c))
    ))
  );
  });
   
  
self.addEventListener('fetch', function(event) {

    if(STRATEGY === 'STALE_WHILE_REVALIDATE'){
          //Cache first (may be stale), then (always) update cache via network call

        event.respondWith(caches.open(cacheName).then((cache) => {
          return cache.match(event.request).then((cachedResponse) => {
            const fetchedResponse = fetch(event.request).then((networkResponse) => {
              //log("Fetching " + event.request.url);
              if (event.request.method === 'GET' && networkResponse.ok) {
                //log("Updating cache ...");
                cache.put(event.request, networkResponse.clone());
                }
              return networkResponse;
            });
    
            return cachedResponse || fetchedResponse;
          });
        }));

      }
    else if (STRATEGY === 'CACHE_FIRST'){   //CACHE_FIRST
          // Cache first, fall back to network in case of a miss
          // Open the cache
          event.respondWith(caches.open(cacheName).then(async (cache) => {
            // Respond with the image from the cache or from the network
            const cachedResponse = await cache.match(event.request);
            return cachedResponse || fetch(event.request).then((fetchedResponse) => {
              //log("Fetching " + event.request.url);
              // Add the network response to the cache for future visits.
              // Note: we need to make a copy of the response to save it in
              // the cache and use the original as the request response.
              if (event.request.method === 'GET' && fetchedResponse.ok) {
                //log("Updating cache ...");
                cache.put(event.request, fetchedResponse.clone());
                }

              // Return the network response
              return fetchedResponse;
            });
          }));

      }
    else{   //DEFAULT... use pre-cached resources, otherwise fetch from network
        event.respondWith(
          caches.match(event.request)
            .then(function(cachedResponse) {
              return cachedResponse || fetch(event.request);  
              })
          );
    }

  
});


// each logging line will be prepended with the service worker version
function log(message) {
  console.log(VERSION, message);
}