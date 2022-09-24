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

            fetch(url,options)
                .then((response) => {
                    return response.json();
                    })
                .then((data) => {
                    let det = data;
                    console.log(JSON.stringify(det));
                    })
                .catch((error) => {
                    console.log(error);
                    });
              
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

                let cv = document.getElementById('mycanvas')
                let shot    = cv.toDataURL('image/png')
                let msg = new Message(1, this.name, shot, det);

                this.worker.postMessage(msg);

            }
        );

        this.detector.detect(video, gotDetections);
    }
}

