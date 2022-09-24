

/* ===
Incident Logging
All incidents, anomalies, weapons
TODO: Accommodate multiple detections 
=== */

let fr = 10; //starting FPS

function setup() {

  clearStorage();

  let canvas = createCanvas(640, 480);
  canvas.parent("sketch-container");
  canvas.id('mycanvas');

  frameRate(fr);
  video = createCapture(VIDEO);
  video.size(640, 480);
  video.hide();
  // // Models available are 'cocossd', 'yolo'
  let jobList = [
    new Job("All detections ", "assets/js/ml5/workers/incidents-worker.js", "incident-logs", "cocossd"),
    new Job("Persons Detector", "assets/js/ml5/workers/persons-worker.js", "person-logs","cocossd"),
    new Job("Weapons detector", "assets/js/ml5/workers/weapons-worker.js", "weapon-logs", "cocossd"),
    ];
  jobList.forEach((jb, i) => {
        console.log("Starting..." + jb.name);
        let modelReady = jb.modelReady.bind(jb);

        //TODO: add ability to call backend api for detection using custom models
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

