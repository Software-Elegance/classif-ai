function showImage(incidentId) {
    
    let url = 'http://localhost:8080/classif-ai/crud/detection/domain/' + incidentId;
    fetch(url)
        .then((response) => {
            return response.json();
            })
        .then((data) => {
            let det = data;
            document.getElementById("snapshot").src = det.base64;

            let narrative = "<h5 class=\"card-title\">" + det.createdAt + "</h5><p class=\"card-text\">" + det.label + " ...</p>";
            document.getElementById("snapshot-narrative").innerHTML = narrative;

            })
        .catch((error) => {
            console.log(error);
            });


    }