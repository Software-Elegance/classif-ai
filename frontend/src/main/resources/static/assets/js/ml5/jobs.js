
class Job {

    constructor(jobName, jsFile, ell, model, settings) {
        this.file = jsFile;
        this.name = jobName;
        this.elementName = ell;
        this.model = model;
        this.cv = document.getElementById('mycanvas');
        this.isRunning = false;
        this.settings = settings;

        //bind methods to the class to avoid losing `this`
        let modelReady = this.modelReady.bind(this);
    }

    modelReady() {
        //document.getElementById("modelStatus").insertAdjacentHTML('beforeEnd', this.name + " Ready ");
        console.log(this.name + " Ready ");
        }

    // When the model is loaded
    poseNetLoaded() {
        console.log('PoseNet Loaded!' + this.name);
        }

    start(detector) {
        this.isRunning = true;

        this.detector = detector;
        let update = this.listen.bind(this);
        let log = this.log.bind(this);
        let poseNetLoaded = this.poseNetLoaded.bind(this);

        //start a thread
        this.worker = new Worker(this.file);
        this.worker.onmessage = update;
        this.worker.onerror = log;
        
        let gotDetections = this.gotDetections.bind(this);
        this.detector.detect(video, gotDetections);

        this.poseNet = ml5.poseNet(poseNetLoaded);

    }

    //listen to messages from workers
    listen(event) {
        
            //TODO: refactor to use queue / event driven

            let msg = event.data;
            document.getElementById(this.elementName).insertAdjacentHTML('beforeEnd', msg.message);

            let baseUrl = apiBaseUrl + "/api/crud";

            if(!['localhost', '127.0.0.1'].includes(location.hostname)){
                baseUrl = "https://" + location.hostname + "/api/crud";
                }

            console.log("baseUrl " + baseUrl);

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

            if(msg.title === 'person'){     //estimate pose only if persons are involved

                let pNet = this.poseNet;

                const poseSaved = async() =>{  
                    loadImage(msg.payload, function (newImage) {
                        console.log("Estimating pose");
                        pNet.multiPose(newImage)
                            .then( (results) => {
                                let poseObj = JSON.stringify(results);
                                payload.poses = poseObj;
                                apiPostRequest(baseUrl + "/pose/add", options);
                                })
                            .catch( (err) => {
                                console.error("promise error " + err);
                                return err;
                                });
                        });
                    }
                    poseSaved().then( (results) => {
                        console.log("poseSaved..." + results);
                        });
            }
            
            apiPostRequest(baseUrl + "/detection/add", options);
            
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
        else{
            detections = results;       //global variable. required by draw()
            results.forEach((det, i) => {
                    //update ui
                    document.getElementById("label").innerHTML = det.label;
                    document.getElementById("confidence").innerHTML = det.confidence;
    
                    // let cv = document.getElementById('mycanvas')
                    var quality = 0.5;
                    let shot    = this.cv.toDataURL('image/jpeg',quality);   //'image/jpeg' vs image/png
    
                    let msg = new Message(1, this.name, shot, det, this.settings);
                    this.worker.postMessage(msg);
        
                    });
                }


        this.detector.detect(video, gotDetections);
    }
}

