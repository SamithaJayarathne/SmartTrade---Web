async function verifyAccount() {

    const verification_code = document.getElementById("verificaion_code").value;

    const verification = {
        code: verification_code
    };

    const verificationJson = JSON.stringify(verification);

    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: verificationJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {

        const responseJson = await response.json();

        if (responseJson.status) {

            window.location = "index.html";

        } else {

            if (responseJson.message === "1") { // email not found
                window.location = "sign-in.html";
            } else {
                document.getElementById("errorMessage").innerHTML = responseJson.message;
            }

        }

    } else {
        document.getElementById("errorMessage").innerHTML = "Verification failed. Please try again";
    }

}


