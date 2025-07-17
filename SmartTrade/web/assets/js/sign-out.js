async function signOut() {

    const response = await fetch("SignOut");


    if (response.ok) {
        const json = await response.json();

        if (json.status) {
            window.location = "sign-in.html";
        } else {
            window.location.reload();
        }

    } else {
        console.log("Logout failed!")
    }


}

// spinner ekak dnwa nan mulin d-block krla anthimt d-none krnn
