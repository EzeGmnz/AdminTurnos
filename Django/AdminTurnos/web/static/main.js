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
        displayServiceSelection(json);
        hideAppointmentSelection();
    }

    GET("/web/get-provided-services/", [
        ["job_id", job],
        ["date", date]
    ], callback);
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
        displayAppointmentSelection(json);
        hideServiceSelection();
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

function confirmAppointment() {
    let callback = function(json) {
        console.log(json);
        hideAppointmentSelection();
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

