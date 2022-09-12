
let incidents = "All Person detections";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let now = new Date().toLocaleString('en-US').split(',')[1];

    if(event.data.label === 'person'){
        console.log('Person detected on main script');
        }
    else{
        incidents += "\n " + prevTime + " - " + now + " "  + prevLabel;
        postMessage(incidents);
        prevTime = now;
        prevLabel = "person";
        }


}
