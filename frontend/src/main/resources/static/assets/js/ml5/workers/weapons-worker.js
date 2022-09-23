
class Message {
    constructor(title, message, payload) {
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US').split(',')[1];
        }
    }


let incidents = "Weapons";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];
    
    if(prevLabel === msg.payload.label){
        console.log('...');
        }
    else{
        if(['knife', 'fork', 'spoon', 'bottle', 'gun', 'scissors'].includes(msg.payload.label)){
            console.log('Found suspected weapon ... ' + msg.payload.label);

            incidents += "\n " + prevTime + " - " + now + " " + prevLabel ;

            let output = new Message("weapons", incidents, null);
            postMessage(output);
            // postMessage(incidents);

            prevTime = now;
            prevLabel = msg.payload.label;
            }
    }
           
}

