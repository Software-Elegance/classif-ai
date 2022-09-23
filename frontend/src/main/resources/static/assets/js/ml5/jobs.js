class Job {

    constructor(jobName, jsFile, ell, model) {
        this.file = jsFile;
        this.name = jobName;
        this.elementName = ell;
        this.model = model;
        //bind methods to the class to avoid losing `this`
        let modelReady = this.modelReady.bind(this);
    }

    modelReady() {
        select("#modelStatus").html(this.name + " Ready !");
    }

    start(detector) {
        this.detector = detector;
        let update = this.update.bind(this);
        let log = this.log.bind(this);

        //start a thread
        this.worker = new Worker(this.file);
        this.worker.onmessage = update;
        this.worker.onerror = log;
        
        let gotDetections = this.gotDetections.bind(this);
        this.detector.detect(video, gotDetections);
    }

    //update ui
    update(event) {
            let msg = event.data;
            document.getElementById(this.elementName).innerHTML = msg.message;
    }

    //error logs
    log(event) {
        console.error("OOPs");
        console.error(event.message, event);
    }

    //stop when necessary
    stop() {
        this.worker.terminate();
    }

    gotDetections(error, results) {
        let gotDetections = this.gotDetections.bind(this);
        if (error) {
            console.error(error);
        }
        detections = results;       //global variable. required by draw()
        results.forEach((det, i) => {
                //update ui
                document.getElementById("label").innerHTML = det.label;
                document.getElementById("confidence").innerHTML = det.confidence;

                let msg = new Message("jobname", "message", det);

                //this.worker.postMessage(det);
                this.worker.postMessage(msg);

                // if(det.label === 'person'){
                //     console.log("pushing perrson to local storage");
                //     const cv = document.getElementById('mycanvas')
                //     const shot    = cv.toDataURL('image/png')
                //     console.log("shot => " + shot);
                //     //storeItem('xpos', posX);      //save in local storage
                //     }
                

            }
        );
        this.detector.detect(video, gotDetections);
    }
}

