class Message {
    constructor(title, message, payload) {
        this.title = title;
        this.message = message;
        this.payload = payload;
        this.timestamp = new Date().toLocaleString('en-US').split(',')[1];
        }
    }

let incidents = "<div>All Person detections</div>";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let msg = event.data;

    let now = new Date().toLocaleString('en-US').split(',')[1];

    let incidentId = prevTime + " - " + now + " "  + prevLabel
    if(msg.payload.label === 'person'){
        console.log('NOOP. Person detected on main script');
        //would be nice if we can create a video of the saved frames ....

        }
    else{

        incidents =  "<div>" + incidentId + "</div>";

        console.log("incidents = " + incidents);

        //would be nice if we can create a video of the saved frames ....

        let output = new Message("persons", incidents, null);
        postMessage(output);

        prevTime = now;
        prevLabel = "person";
        }


}
