

    function showImage(incidentId) {
        
        let url = apiBaseUrl + '/api/crud/detection/domain/' + incidentId;

        if(!['localhost', '127.0.0.1'].includes(location.hostname)){
            url = 'https://' + location.hostname + '/api/crud/detection/domain/' + incidentId;
            }

        console.log("url " + url);
        
        fetch(url)
            .then((response) => {
                return response.json();
                })
            .then((data) => {
                let det = data;
                document.getElementById("snapshot").src = det.base64;

                let narrative = "<h5 class=\"card-title\">" + det.createdAt + "</h5><p class=\"card-text\">" + det.label + " ...</p><a href=\"#\" class=\"btn btn-primary\">More insights</a>";

                document.getElementById("snapshot-narrative").innerHTML = narrative;

                })
            .catch((error) => {
                console.log(error);
                });

        window.location.href = "#snapshot-container";

        }



    async function apiPostRequest(url,options) {
        const response = await fetch(
            url,
            options
            );
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
            }
        const data = await response.json();
        return data;
        }



    function defaultSensitivity(){

        console.log("resetting sensitivity defaults");

        //update localStorage 
        localStorage.setItem("sensitivity_time", DEFAULT_SENSITIVITY_TIME);
        localStorage.setItem("sensitivity_movement", DEFAULT_SENSITIVITY_MOVEMENT);
        localStorage.setItem("frames_per_second", DEFAULT_FRAMES_PER_SECOND);

        //update the sessionStorage (temp)
        sessionStorage.setItem("sensitivity_time",DEFAULT_SENSITIVITY_TIME);
        sessionStorage.setItem("sensitivity_movement",DEFAULT_SENSITIVITY_MOVEMENT);
        sessionStorage.setItem("frames_per_second",DEFAULT_FRAMES_PER_SECOND);

        //update form inputs
        deltaTimeField.value = DEFAULT_SENSITIVITY_TIME;
        deltaMovementField.value = DEFAULT_SENSITIVITY_MOVEMENT;
        fpsField.value = DEFAULT_FRAMES_PER_SECOND;

        return false;   //do not follow href
        }

    function updateSensitivity(){

        console.log("Persisting sensitivity values. New values will take effect on reload");
        //Get the values from session storage (events.js) to local storage
        localStorage.setItem("sensitivity_time",sessionStorage.getItem("sensitivity_time"));
        localStorage.setItem("sensitivity_movement",sessionStorage.getItem("sensitivity_movement"));
        localStorage.setItem("frames_per_second",sessionStorage.getItem("frames_per_second"));

        return false;   //do not follow href
        }




    function loadSettings(){

        console.log("load settings and system config from local storage if present");

        let videoSource;
        if (localStorage.getItem("sensitivity_time") && localStorage.getItem("sensitivity_movement") && localStorage.getItem("frames_per_second") && localStorage.getItem("video_source") && localStorage.getItem("api_base_url")) {
            deltaSensitivity = localStorage.getItem("sensitivity_movement");
            timeSensitivityMilliSeconds = localStorage.getItem("sensitivity_time");
            framesPerSecond = localStorage.getItem("frames_per_second");
            videoSource = localStorage.getItem("video_source");
            apiBaseUrl = localStorage.getItem("api_base_url");
            }
        else{
            deltaSensitivity = DEFAULT_SENSITIVITY_MOVEMENT;
            timeSensitivityMilliSeconds = DEFAULT_SENSITIVITY_TIME;
            framesPerSecond = DEFAULT_FRAMES_PER_SECOND;
            videoSource = DEFAULT_VIDEO;
            apiBaseUrl = DEFAULT_API_BASE_URL;
            }

        deltaTimeField.value = timeSensitivityMilliSeconds;
        deltaMovementField.value = deltaSensitivity;
        fpsField.value = framesPerSecond;
        apiBaseUrlField.value = apiBaseUrl;

        if("webcam" === videoSource){
            document.getElementById("webcamRadio").checked = true;      //clumsy and inefficient 
            }
        }

    function changeVideoSource(el) {

        console.log("changeVideoSource");
        console.log(el.value);

        localStorage.setItem("video_source",el.value);

        if("webcam" === el.value){
            console.log("setting up webcam");
            cctvUrlField.style.display = 'none';
            cctvUrlLabel.style.display = 'none';

            videoUrlField.style.display = 'none';
            videoUrlLabel.style.display = 'none';
            }
        else if("cctv" === el.value){
            console.log("setting up cctv");
            cctvUrlField.style.display = 'block';
            cctvUrlLabel.style.display = 'block';

            videoUrlField.style.display = 'none';
            videoUrlLabel.style.display = 'none';

            }
        else if("video" === el.value){
            console.log("setting up video");
            cctvUrlField.style.display = 'none';
            cctvUrlLabel.style.display = 'none';

            videoUrlField.style.display = 'block';
            videoUrlLabel.style.display = 'block';

            }
        }

    function updateVideoSource(){
        console.log("updating video source");

        if("webcam" === localStorage.getItem("video_source")){
            localStorage.setItem("video_url","");
            }
        else if("cctv" === localStorage.getItem("video_source")){
            localStorage.setItem("video_url",cctvUrlField.value);

            console.log("posting rtspUrl to backend");

            //update

            fetch(apiBaseUrl + '/api/rtsp/set/stream',
                    {
                        // Adding method type
                        method: "POST",
                        
                        // Adding body or contents to send
                        body: JSON.stringify({
                            title: "testing",
                            rtspUrl: cctvUrlField.value,
                            frameRate: 0.1
                        }),
                        
                        // Adding headers to the request
                        headers: {
                            "Content-type": "application/json; charset=UTF-8"
                        }
                    }
            
                )
                .then((response) => {
                    console.log(response);
                    //return response.json();
                    })
                .then((data) => {
                    console.log(data);
                    })
                .catch((error) => {
                    console.log(error);
                });



            }
        else if("video" === localStorage.getItem("video_source")){
            localStorage.setItem("video_url",videoUrlField.value);
            }

        return false;
        }

    function defaultVideoSource(){
        console.log("default video source");

        document.getElementById("webcamRadio").checked = true;  

        localStorage.setItem("video_source","webcam");
        localStorage.setItem("video_url","http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

        videoUrlField.value = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4";
        cctvUrlField.value = "rtsp://localhost";

        return false;//do not follow href
        }


    function defaultSystemConfig(){
        console.log("default system config");
        apiBaseUrlField.value = apiBaseUrl;
        return false;//do not follow href
        }


    function updateSystemConfig(){

        console.log("Persisting SysConfig values. New values will take after reloading !");
        //we need to validate url input in future
        apiBaseUrl = apiBaseUrlField.value;
        localStorage.setItem("api_base_url",apiBaseUrl);

        return false;   //do not follow href
        }