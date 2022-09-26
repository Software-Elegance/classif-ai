class Job {

    constructor(jobName, jsFile, ell, model) {
        this.file = jsFile;
        this.name = jobName;
        this.elementName = ell;
        this.model = model;
        this.cv = document.getElementById('mycanvas');
        this.isRunning = false;

        //bind methods to the class to avoid losing `this`
        let modelReady = this.modelReady.bind(this);
    }

    modelReady() {
        document.getElementById("modelStatus").insertAdjacentHTML('beforeEnd', this.name + " Ready ");
        }

    start(detector) {
        this.isRunning = true;

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

    //listen to messages from workers
    update(event) {
            let msg = event.data;
            document.getElementById(this.elementName).insertAdjacentHTML('beforeEnd', msg.message);

            //Save incident
            let url = "http://localhost:8080/classif-ai/crud/detection/add"

            let payload = {
                label: msg.title,
                incidentId: msg.id,
                jobName: this.name,
                base64: msg.payload
                }

            let options = {
                method: 'POST',
                body: JSON.stringify(payload),
                headers: new Headers({
                    'Content-Type': 'application/json; charset=UTF-8'
                    })
                }

            apiPostRequest(url, options);
            
        }

    //error logs
    log(event) {
        console.error("OOPs");
        console.error(event.message, event);
    }

    //stop when necessary
    stop() {
        this.worker.terminate();
        this.isRunning = false;
    }


    gotDetections(error, results) {

        if(!this.isRunning){
            return;
            }

        let gotDetections = this.gotDetections.bind(this);
        if (error) {
            console.error(error);
            }
        detections = results;       //global variable. required by draw()
        results.forEach((det, i) => {
                //update ui
                document.getElementById("label").innerHTML = det.label;
                document.getElementById("confidence").innerHTML = det.confidence;

                // let cv = document.getElementById('mycanvas')
                var quality = 0.5;
                let shot    = this.cv.toDataURL('image/jpeg',quality);   //'image/jpeg' vs image/png

                let msg = new Message(1, this.name, shot, det);
                this.worker.postMessage(msg);

            }
        );

        this.detector.detect(video, gotDetections);
    }
}

