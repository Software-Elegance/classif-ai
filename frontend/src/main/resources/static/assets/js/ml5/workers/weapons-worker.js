class Message {

    constructor(id, title, message, payload) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        }
    
    }

let incidents = "Weapons";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];
    let incidentLabel = prevTime + " - " + now + " "  + prevLabel

    if(prevLabel === msg.payload.label){
        console.log('...');
        }
    else{
        if(['knife', 'fork', 'spoon', 'bottle', 'gun', 'scissors'].includes(msg.payload.label)){
            console.log('Found suspected weapon ... ' + msg.payload.label);

            let id = msg.payload.label + "-" +new Date().getTime();
            incidents =  "<div>" + incidentLabel + "</div>";

            let output = new Message(id, msg.payload.label, incidents, msg.message);
            postMessage(output);

            prevTime = now;
            prevLabel = msg.payload.label;
            }
    }
           
}

