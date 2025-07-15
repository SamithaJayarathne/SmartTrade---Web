async function signUp() {

    const firstName = $("#firstName").val(); //jQuery
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const user = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        password: password
    };

    const userJson = JSON.stringify(user);

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body: userJson,
                header: {
                    "Content-Type": "application/json"
                }
            });


    if (response.ok) {

        const responseJSON = await response.json();

        if (responseJSON.status) {
            window.location = "verify-account.html";
        } else {
            document.getElementById("message").innerHTML = responseJSON.message;
        }

    } else {

        document.getElementById("message").innerHTML = responseJSON.message;

    }

}

