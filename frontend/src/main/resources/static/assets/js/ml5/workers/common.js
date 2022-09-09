
const logger = (event,incidents,prevLabel,prevTime) => {
    console.log(`Received ${incidents} received from main script `);
    console.log(event.data);
    if(prevLabel === event.data.label){
        console.log('Loading...');
    }
    else{
        incidents += "\n " + prevTime + " - " + new Date().toLocaleString('en-US') + " " + prevLabel ;
        postMessage(incidents);
    }
}