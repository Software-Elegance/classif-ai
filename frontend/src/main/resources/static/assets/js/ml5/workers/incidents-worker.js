class Message {

    constructor(id, title, message, payload) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        }
    
    }


let incidents = "All incidents";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];
let deltaSensitivity = 8000;     //sensitivity to movement (change in topleft)
let timeSensitivityMilliSeconds = 0 * 1000;        //seconds 

let prevTopLeftMap = new Map();//movement config as map of label and delta
let prevTimeMap = new Map();

prevTimeMap.set("",new Date().getTime());
prevTopLeftMap.set("",0);

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentLabel = prevTime + " - " + now + " "  + msg.payload.label

  
    let topLeft = msg.payload.x * msg.payload.y;
    let delta = Math.abs(topLeft - (prevTopLeftMap.has(msg.payload.label)?prevTopLeftMap.get(msg.payload.label):0));        
    let timeDelta = Math.abs(new Date().getTime() - (prevTimeMap.has(msg.payload.label)?prevTimeMap.get(msg.payload.label):0));

    //if there's movement and not too soon
    if(delta > deltaSensitivity && timeDelta > timeSensitivityMilliSeconds){//these can be set as sensitivity variable for configuration
        console.log('Detected deltas beyond the threshold. Logging this incident');

        let id = msg.payload.label + "-" + new Date().getTime();
        incidents =  "<div onclick='showImage(\"" + id + "\");' onmouseover=\"this.style.color='blue';\" onmouseout=\"this.style.color='black';\">" + incidentLabel + "</div>";

        let output = new Message(id, msg.payload.label, incidents, msg.message);
        postMessage(output);

        prevTime = now;
        prevLabel = msg.payload.label;

        }

    prevTimeMap.set(msg.payload.label,new Date().getTime());
    prevTopLeftMap.set(msg.payload.label,topLeft);

    }
