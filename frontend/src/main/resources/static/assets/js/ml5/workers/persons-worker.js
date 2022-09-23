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

    let incidentId = + prevTime + " - " + now + " "  + prevLabel
    if(msg.payload.label === 'person'){
        console.log('NOOP. Person detected on main script');
        }
    else{

        // incidents +=  "\n " + prevTime + " - " + now + " "  + prevLabel + "<img src="+ msg.message +" alt=" + prevTime + " - " + now + " "  + prevLabel + " />";
        incidents +=  "\n " + incidentId + "<img src=\"\" alt=" + incidentId + " />";

        console.log("incidents = " + incidents);
        // incidents += "\n " + incidentId;

        //would be nice if we can create a video of the saved frames ....

        let output = new Message("persons", incidents, null);
        postMessage(output);

        //postMessage(incidents);
        prevTime = now;
        prevLabel = "person";
        }


}
