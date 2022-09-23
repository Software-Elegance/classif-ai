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

    if(prevLabel === msg.payload.label){
        console.log('...');
        }
    else{
        console.log('Received detection from main script ');
        incidents += "\n " + prevTime + " - " +  now + " " + prevLabel;
        let output = new Message("incident log", incidents, null);
        postMessage(output);
        // postMessage(incidents);

        prevTime = now;
        prevLabel = msg.payload.label;

        }
    }
