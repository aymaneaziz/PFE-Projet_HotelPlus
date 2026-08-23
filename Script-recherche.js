
document.addEventListener("DOMContentLoaded", function () {
    attachImageCarouselEvents(); 
});
function attachImageCarouselEvents() {
    document.querySelectorAll(".hotel-card").forEach(function (hotelCard) {
        let imageContainer = hotelCard.querySelector(".hotel-image");
        let imageElement = hotelCard.querySelector(".hotel-image-element");
        let prevButton = hotelCard.querySelector(".prev-button");
        let nextButton = hotelCard.querySelector(".next-button");

        if (!imageContainer || !imageElement || !prevButton || !nextButton) return;

        let images = JSON.parse(imageContainer.getAttribute("data-images"));
        let currentIndex = 0;

        prevButton.addEventListener("click", function () {
            currentIndex = (currentIndex - 1 + images.length) % images.length;
            imageElement.src = images[currentIndex];
        });

        nextButton.addEventListener("click", function () {
            currentIndex = (currentIndex + 1) % images.length;
            imageElement.src = images[currentIndex];
        });
    });
}



//------------------------------------------appliquer filter----------------------------------
/*function applyFilters() {
    var formData = new FormData(document.getElementById('filters-form'));

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'traitement.php', true); 

    xhr.onload = function () {
        if (xhr.status === 200) {
            

            document.getElementById('hotelsList').innerHTML = xhr.responseText;
            attachImageCarouselEvents(); 
          
            
          
        }
    };

    xhr.send(formData);
}
document.querySelectorAll('#filters-form input').forEach(function(input) {
    input.addEventListener('change', applyFilters);
});
applyFilters();


//------------------------------------------Recuperer hotels----------------------------------
function applyFilters2() {
    var formData = new FormData(document.getElementById('filters-form'));

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '../php/traitement_tabhotels.php', true);

    xhr.onload = function () {
          if (xhr.status === 200) {

     
            hotels = JSON.parse(xhr.responseText);
            console.log(hotels);
            initMap(hotels) ;
     
    }

    };

    xhr.send(formData);
}
document.querySelectorAll('#filters-form input').forEach(function(input) {
    input.addEventListener('change', applyFilters2);
});

applyFilters2();



*/

 // Fonction pour faire défiler la page vers le haut
/* function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth' // Permet un défilement en douceur
    });
}

// Ajouter un événement sur les éléments du formulaire
const formElements = document.querySelectorAll('#filters-form input');
formElements.forEach(element => {
    element.addEventListener('change', scrollToTop);
});
*/





//------------------------------------------button star----------------------------------
document.querySelectorAll(".stars-filter label").forEach(function(label) {
    label.addEventListener("click", function() {
        let input = label.querySelector("input");

    
        if (input.checked) {
            input.checked = false;
            label.classList.remove("active");
        } else {
            input.checked = true;
            label.classList.add("active");
        }
    });
});


 // function for  update price 
 function updatePrice() {
    var rangeValue = document.getElementById('price').value;
    document.getElementById('price-value').value = rangeValue + '€';
}
function updateRange() {
    var textValue = document.getElementById('price-value').value.replace('€', ''); // Enlève le symbole €
    var numericValue = parseInt(textValue);


    if (!isNaN(numericValue) && numericValue >= 0 && numericValue <= 10000) {
        document.getElementById('price').value = numericValue;
    }

    
    document.getElementById('price-value').value = Math.min(numericValue, 10000) + '€';
}





//function api city morroco
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



function getStarsHtml(count) {
    const fullStars = '★'.repeat(count); 
    const emptyStars = '☆'.repeat(5 - count); 
    return fullStars + emptyStars; 
}

function clearMarkers() {
    markers.forEach(marker => marker.setMap(null));
    markers = [];
}

function getRatingColor(rating) {
    if (rating >= 8) return '#4CAF50'; // Vert pour excellent
    if (rating >= 6) return '#FFC107'; // Jaune pour bon
    return '#F44336'; // Rouge pour mauvais
}


function getRatingColorClass(rating) {
    if (rating >= 8) return 'rating-excellent';
    if (rating >= 6) return 'rating-good';
    return 'rating-bad';
}



document.getElementById('open-map-btn').addEventListener('click', function() {
    const modal = document.getElementById('map-modal');
    modal.style.display = 'flex';
    document.body.style.overflow = 'hidden';

    // Redimensionner la carte modale après affichage
    setTimeout(() => {
        if (mapModal) {
            google.maps.event.trigger(mapModal, 'resize');
            mapModal.setCenter(map.getCenter()); // Ajuster le centre
        }
    }, 300);
});

document.getElementById('close-map-btn').addEventListener('click', function() {
    const modal = document.getElementById('map-modal');
    modal.style.display = 'none';
    document.body.style.overflow = 'auto';
});


