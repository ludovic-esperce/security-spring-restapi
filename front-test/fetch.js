const SERVER_URL = "http://localhost:8000";

const USER = {
    "email": "ada.lovelace@computing.co.uk",
    "name": "Ada Lovelace",
    "password": "adalovelace"
}

window.addEventListener("load", async (event) => {

    console.log("1. Création d'un utilisateur");
    const user = await createUser();
    console.log(`${user.name} créé.e avec succès`);

    console.log("--------------------");
    console.log("2. Tentative de connexion et récupération du JWT");
    await login();

    console.log("--------------------");
    console.log("3. requête GET sur endpoint sécurisé")
    const stations = await getStations();
    console.log(stations);
});

async function createUser() {
    try {
        console.log("Requête HTTP - création d'un utilisateur");
        let response = await fetch(`${SERVER_URL}/authentication/register`,
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(USER)
            }
        );

        console.log("Récupération résultat en JSON");
        let userResponse = await response.json();
        console.log(userResponse);
        return userResponse;
    } catch (e) {
        console.error(e.message);
    }
}

async function login() {
    try {
        console.log("Requête HTTP - connexion d'un utilisateur");
        let response = await fetch(`${SERVER_URL}/authentication/login`,
            {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    "email": USER.email,
                    "password": USER.password
                    }
                )
            }
        );

        console.log("Récupération résultat en JSON");
        let loginResponse = await response.json();
        console.log(loginResponse);

        console.log("Stockage du JWT en localstorage");
        localStorage.setItem("token", loginResponse.token);

        return loginResponse;
    } catch (e) {
        console.error(e.message);
    }
}

async function getStations() {
    try {
        console.log("Fetch request - récupération des stations sur endpoint sécurisé");
        let response = await fetch(`${SERVER_URL}/api/stations`,
            { headers: { Authorization: `Bearer ${localStorage.token}` } }
        );
        console.log("Récupération résultat en JSON");
        let stations = await response.json();
        return stations;
    } catch (e) {
        console.error(e.message);
    }
}