class Message {
    constructor(title, message, payload) {
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US').split(',')[1];
        }
    }

let incidents = "Anomaly";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');

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
        // postMessage(incidents);
        prevTime = new Date().toLocaleString('en-US');
        prevLabel = msg.payload.label;
    }

}
