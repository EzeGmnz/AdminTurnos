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

function displayServiceSelection(json) {
    let divServiceSelection = document.getElementById("divServiceSelection");
    let divProvidedServices = document.getElementById("divProvidedServices");
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");

    divServiceSelection.style.visibility = "visible";
    divProvidedServices.style.visibility = "visible";
    btnGetAvailableAppointments.style.visibility = "visible";

    clearDiv(divProvidedServices);
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
    
    label.appendChild(document.createTextNode(name));
    div.appendChild(checkBox);
    div.appendChild(label);
    
    return div;
}

function hideServiceSelection(){
    let divServiceSelection = document.getElementById("divServiceSelection")
    let divProvidedServices = document.getElementById("divProvidedServices");
    let btnGetAvailableAppointments = document.getElementById("btnGetAvailableAppointments");

    clearDiv(divProvidedServices);
    divServiceSelection.style.visibility = "hidden"
    divProvidedServices.style.visibility = "hidden";
    btnGetAvailableAppointments.style.visibility = "hidden";
}

function displayAppointmentSelection(json) {
    let divisions = json["divisions"];
    let btnConfirm = document.getElementById("btnConfirmAppointment");
    let divAppointmentSelection = document.getElementById("divAppointmentSelection");
    let divAvailableAppointments = document.getElementById("divAvailableAppointments");

    divAppointmentSelection.style.visibility = "visible";
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

    btnConfirm.style.visibility = "hidden";
    divAppointmentSelection.style.visibility = "hidden";
    divAvailableAppointments.style.visibility = "hidden";
    clearDiv(divAvailableAppointments);
}