var services = {}
var appointmentsAvailable = {}
var selector;

function clearDiv(div) {
    while (div.firstChild) {
        div.removeChild(div.lastChild);
    }
}

function getProvidedServices() {
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;

    let callback = function(json) {
        displayProvidedServices(json);
    }

    GET("/web/get-provided-services/", [
        ["job_id", job],
        ["date", date]
    ], callback);
}

function displayProvidedServices(json) {
    let checkboxes_div = document.getElementById("divProvidedServices");
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");

    checkboxes_div.style.visibility = "visible";
    btnGetAvailableAppointments.style.visibility = "visible";
    clearDiv(checkboxes_div);
    
    for (let key in json) {
        services[key] = json[key]["name"];
        checkboxes_div.appendChild(createServiceDiv(key, json[key]["name"]))
    }
}

function createServiceDiv(id, name) {
    let div = document.createElement("div");
    div.classList.add('service');
    let checkBox = document.createElement("input");
    let label = document.createElement("label");
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
        displayPromotions(json);
    }

    GET("/web/get-promotions/", [
        ["job_id", job],
        ["date", date]
    ], callback);
}

function displayPromotions(json){
    
}

function getAvailableAppointments() {
    let job = document.getElementById("jobInput").value;
    let date = document.getElementById("dateInput").value;
    let selectedServices = getSelectedServices();

    let callback = function(json) {
        displayAvailableAppointments(json);
    }
    if (selectedServices.length > 0) {
        GET("/web/get-available-appointments/", [
            ["job_id", job],
            ["date", date],
            ["services", selectedServices]
        ], callback);
    } else {
        console.log("No service selected");
    }
}

function getSelectedServices() {
    selectedServices = [];
    let childs = document.getElementById("divProvidedServices").getElementsByTagName("div");

    for (i = 0; i < childs.length; i++) {
        let checkbox = childs[i].firstChild;

        if (checkbox.checked) {
            selectedServices.push(checkbox.value);
        }
    }
    return selectedServices;
}

function displayAvailableAppointments(json) {
    let divisions = json["divisions"];
    let btnConfirm = document.getElementById("btnConfirmAppointment");

    divAvailableAppointments = document.getElementById("divAvailableAppointments");
    divAvailableAppointments.style.visibility = "visible";
    btnConfirm.style.visibility = "visible";
    clearDiv(divAvailableAppointments);

    timeDivisions = [];
    for (var x in divisions) {
        timeDivisions.push(new TimeDivision(services, divisions[x]));
    }

    selector = new Selector(timeDivisions, onAppointmentSelected);
    divAvailableAppointments.appendChild(selector.getDiv());
}


function onAppointmentSelected() {
    selector.select(this.id);
    let btnConfirm = document.getElementById("btnConfirmAppointment");
    if (selector.getSelected() != null) {
        btnConfirm.style.visibility = "visible";
    } else {
        btnConfirm.style.visibility = "hidden";
    }
}

function confirmAppointment() {
    let callback = function(json) {
        console.log(json);
        updateUIAfterConfirm();
    }

    let job = document.getElementById("jobInput").value;
    let appointment = selector.getSelected().getAppointment();
    let date = document.getElementById("dateInput").value;

    console.log(selector.getSelected().toString());

    body = {
        job_id: job,
        date: date,
        appointment: appointment
    }

    POST("/web/new-appointment/", body, callback);
}

function updateUIAfterConfirm(){
    hideAvailableAppointments();
    hideProvidedServices();
}

function hideProvidedServices(){
    let checkboxes_div = document.getElementById("divProvidedServices");
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");

    clearDiv(checkboxes_div);
    checkboxes_div.style.visibility = "hidden";
    btnGetAvailableAppointments.style.visibility = "hidden";
}

function hideAvailableAppointments(){
    let divAvailableAppointments = document.getElementById("divAvailableAppointments")
    let btnConfirm = document.getElementById("btnConfirmAppointment");

    clearDiv(divAvailableAppointments);
    divAvailableAppointments.style.visibility = "hidden";
    btnConfirm.style.visibility = "hidden";
}