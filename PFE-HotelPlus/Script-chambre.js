
document.addEventListener("DOMContentLoaded", function() {
    let currentIndex = 0;

    const mainImage = document.getElementById("main-img");
    const secondaryImages = document.querySelectorAll(".secondary img");
    const prevBtn = document.getElementById("prev-btn");
    const nextBtn = document.getElementById("next-btn");

    function updateHotelImages() {
        if (images.length === 0) return;

        mainImage.src = images[currentIndex];

        for (let i = 0; i < secondaryImages.length; i++) {
            let imgIndex = (currentIndex + i + 1) % images.length;
            secondaryImages[i].src = images[imgIndex];
        }
    }

    nextBtn.addEventListener("click", function () {
        currentIndex = (currentIndex + 1) % images.length;
        updateHotelImages();
    });

    prevBtn.addEventListener("click", function () {
        currentIndex = (currentIndex - 1 + images.length) % images.length;
        updateHotelImages();
    });

    updateHotelImages();
});



//calcul prix
document.addEventListener('DOMContentLoaded', function() {
    const serviceCheckboxes = document.querySelectorAll('.service-checkbox');
    const selectedServicesDiv = document.getElementById('selected-services');
    const totalPriceSpan = document.getElementById('total-price');
    const roomPriceElement = document.getElementById('priceC');
    
    let totalPrice = parseFloat(roomPriceElement.getAttribute('value'));  
    if (isNaN(totalPrice)) {
        totalPrice = 0; 
    }

    function updateSelectedServices() {
        selectedServicesDiv.innerHTML = ''; 
        let newTotalPrice = totalPrice;
        let selectedServices = [];

        serviceCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                const serviceName = checkbox.value;
                let servicePrice = parseFloat(checkbox.getAttribute('data-price'));

                if (isNaN(servicePrice)) {
                    servicePrice = 0; 
                }

                const serviceDiv = document.createElement('div');
                serviceDiv.classList.add('price-item');
                serviceDiv.innerHTML = `<span>${serviceName} </span><span> ${servicePrice.toFixed(2)}€</span>`;
                selectedServicesDiv.appendChild(serviceDiv);

                // Stocker les services pour la session
                selectedServices.push({
                    name: serviceName,
                    price: servicePrice
                });

                newTotalPrice += servicePrice; 
            }
        });

        totalPriceSpan.textContent = newTotalPrice.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",") + '€';

        
        // Envoyer les données au serveur pour stockage en session PHP
        saveToSession(totalPrice, selectedServices, newTotalPrice);
    }
    
    function saveToSession(roomPrice, services, totalPrice) {
        fetch('Save_reservation.php', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                roomPrice: roomPrice,
                selectedServices: services,
                totalPrice: totalPrice
            })
        })
        .then(response => response.json())
        .then(data => {
            console.log('Session sauvegardée:', data);
        })
        .catch(error => {
            console.error('Erreur:', error);
        });
    }

    serviceCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', updateSelectedServices);
    });

    updateSelectedServices();
});





  

// date check 
document.getElementById('departure_date').addEventListener('change', validateDates);
document.getElementById('return_date').addEventListener('change', validateDates);

function validateDates() {
    var departureDate = document.getElementById('departure_date').value;
    var returnDate = document.getElementById('return_date').value;

    if (departureDate && returnDate) {
        var depDate = new Date(departureDate);
        var retDate = new Date(returnDate);

        if (retDate <= depDate) {
            document.getElementById('return_date').setCustomValidity('Veuillez choisir une date de retour valide');
        } else {
            document.getElementById('return_date').setCustomValidity('');
        }
    }
}

//avis
function submitReview() {
    const rating = document.querySelector('input[name="rating"]:checked');
    const reviewText = document.getElementById('review-text').value.trim();
    const errorMessage = document.getElementById('error-message');

    const urlParams = new URLSearchParams(window.location.search);
    const roomId = urlParams.get('idc');

    if (!rating || !reviewText) {
        errorMessage.textContent = "Veuillez sélectionner une note et entrer un avis";
        errorMessage.style.display = "block"; 
        return;
    }

    errorMessage.style.display = "none"; 

    const data = "rating=" + encodeURIComponent(rating.value) +
                 "&review=" + encodeURIComponent(reviewText) +
                 "&roomId=" + encodeURIComponent(roomId);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "AvisClient.php", true);
    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                console.log(xhr.responseText);
                window.location.reload();
            } else {
                errorMessage.textContent = "Erreur lors de l'envoi de l'avis. Veuillez réessayer.";
                errorMessage.style.display = "block";
            }
        }
    };

    xhr.send(data);
}




//function api city morroco-----------------------------------------------------------------------------------------------------
document.addEventListener("DOMContentLoaded", function () {
    const apiKey = "OXJ0V0xVYWd0QzNDb2RZSWg3UmNubEVCRkw5SGdBVlJiaXZJRmlUNg==";
    const countryCode = "MA";  
    const url = `https://api.countrystatecity.in/v1/countries/${countryCode}/cities`;
    let cities = [];

    fetch(url, {
        method: "GET",
        headers: {
            "X-CSCAPI-KEY": apiKey
        }
    })
    .then(response => response.json())
    .then(data => {
        cities = data.map(city => city.name); // Stocker toutes les villes
    })
    .catch(error => console.error("Erreur lors de la récupération des villes:", error));

    const input = document.getElementById("destination");
    const suggestionsContainer = document.getElementById("suggestions");

    input.addEventListener("input", function () {
        const searchText = input.value.toLowerCase();
        suggestionsContainer.innerHTML = "";
        
        if (searchText.length === 0) {
            suggestionsContainer.style.display = "none";
            return;
        }

        const filteredCities = cities.filter(city => city.toLowerCase().includes(searchText)).slice(0, 10);
        
        if (filteredCities.length > 0) {
            suggestionsContainer.style.display = "block";
            filteredCities.forEach(city => {
                const div = document.createElement("div");
                div.classList.add("suggestion");
                div.textContent = city;
                div.addEventListener("click", function () {
                    input.value = city;
                    suggestionsContainer.style.display = "none";
                });
                suggestionsContainer.appendChild(div);
            });
        } else {
            suggestionsContainer.style.display = "none";
        }
    });

    document.addEventListener("click", function (e) {
        if (!input.contains(e.target) && !suggestionsContainer.contains(e.target)) {
            suggestionsContainer.style.display = "none";
        }
    });
});




