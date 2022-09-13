
let incidents = "Weapons";
let prevLabel = "";
let prevTime = new Date().toLocaleString('en-US').split(',')[1];

onmessage = (event) => {

    let now = new Date().toLocaleString('en-US').split(',')[1];
    
    if(prevLabel === event.data.label){
        console.log('...');
        }
    else{
        if(['knife', 'fork', 'spoon', 'bottle', 'gun', 'scissors'].includes(event.data.label)){
            console.log('Found suspected weapon ... ' + event.data.label);

            incidents += "\n " + prevTime + " - " + now + " " + prevLabel ;
            postMessage(incidents);

            prevTime = now;
            prevLabel = event.data.label;
            }
    }
           
}

