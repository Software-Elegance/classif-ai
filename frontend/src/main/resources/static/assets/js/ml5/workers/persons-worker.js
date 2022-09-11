
let incidents = "All Person detections";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');

onmessage = (event) => {

        if(event.data.label === 'person'){
            console.log('Person detected on main script');
            }
        else{
            incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " "  + prevLabel;
            postMessage(incidents);
            prevTime = new Date().toLocaleString('en-US');
            prevLabel = "person";
            }


}
