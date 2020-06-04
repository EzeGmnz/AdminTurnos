function getProvidedServices() {
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;

    let callback = function(json) {
        populateAvailableServices(json);
    }

    GET("/web/get-provided-services/", [
        ["job_id", job],
        ["date", date]
    ], callback);
}

function populateAvailableServices(json) {
    var checkboxes_div = document.getElementById("servicesCheckBoxes");

    while (checkboxes_div.firstChild) {
        checkboxes_div.removeChild(checkboxes_div.lastChild);
    }
    for (var key in json) {
        checkboxes_div.appendChild(createServiceDiv(key, json[key]["name"]))
    }

}

function createServiceDiv(id, name){
    var div = document.createElement("div");
    div.classList.add('service');
    var checkBox = document.createElement("input");
    var label = document.createElement("label");
    checkBox.type = "checkbox";
    checkBox.value = id;
    label.appendChild(document.createTextNode(name));
    div.appendChild(checkBox);
    div.appendChild(label);
    return div;
}


function getPromotions() {
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;

    let callback = function(json) {
        //console.log(json);
    }

    GET("/web/get-promotions/", [
        ["job_id", job],
        ["date", date]
    ], callback);
}

function getAvailableAppointments() {
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;
    let selectedServices = getSelectedServices();

    let callback = function(json) {
        //console.log(json);
    }

    GET("/web/get-available-appointments/", [
        ["job_id", job],
        ["date", date],
        ["services", selectedServices]
    ], callback);
}

function getSelectedServices(){
    selectedServices = [];
    var childs = document.getElementById("servicesCheckBoxes")
    .getElementsByTagName("div");
    
    for (i = 0; i < childs.length; i++){
        let checkbox = childs[i].firstChild;

        if(checkbox.checked){
            selectedServices.push(checkbox.value);
        }
    }
    return selectedServices;
}