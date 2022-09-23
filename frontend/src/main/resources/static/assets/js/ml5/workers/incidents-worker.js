class Message {
    constructor(title, message, payload) {
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US').split(',')[1];
        }
    }


let incidents = "All incidents";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentId = prevTime + " - " + now + " "  + prevLabel

    if(prevLabel === msg.payload.label){
        console.log('...');
        }
    else{
        console.log('Received detection from main script ');
        incidents =  "<div>" + incidentId + "</div>";

        let output = new Message("incident_log", incidents, null);
        postMessage(output);

        prevTime = now;
        prevLabel = msg.payload.label;

        }
    }
