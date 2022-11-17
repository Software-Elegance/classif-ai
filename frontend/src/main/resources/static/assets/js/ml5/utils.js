    // const DEFAULT_SENSITIVITY_TIME = 1000;
    // const DEFAULT_SENSITIVITY_MOVEMENT = 8000;


    function showImage(incidentId) {
        
        //let url = 'http://localhost:8080/classif-ai/crud/detection/domain/' + incidentId;
        //let url = 'http://' + location.hostname + ':8080/classif-ai/crud/detection/domain/' + incidentId;
        let url = 'http://localhost/api/crud/detection/domain/' + incidentId;

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
        //Get the values from session storage to local storage
        localStorage.setItem("sensitivity_time",sessionStorage.getItem("sensitivity_time"));
        localStorage.setItem("sensitivity_movement",sessionStorage.getItem("sensitivity_movement"));
        localStorage.setItem("frames_per_second",sessionStorage.getItem("frames_per_second"));

        return false;   //do not follow href
        }


    function loadSettings(){
        console.log("load settings from local storage");

        if (localStorage.getItem("sensitivity_time") && localStorage.getItem("sensitivity_movement") && localStorage.getItem("frames_per_second")) {
            deltaSensitivity = localStorage.getItem("sensitivity_movement");
            timeSensitivityMilliSeconds = localStorage.getItem("sensitivity_time");
            framesPerSecond = localStorage.getItem("frames_per_second");
            }
          else{
            deltaSensitivity = DEFAULT_SENSITIVITY_MOVEMENT;
            timeSensitivityMilliSeconds = DEFAULT_SENSITIVITY_TIME;
            framesPerSecond = DEFAULT_FRAMES_PER_SECOND;
            }

        deltaTimeField.value = timeSensitivityMilliSeconds;
        deltaMovementField.value = deltaSensitivity;
        fpsField.value = framesPerSecond;
            
        }