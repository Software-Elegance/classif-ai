class Message {

    constructor(id, title, message, payload) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US');
        }
    
    }

let incidents = "Anomaly";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');
let prevTopLeft = 0;

onmessage = (event) => {
    let msg = event.data;

    console.log('Received anomaly from main script ');
    console.log(msg);
    if (prevLabel === msg.payload.label) {
        console.log('...');
    } else {
        incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " " + prevLabel;
        let output = new Message("anomalies", incidents, null);
        postMessage(output);
        prevTime = new Date().toLocaleString('en-US');
        prevLabel = msg.payload.label;
    }

}
