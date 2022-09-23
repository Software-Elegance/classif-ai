class Message {
    constructor(title, message, payload) {
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US').split(',')[1];
        }
    }

let incidents = "All Person detections";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];

    if(msg.payload.label === 'person'){
        console.log('Person detected on main script');
        }
    else{
        incidents += "\n " + prevTime + " - " + now + " "  + prevLabel;

        let output = new Message("persons", incidents, null);
        postMessage(output);

        //postMessage(incidents);
        prevTime = now;
        prevLabel = "person";
        }


}
