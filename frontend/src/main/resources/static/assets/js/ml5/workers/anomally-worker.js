
let incidents = "Anomaly";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');

onmessage = (event) => {
    console.log('Received anomaly from main script ');
    console.log(event.data);
    if(prevLabel === event.data.label){
        console.log('...');
        }
    else{
        incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " " + prevLabel ;
        postMessage(incidents);
        prevTime = new Date().toLocaleString('en-US');
        prevLabel = event.data.label;
        }

    }
