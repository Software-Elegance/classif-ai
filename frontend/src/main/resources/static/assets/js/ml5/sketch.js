

/* ===
Incident Logging
All incidents, anomalies, weapons
TODO: Accommodate multiple detections 
=== */

let fr = 10; //starting FPS
let jobList = [];


let webcam = true;

let playing = false;
let myVideo;
let myButton;

function setup() {

  let canvas = createCanvas(640, 480);  //4:3
  canvas.parent("sketch-container");
  canvas.id('mycanvas');

  //1. FRAMERATE
  if(localStorage.getItem("frames_per_second")){
    fr = localStorage.getItem("frames_per_second");
    }
  else{
    fr = DEFAULT_FRAMES_PER_SECOND;
    }

  frameRate(fr);

  //2. VIDEO INPUT
  if("webcam" === localStorage.getItem("video_source")){
      webcam = true;
      video = createCapture(VIDEO); //webcam
      }
  else if("cctv" === localStorage.getItem("video_source")){
      webcam = false;
      //set the feed url
      video = createVideo("http://localhost/api/rtsp/live.mp4",vidLoad); //remote mp4
      }
  else if("video" === localStorage.getItem("video_source")){
      webcam = false;

      let videoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
      if(localStorage.getItem("video_url")){
        videoUrl = localStorage.getItem("video_url");
        }
      
      video = createVideo(videoUrl,vidLoad); //remote mp4
        
      myButton = createButton('play');
      myButton.mousePressed(togglePlay);
      }
  else{
      webcam = true;
      video = createCapture(VIDEO); //webcam
      localStorage.setItem("video_source",DEFAULT_VIDEO);
      }

  video.size(640, 480);
  video.hide();

  //3. SENSITIVITY
  let settings = new Map();//movement config as map of label and delta

  if (localStorage.getItem("sensitivity_time") && localStorage.getItem("sensitivity_movement") && localStorage.getItem("frames_per_second")) {
    deltaSensitivity = localStorage.getItem("sensitivity_movement");
    timeSensitivityMilliSeconds = localStorage.getItem("sensitivity_time");
    framesPerSecond = localStorage.getItem("frames_per_second");
    }
  else{
    deltaSensitivity = DEFAULT_SENSITIVITY_MOVEMENT;
    timeSensitivityMilliSeconds = DEFAULT_SENSITIVITY_TIME;
    framesPerSecond = DEFAULT_FRAMES_PER_SECOND;
    }

  settings.set("sensitivity_time",timeSensitivityMilliSeconds);
  settings.set("sensitivity_movement",deltaSensitivity);
  settings.set("frames_per_second",framesPerSecond);    //redundant

  console.log("settings " + settings);

  //WORKERS aka DETECTORS
  
  jobList = [
    // // Models available are 'cocossd', 'yolo'
    new Job("All detections", "assets/js/ml5/workers/incidents-worker.js", "incident-logs", "cocossd", settings),
    new Job("Persons Detector", "assets/js/ml5/workers/persons-worker.js", "person-logs","cocossd",settings),
    new Job("Anomally detector", "assets/js/ml5/workers/anomally-worker.js", "anomally-logs", "cocossd",settings),
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



