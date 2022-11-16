

/* ===
Incident Logging
All incidents, anomalies, weapons
TODO: Accommodate multiple detections 
=== */

let fr = 10; //starting FPS
let jobList = [];


let webcam = false;

let playing = false;
let myVideo;
let myButton;

function setup() {

  let canvas = createCanvas(640, 480);  //4:3
  canvas.parent("sketch-container");
  canvas.id('mycanvas');

  frameRate(fr);
  if(webcam) {
    video = createCapture(VIDEO); //webcam
    }
  else{
    video = createVideo("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",vidLoad); //remote mp4
    //Start playing remote video
    myButton = createButton('play');
    myButton.mousePressed(togglePlay);
    }
  video.size(640, 480);
  video.hide();

  // // Models available are 'cocossd', 'yolo'
  jobList = [
    new Job("All detections", "assets/js/ml5/workers/incidents-worker.js", "incident-logs", "cocossd"),
    new Job("Persons Detector", "assets/js/ml5/workers/persons-worker.js", "person-logs","cocossd"),
    // new Job("Weapons detector", "assets/js/ml5/workers/weapons-worker.js", "weapon-logs", "cocossd"),
    new Job("Anomally detector", "assets/js/ml5/workers/anomally-worker.js", "anomally-logs", "cocossd"),
    ];
  jobList.forEach((jb, i) => {
        console.log("Starting..." + jb.name);
        let modelReady = jb.modelReady.bind(jb);
        let poseNetLoaded = jb.poseNetLoaded.bind(jb);

        //TODO: add ability to call backend api for detection using custom models
        let det = ml5.objectDetector(jb.model, modelReady);

        jb.start(det);
      }
  );

  

}




function stopStartAll(){

  if(detections.length == 0){//already stopped
    console.log("Calling setup")
    setup();
    }
  else{
    console.log("stopping detection engines...");
    jobList.forEach((jb, i) => {
      jb.stop();
      });
    detections.splice(0,detections.length);
    select("#modelStatus").html("Stopped !");
    }

  return false;   //do not follow href
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


  //toggle playing of remote video
  function togglePlay() {
    if (playing) {
      video.pause()
      playing = false;
    }
    else {
      video.play();
      video.volume(0);
      playing = true;
    }
  }

//on remote video load
function vidLoad() {
  console.log("video loaded");
  }