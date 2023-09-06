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

let incidents = "Anomaly";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];


// let deltaSensitivity;   // = 0 * 1000;     //sensitivity to movement (change in topleft)
// let timeSensitivityMilliSeconds; // = 0 * 1000;        //seconds 


let prevTopLeftMap = new Map();//movement config as map of label and delta
let prevTimeMap = new Map();

prevTimeMap.set("",new Date().getTime());
prevTopLeftMap.set("",0);

onmessage = (event) => {

    let msg = event.data;

    let deltaSensitivity = msg.settings.get("sensitivity_movement");
    let timeSensitivityMilliSeconds = msg.settings.get("sensitivity_time");

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentLabel = prevTime + " - " + now + " "  + prevLabel

    let topLeft = msg.payload.x * msg.payload.y;
    let delta = Math.abs(topLeft - (prevTopLeftMap.has(msg.payload.label)?prevTopLeftMap.get(msg.payload.label):0));        
    let timeDelta = Math.abs(new Date().getTime() - (prevTimeMap.has(msg.payload.label)?prevTimeMap.get(msg.payload.label):0));

    //if(['Robbery', 'Stealing', 'Shoplifting'].includes(msg.payload.label) && delta > deltaSensitivity && timeDelta > timeSensitivityMilliSeconds){
    if ( ['Robbery', 'Stealing', 'Shoplifting'].includes(msg.payload.label) 
            && timeDelta > timeSensitivityMilliSeconds
            && msg.payload.confidence > 0.3 ){
        console.log('Found an anomaly ... ' + msg.payload.label + ', confidence = ' + msg.payload.confidence);

        let id = msg.payload.label + "-" +new Date().getTime();
        incidents =  "<div onclick='showImage(\"" + id + "\");' onmouseover=\"this.style.color='blue';\" onmouseout=\"this.style.color='black';\">" + incidentLabel + "</div>";

        let output = new Message(id, msg.payload.label, incidents, msg.message);
        postMessage(output);

        prevTime = now;
        prevLabel = msg.payload.label;
        }

    prevTimeMap.set(msg.payload.label,new Date().getTime());
    prevTopLeftMap.set(msg.payload.label,topLeft);

}

