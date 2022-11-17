class Message {

    constructor(id, title, message, payload, settings) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        this.settings = settings;
        }
    
    }


let incidents = "<div>All Person detections</div>";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];
let prevTopLeft = 0;
//let prevTopLeftMap = new Map()

// let deltaSensitivity = 8000;        //sensitivity to movement (change in topleft)
// let timeSensitivityMilliSeconds = 0 * 1000;        //seconds 


let prevTopLeftMap = new Map();//movement config as map of label and delta
let prevTimeMap = new Map();

prevTimeMap.set("person",new Date().getTime());
prevTopLeftMap.set("person",0);

onmessage = (event) => {

    let msg = event.data;
    
    let deltaSensitivity = msg.settings.get("sensitivity_movement");
    let timeSensitivityMilliSeconds = msg.settings.get("sensitivity_time");

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentLabel = prevTime + " - " + now + " "  + prevLabel;
    let topLeft = msg.payload.x * msg.payload.y;

    //we can crop the person and run it through posenet to estimate the pose.... 
    //let cropped = copy(sx, sy, sw, sh, dx, dy, dw, dh)


    let delta = Math.abs(topLeft - (prevTopLeftMap.has("person")?prevTopLeftMap.get("person"):0));        
    let timeDelta = Math.abs(new Date().getTime() - (prevTimeMap.has("person")?prevTimeMap.get("person"):0));

    if(msg.payload.label === 'person'){ 
     
        //if there's movement and not too soon
        if(delta > deltaSensitivity && timeDelta > timeSensitivityMilliSeconds){    //these can be set as sensitivity variable for configuration

            let id = msg.payload.label + "-" +new Date().getTime();
            incidents =  "<div onclick='showImage(\"" + id + "\");' onmouseover=\"this.style.color='blue';\" onmouseout=\"this.style.color='black';\">" + incidentLabel + "</div>";
    
            let output = new Message(id, msg.payload.label, incidents, msg.message);
            postMessage(output);
    
            prevTime = now;
            prevLabel = "person";
    
            console.log('Detected movement beyond the threshold. Logging this incident');

            }
        prevTimeMap.set("person",new Date().getTime());
        prevTopLeftMap.set("person",topLeft);

        }
  
    
}
