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
    var checkboxes_div = document.getElementById("servicesCheckBoxes");

    clearDiv(checkboxes_div);
    for (var key in json) {
        services[key] = json[key]["name"];
        checkboxes_div.appendChild(createServiceDiv(key, json[key]["name"]))
    }

}

function createServiceDiv(id, name) {
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
        //TODO populate promotions
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
    var childs = document.getElementById("servicesCheckBoxes")
        .getElementsByTagName("div");

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
    appointmentsContainer = document.getElementById("availableAppointmentsContainer");

    timeDivisions = [];
    for (var x in divisions) {
        timeDivisions.push(new TimeDivision(services, divisions[x]));
    }

    selector = new Selector(timeDivisions, onAppointmentSelected);

    clearDiv(appointmentsContainer);
    appointmentsContainer.appendChild(selector.getDiv());
}

function onAppointmentSelected() {
    selector.select(this.id);
    let btnConfirm = document.getElementById("btnConfirmAppointment");
    if (selector.getSelected() != null) {
        if (btnConfirm.classList.contains("btnConfirmAppointmentHidden")) {
            btnConfirm.classList.remove("btnConfirmAppointmentHidden");
        }
    } else {
        btnConfirm.classList.add("btnConfirmAppointmentHidden");
    }
}

function confirmAppointment() {
    let callback = function(json) {
        console.log(json);
    }

    let job = document.getElementById("jobInput").value;
    let appointment = selector.getSelected().getAppointment();
    let date = document.getElementById("dateInput").value;
    
    body = {
        job_id: job,
        date: date,
        appointment: appointment
    }

    POST("/web/new-appointment/", body, callback);
}