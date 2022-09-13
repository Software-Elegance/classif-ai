
let incidents = "All incidents";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {
    
    let now = new Date().toLocaleString('en-US').split(',')[1];

    if(prevLabel === event.data.label){
        console.log('...');
        }
    else{
        console.log('Received detection from main script ');
        incidents += "\n " + prevTime + " - " +  now + " " + prevLabel;
        
        postMessage(incidents);

        prevTime = now;
        prevLabel = event.data.label;

        }
    }
