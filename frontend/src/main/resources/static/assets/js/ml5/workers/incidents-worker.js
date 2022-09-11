
let incidents = "All incidents";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US');

onmessage = (event) => {
    
    if(prevLabel === event.data.label){
        console.log('...');
        }
    else{
        console.log('Received detection from main script ');
        incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " " + prevLabel ;
        postMessage(incidents);

        prevTime = new Date().toLocaleString('en-US');
        prevLabel = event.data.label;
     }
    }
