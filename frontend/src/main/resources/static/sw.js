const VERSION = 'v1';
const STRATEGY = 'CACHE_FIRST';    //DEFAULT, STALE_WHILE_REVALIDATE,NETWORK_FIRST, CACHE_FIRST, CACHE_ONLY, NETWORK_ONLY

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
  event.waitUntil(
    caches.keys().then(cacheNames => Promise.all(
      cacheNames
        .filter(c => c !== cacheName)
        .map(c => caches.delete(c))
    ))
  );
  });
   
  // Stale-While-Revalidate
  // Cache first, then Network
  // Network first, then Cache
  // Cache only
  // Network only

// //1. Default. Cache first
// self.addEventListener('fetch', function(e) {
//   //console.log('Service Worker: Fetching ' + e.request.url);
//   e.respondWith(
//     caches.match(e.request)
//       .then(function(cacheResponse) {
//         return cacheResponse || fetch(e.request);  
//         })
//     );
// });


//2. Net only 
// self.addEventListener('fetch', function (event) {
//   event.respondWith(
//       fetch(event.request).then(function(networkResponse) {
//           return networkResponse
//       })
//   )
// });

//Network first
// self.addEventListener('fetch', function(e) {
//   //console.log('Service Worker: Fetching ' + e.request.url);
//   e.respondWith(
//     caches.match(e.request)
//       .then(function(cacheResponse) {
//         return fetch(e.request) || cacheResponse;  
//         })
//     );
// });

//2. GLOBAL 
self.addEventListener('fetch', function(event) {

    log("Fetching. Strategy = " + STRATEGY);

    if(STRATEGY === 'STALE_WHILE_REVALIDATE'){

        event.respondWith(async function() {
            const cache = await caches.open(cacheName);
            const cachedResponse = await cache.match(event.request);
            const fetchPromise = fetch(event.request);
            
            let networkResponse;

            event.waitUntil(async function () {
                networkResponse = await fetchPromise;
                // Update the cache with a newer version
                if (event.request.method === 'GET' && networkResponse.ok) {
                  await cache.put(event.request, networkResponse.clone());
                  }
              }());

            // The response contains cached data, if available
            return cachedResponse || networkResponse;
        }());

      }
    else if (STRATEGY === 'CACHE_FIRST'){   //CACHE_FIRST
          //Cache first, fall back to network
          // Open the cache
          event.respondWith(caches.open(cacheName).then(async (cache) => {
            // Respond with the image from the cache or from the network
            const cachedResponse = await cache.match(event.request);
            return cachedResponse || fetch(event.request).then((fetchedResponse) => {
              log("making a network call for " + event.request.url);
              // Add the network response to the cache for future visits.
              // Note: we need to make a copy of the response to save it in
              // the cache and use the original as the request response.
              if (event.request.method === 'GET' && fetchedResponse.ok) {
                log("Caching GET request and response");
                cache.put(event.request, fetchedResponse.clone());
                }

              // Return the network response
              return fetchedResponse;
            });
          }));

      }
    else{   //DEFAULT... works
        event.respondWith(
          caches.match(event.request)
            .then(function(cachedResponse) {
              return cachedResponse || fetch(event.request);  
              })
          );
    }

  
});


//2. Stale-While-Revalidate. test 1
// self.addEventListener('fetch', function (event) {
//   event.respondWith(
//       caches.open(cacheName)
//           .then(function(cache) {
//               cache.match(event.request)
//                   .then( function(cacheResponse) {
//                       fetch(event.request)
//                           .then(function(networkResponse) {
//                               cache.put(event.request, networkResponse.clone())
//                           })
//                       return cacheResponse || networkResponse
//                   })
//           })
//     )
// });



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
//              .then(response => response)
//       )
//   );
// });



// each logging line will be prepended with the service worker version
function log(message) {
  console.log(VERSION, message);
}