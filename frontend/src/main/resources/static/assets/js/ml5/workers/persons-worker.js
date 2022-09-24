class Message {

    constructor(id, title, message, payload) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        }
    
    }


let incidents = "<div>All Person detections</div>";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];
let prevTopLeft = 0;
//let prevTopLeftMap = new Map()

let deltaSensitivity = 8000;        //sensitivity to movement (change in topleft)
let timeSensitivityMilliSeconds = 1 * 1000;        //seconds 

let prevTopLeftMap = new Map();//movement config as map of label and delta
let prevTimeMap = new Map();

prevTimeMap.set("person",new Date().getTime());
prevTopLeftMap.set("person",0);

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentLabel = prevTime + " - " + now + " "  + prevLabel;
    let topLeft = msg.payload.x * msg.payload.y;
    //let delta = Math.abs(topLeft - prevTopLeft); 

    let delta = Math.abs(topLeft - (prevTopLeftMap.has("person")?prevTopLeftMap.get("person"):0));        
    let timeDelta = Math.abs(new Date().getTime() - (prevTimeMap.has("person")?prevTimeMap.get("person"):0));

    if(msg.payload.label === 'person'){ //this can be set as sensitivity variable for configuration
     
        if(delta < deltaSensitivity){
            //console.log('NOOP. Small difference, ignoring this detection');
            //would be nice if we can create a video of the saved frames ....
            }
        else{
            console.log('Detected movement beyond the threshold. Logging this incident');

            let id = msg.payload.label + "-" +new Date().getTime();
            incidents =  "<div onclick='showImage(\"" + id + "\");' onmouseover=\"this.style.color='blue';\" onmouseout=\"this.style.color='black';\">" + incidentLabel + "</div>";
    
            let output = new Message(id, msg.payload.label, incidents, msg.message);
            postMessage(output);
    
            prevTime = now;
            prevLabel = "person";
    
            }
        prevTimeMap.set("person",new Date().getTime());
        prevTopLeftMap.set("person",topLeft);

        }
  
    
}
