function displayProfile(){
    let div = document.createElement("d");
    let imgProfilePic = document.getElementById("imgProfilePic");
    let labelProfileName = document.getElementById("labelProfileName");
    labelProfileName.innerHTML = "Hola " + profile.getGivenName();
    
    document.getElementById("divGoogleSignIn").style.display = "none";
    imgProfilePic.src = profile.getImageUrl();
    
    div.appendChild(labelProfileName);
    div.appendChild(imgProfilePic);
    document.getElementById("profile").appendChild(div);
    
}

function goBack(){
  
    let divAppointmentSelection = document.getElementById("divAppointmentSelection");
    let isSelectingAppointment = divAppointmentSelection.style.display == "block";

    if(!isSelectingAppointment) {
        hideBtnGoBack();
        displayDateSelection();
        hideServiceSelection();
    } else {
        hideAppointmentSelection();
        displayServiceSelection();
    }
}

function displayBtnGoBack(){
    let btnGoBack = document.getElementById("btnGoBack");
    btnGoBack.style.visibility = "visible";
}

function hideBtnGoBack(){
    let btnGoBack = document.getElementById("btnGoBack");
    btnGoBack.style.visibility = "hidden";
}

function displayBtnGetProvidedServices(){
    let btnGetServices = document.getElementById("btnGetServices");
    btnGetServices.style.visibility = "visible";
}

function hideBtnGetProvidedServices(){
    let btnGetServices = document.getElementById("btnGetServices");
    btnGetServices.style.visibility = "hidden";
}

function displayDateSelection(){
    displayBtnGetProvidedServices();
    let divDateSelection = document.getElementById("divDateSelection");
    divDateSelection.style.display = "block";
}

function hideDateSelection(){
    hideBtnGetProvidedServices();
    let divDateSelection = document.getElementById("divDateSelection");
    divDateSelection.style.display = "none";
}

function displayServiceSelection(json) {
    hideDateSelection();
    displayBtnGoBack();
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");
    if (getSelectedServices().length == 0){
       hideBtnGetAvailableAppointments();
    }else{
       displayBtnGetAvailableAppointments();
    }

    let divServiceSelection = document.getElementById("divServiceSelection");
    let divProvidedServices = document.getElementById("divProvidedServices");
    

    divServiceSelection.style.display = "inline-block";
    divProvidedServices.style.display = "inline-block";
    

    for (let key in json) {
        services[key] = json[key]["name"];
        divProvidedServices.appendChild(createServiceDiv(key, json[key]["name"]))
    }
}

function createServiceDiv(id, name) {
    let div = document.createElement("div");
    let checkBox = document.createElement("input");
    let label = document.createElement("label");

    div.classList.add('service');
    checkBox.value = id;
    checkBox.type = "checkbox";
    checkBox.addEventListener("click", onServiceCheckBoxClicked, false);
    
    label.appendChild(document.createTextNode(name));
    div.appendChild(checkBox);
    div.appendChild(label);
    
    return div;
}

function displayBtnGetAvailableAppointments(){
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");
    btnGetAvailableAppointments.style.visibility = "visible";
}

function hideBtnGetAvailableAppointments(){
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");
    btnGetAvailableAppointments.style.visibility = "hidden";
}

function hideServiceSelection(){
    let divServiceSelection = document.getElementById("divServiceSelection")
    let divProvidedServices = document.getElementById("divProvidedServices");
    hideBtnGetAvailableAppointments();

    divServiceSelection.style.display = "none";
    divProvidedServices.style.display = "none";
    
}

function displayAppointmentSelection(json) {
    let divisions = json["divisions"];
    let btnConfirm = document.getElementById("btnConfirmAppointment");
    let divAppointmentSelection = document.getElementById("divAppointmentSelection");
    let divAvailableAppointments = document.getElementById("divAvailableAppointments");

    divAppointmentSelection.style.display = "block";
    divAvailableAppointments.style.display = "block";
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
    let btnConfirm = document.getElementById("btnConfirmAppointment");

    selector.select(this.id);
    if (selector.getSelected() != null) {
        btnConfirm.style.visibility = "visible";
    } else {
        btnConfirm.style.visibility = "hidden";
    }
}

function hideAppointmentSelection(){
    let btnConfirm = document.getElementById("btnConfirmAppointment");
    let divAppointmentSelection = document.getElementById("divAppointmentSelection");
    let divAvailableAppointments = document.getElementById("divAvailableAppointments");

    clearDiv(divAvailableAppointments);
    btnConfirm.style.visibility = "hidden";
    divAvailableAppointments.style.display = "none";
    divAppointmentSelection.style.display = "none";
    clearDiv(divAvailableAppointments);
}