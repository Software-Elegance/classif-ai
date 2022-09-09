

/* ===
Incident Logging
All incidents, anomalies, weapons
TODO: Accommodate multiple detections 
=== */


let fr = 5; //starting FPS

function setup() {
  let canvas = createCanvas(640, 480);
  canvas.parent("sketch-container");
  frameRate(fr); 

  video = createCapture(VIDEO);
  video.size(640, 480);
  video.hide();

  // // Models available are 'cocossd', 'yolo'
  var jobList = [
    new Job("Anomaly Detector", "assets/js/ml5/workers/anomally-worker.js", "abnormalEvents","cocossd"),
    new Job("Incidents", "assets/js/ml5/workers//incidents-worker.js", "allIncidents", "cocossd"),
    ];

  jobList.forEach((jb,i) => {      
      console.log("Starting..." + jb.name);
      let modelReady = jb.modelReady.bind(jb);
      let det = ml5.objectDetector(jb.model, modelReady);
      jb.start(det);
      }
    );  

  }


function draw() {
  image(video, 0, 0);
  
  for (let i = 0; i < detections.length; i++) {
    let object = detections[i];
    stroke(0, 255, 0);
    strokeWeight(4);
    noFill();
    rect(object.x, object.y, object.width, object.height);
    noStroke();
    fill(255);
    textSize(24);
    text(object.label, object.x + 10, object.y + 24);
  }
}

