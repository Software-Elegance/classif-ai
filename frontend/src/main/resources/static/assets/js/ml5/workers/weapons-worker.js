let incidents = "Weapons";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');

onmessage = (event) => {

        if(['knife', 'fork', 'spoon', 'bottle', 'gun', 'scissors'].includes(event.data.label)){
            console.log('Found suspected weapon ... ' + event.data.label);

            incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " " + prevLabel ;
            postMessage(incidents);

            prevTime = new Date().toLocaleString('en-US');
            prevLabel = event.data.label;
            }
   
    }

