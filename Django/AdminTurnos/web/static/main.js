var services = {}
var appointmentsAvailable = {}
var selector;
var selectedDate;

function clearDiv(div) {
    while (div.firstChild) {
        div.removeChild(div.lastChild);
    }
}

function onDateSelected(date){
    this.selectedDate = date.date + "/" + date.month + "/" + date.year;
    displayBtnGetProvidedServices();
}

function getProvidedServices() {
    let callback = function(json) {
        clearDiv(divProvidedServices);
        displayServiceSelection(json);
        hideDateSelection();
    }
    
    GET("/web/get-provided-services/", [
        ["job_id", /*TODO*/1/**/],
        ["date", selectedDate]
    ], callback);
}

function onServiceCheckBoxClicked(){
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");
    if (getSelectedServices().length == 0){
       hideBtnGetAvailableAppointments();
    }else{
       displayBtnGetAvailableAppointments();
    }
}

function getPromotions() {
    let callback = function(json) {
        displayPromotions(json);
    }

    GET("/web/get-promotions/", [
        ["job_id", /*TODO*/1/**/],
        ["date", selectedDate]
    ], callback);
}

function displayPromotions(json){
    
}

function getAvailableAppointments() {
    let job = 1 /*TODO*/;

    let selectedServices = getSelectedServices();

    let callback = function(json) {
        displayAppointmentSelection(json);
        hideServiceSelection();
    }
    if (selectedServices.length > 0) {
        GET("/web/get-available-appointments/", [
            ["job_id", job],
            ["date", selectedDate],
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

    let job = 1; /*TODO*/
    let appointment = selector.getSelected().getAppointment();

    body = {
        job_id: job,
        date: selectedDate,
        appointment: appointment
    }

    POST("/web/new-appointment/", body, callback);
}

