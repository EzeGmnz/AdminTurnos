function getProfile(){
    let callback = function(json){
        console.log(json);
    }
    GET("/android/profile/", null, callback);
}

function getProvidedServices(){
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;

    let callback = function(json){
        console.log(json);
    }

    GET("/web/get-provided-services/", [["job_id", job], ["date", date]], callback);
}

function getPromotions(){
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;

    let callback = function(json){
        console.log(json);
    }

    GET("/web/get-promotions/", [["job_id", job], ["date", date]], callback);
}