function onSignIn(googleUser) {
    var profile = googleUser.getBasicProfile();

    fetch("http://localhost:8000/auth/login-web-google/", {
        method: "post",
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            id_token: googleUser.getAuthResponse().id_token
        })
    })
    .then( (response) => {
        return response.json();
    })
    .then(function(json) {
        console.log(json['access_token']);
    });

}

  //console.log('Name: ' + profile.getName());
  //console.log('Image URL: ' + profile.getImageUrl());
  //console.log('Email: ' + profile.getEmail());
