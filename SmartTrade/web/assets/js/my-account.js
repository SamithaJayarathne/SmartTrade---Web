//window.onload = function () {
//    alert("ok");
//};

window.addEventListener("load", async function () {

    loadUserData();
    loadCityData()

});

async function loadUserData() {

    const response = await fetch("MyAccount");

    if (response.ok) {

        const responseJson = await response.json();

        document.getElementById("firstName").value = responseJson.fname;
        document.getElementById("lastName").value = responseJson.lname;
        document.getElementById("since").innerHTML = `Smart Trade Member Since, ${responseJson.since}`;
        document.getElementById("currentPassword").value = responseJson.password;
        document.getElementById("username").innerHTML = `Hello, ${responseJson.fname} ${responseJson.lname}`;

    }

}

async function loadCityData() {
    try {
        const response = await fetch("CityData");

        if (response.ok) {
            const responseJson = await response.json();
            const citySelect = document.getElementById("citySelect");

            responseJson.forEach(city => {
                const option = document.createElement("option");
                option.innerHTML = city.name;
                option.value = city.id;
                citySelect.appendChild(option);
            });
        } else {
            console.error("Failed to load city data: " + response.status);
        }
    } catch (error) {
        console.error("Error fetching city data:", error);
    }
}

async function saveChanges() {

    const fname = document.getElementById("firstName").value;
    const lname = document.getElementById("lastName").value;
    const currentPassword = document.getElementById("currentPassword").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmNewPassword = document.getElementById("confirmNewPassword").value;
    const citySelect = document.getElementById("citySelect").value;
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;

    const userData = {
        fname: fname,
        lname: lname,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmNewPassword: confirmNewPassword,
        citySelect: citySelect,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode
    };

    const userDataJson = JSON.stringify(userData);

    const response = await fetch(
            "MyAccount",
            {
                method: "PUT",
                body: userDataJson,
                headers: {
                    "Content-Type": "application/json"
                }

            }
    );
    
    if(response.ok){
        
    }else{
        
    }

}