let map, mapModal;
let markers = [];
let markersModal = [];
let infoWindow;

function initMap(hotels) {
    const maroc = { lat: 31.7917, lng: -7.0926 };

    // Carte principale
    map = new google.maps.Map(document.getElementById("hotel-map"), {
        zoom: 6,
        center: maroc,
        mapTypeControl: true,
        streetViewControl: true,
        fullscreenControl: true,
    });

    // Carte modale (plein écran)
    mapModal = new google.maps.Map(document.getElementById("hotel-map-modal"), {
        zoom: 6,
        center: maroc,
        mapTypeControl: true,
        streetViewControl: true,
        fullscreenControl: true,
    });

    infoWindow = new google.maps.InfoWindow();

    // Ajout des marqueurs sur les deux cartes
    addHotelMarkers(hotels, map, markers);
    addHotelMarkers(hotels, mapModal, markersModal);
}

function addHotelMarkers(hotels, mapInstance, markersArray) {
    clearMarkers(markersArray);

    hotels.forEach((hotel, index) => {
        if (hotel.location) {
            const marker = new google.maps.Marker({
                position: hotel.location,
                map: mapInstance,
                title: hotel.name,
                animation: google.maps.Animation.DROP,
                icon: {
                    path: google.maps.SymbolPath.CIRCLE,
                    fillColor: getRatingColor(hotel.rating),
                    fillOpacity: 0.9,
                    strokeWeight: 1,
                    strokeColor: '#ffffff',
                    scale: 10
                }
            });

            const contentString = `
                <div class="map-info-window">
                    <h3>${hotel.name}</h3>
                    <span class="hotel-stars">${getStarsHtml(hotel.stars)}</span>
                    <p>${hotel.address}</p>
                </div>
            `;

            marker.addListener("click", () => {
                infoWindow.setContent(contentString);
                infoWindow.open(mapInstance, marker);
                const hotelCard = document.querySelector(`.hotel-card[data-index="${hotel.index}"]`);
               /* if (hotelCard) {
                    hotelCard.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    hotelCard.classList.add('highlighted');
                    setTimeout(() => {
                        hotelCard.classList.remove('highlighted');
                    }, 2000);
                }*/
                
            });

            markersArray.push(marker);
        }
    });

    if (markersArray.length > 0) {
        const bounds = new google.maps.LatLngBounds();
        markersArray.forEach(marker => bounds.extend(marker.getPosition()));
        mapInstance.fitBounds(bounds);

        if (markersArray.length === 1) {
            mapInstance.setZoom(5);
        }
    }
}

function clearMarkers(markersArray) {
    markersArray.forEach(marker => marker.setMap(null));
    markersArray.length = 0;
}


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









// Modification de la fonction applyFilters pour intégrer le filtre de nom et le tri
function applyFilters() {
    var formData = new FormData(document.getElementById('filters-form'));
    
    
    var searchHotelName = document.querySelector('.search-input2 input').value;
  
    var sortOption = document.querySelector('.sort-select').value;
    formData.append('hotelName', searchHotelName);
    formData.append('sortOption', sortOption);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'traitement.php', true); 

    xhr.onload = function () {
        if (xhr.status === 200) {
            document.getElementById('hotelsList').innerHTML = xhr.responseText;
            attachImageCarouselEvents(); 
        }
    };

    xhr.send(formData);
}

document.querySelectorAll('#filters-form input').forEach(function(input) {
    input.addEventListener('change', applyFilters);
});

document.addEventListener('DOMContentLoaded', function() {
    
    var searchTimeout;
    document.querySelector('.search-input2 input').addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(applyFilters, 300);
    });
    
   
    document.querySelector('.sort-select').addEventListener('change', applyFilters);
    
   
    applyFilters();
});






// Fonction applyFilters2 pour initialiser la carte
function applyFilters2() {
    var formData = new FormData(document.getElementById('filters-form'));
    
    
    var searchHotelName = document.querySelector('.search-input2 input').value;
    var sortOption = document.querySelector('.sort-select').value;
    
    formData.append('hotelName', searchHotelName);
    formData.append('sortOption', sortOption);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'traitement_tabhotels.php', true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            hotels = JSON.parse(xhr.responseText);
            console.log(hotels);
            initMap(hotels);
        }
    };

    xhr.send(formData);
}

document.querySelectorAll('#filters-form input').forEach(function(input) {
    input.addEventListener('change', applyFilters2);
});

document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.search-input2 input').addEventListener('input', function() {
        clearTimeout(searchTimeout);
        searchTimeout = setTimeout(applyFilters2, 300);
    });
    
    document.querySelector('.sort-select').addEventListener('change', applyFilters2);
    
    
    applyFilters2();
});






