var profile;

function onSignIn(googleUser) {
    profile = googleUser.getBasicProfile();

    fetch(BASE_URL + "/auth/login-web-google/", {
        method: "post",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: JSON.stringify({id_token: googleUser.getAuthResponse().id_token})
    })
    .then( (response) => {
        return response.json();
    })
    .then(function(json) {
        profile.access_token = json.access_token;
        updateUI();
    });
}

function updateUI(){
    document.getElementById("googleSignInBtn").style.display = "none";
    var div = document.createElement("d")
    var image = new Image()
    image.src = profile.getImageUrl();
    var nameText = document.createTextNode("Welcome "+ profile.getGivenName())
    div.appendChild(nameText);
    div.appendChild(image);
    document.getElementById("profile").appendChild(div);
    
}
