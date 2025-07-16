async function userSignIn() {

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const userData = {
        email: email,
        password: password
    };

    const signInJason = JSON.stringify(userData);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: signInJason,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const responseJSON = await response.json();

        if (responseJSON.status) {

            if (responseJSON.message === "1") {
                window.location = "verify-account.html";
            } else {
                window.location = "index.html";
            }

        } else {
            document.getElementById("message").innerHTML = responseJSON.message;
        }

    } else {

        document.getElementById("message").innerHTML = "Sign in failed. Please try again";

    }


}


async function authenticateUser() {

    const response = await fetch("SignIn");

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {
            window.location = "index.html";
        } 

    } else {

    }

}

