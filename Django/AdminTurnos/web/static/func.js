const BASE_URL = "http://localhost:8000";

function POST(relative_url, body, callback){
    fetch(BASE_URL + relative_url, {
        method: "post",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization" : "Token " + profile.access_token
        },
        body: JSON.stringify(body)
    })
    .then( (response) => {
        return response.json();
    })
    .then(function(json) {
        callback(json);
    }).catch(function() {
        console.log("error");
    });
}

function GET(relative_url, params, callback){
    var url = new URL(BASE_URL + relative_url);
    url.search = new URLSearchParams(params).toString();

    fetch(url, {
        method: "get",
        headers: {
            "Accept": "application/json",
            "Content-Type": "application/json",
            "Authorization" : "Token " + profile.access_token
        }
    })
    .then((response) => {
        if(!response.ok){
            throw Error(response.statusText);
        }
        return response.json();
    })
    .then(function(json) {
        callback(json);
    }).catch(function(error) {
        console.log(error);
    });
}