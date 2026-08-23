document.addEventListener('DOMContentLoaded', function() {
    const sliderContainer = document.querySelector('.slider-container');
    const slider = sliderContainer.querySelector('.hotel-slider');
    const nextBtn = sliderContainer.querySelector('.slider-nav.next');

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            slider.scrollBy({
                left: 300,
                behavior: 'smooth'
            });
        });
    }

    // Ajout de la fonctionnalité de balayage pour mobile
    let startX, endX;
    slider.addEventListener('touchstart', (e) => {
        startX = e.changedTouches[0].screenX;
    }, {passive: true});

    slider.addEventListener('touchend', (e) => {
        endX = e.changedTouches[0].screenX;
        handleSwipe();
    }, {passive: true});

    const handleSwipe = () => {
        const threshold = 50;
        if (startX - endX > threshold) {
            slider.scrollBy({
                left: 300,
                behavior: 'smooth'
            });
        } else if (endX - startX > threshold) {
            slider.scrollBy({
                left: -300,
                behavior: 'smooth'
            });
        }
    };
});

//Découvrez plus d'offres
function toggleHotelList() {
    const hotelList = document.getElementById("hotel-list");
    
    // Si le bloc est actuellement caché, on le montre, sinon on le cache
    if (hotelList.style.display === "block") {
        hotelList.style.display = "none";
    } else {
        hotelList.style.display = "block";
    }
}




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

//api
document.addEventListener("DOMContentLoaded", function () {
    // Code de suggestion de ville existant
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
        cities = data.map(city => city.name); 
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

    // NOUVEAU CODE POUR LES CAROUSELS
    const sliders = document.querySelectorAll('.hotel-slider');
    
    sliders.forEach(function(slider) {
        // Sélectionner les cartes et les boutons de navigation
        const cards = slider.querySelectorAll('.hotel-card, .package-card');
        const prevBtn = slider.querySelector('.left-arrow');
        const nextBtn = slider.querySelector('.right-arrow');
        
        if (!cards.length || !prevBtn || !nextBtn) return;
        
        // Créer un conteneur intérieur pour les cartes
        const innerContainer = document.createElement('div');
        innerContainer.className = 'slider-container';
        
        // Déplacer toutes les cartes dans le conteneur intérieur
        cards.forEach(card => {
          
            card.parentNode.removeChild(card);
            
            innerContainer.appendChild(card);
        });
        
        
        slider.insertBefore(innerContainer, nextBtn);
        
       
        let position = 0;
        let cardWidth = 0;
        let visibleCards = 0;
        
        // Fonction pour calculer la largeur des cartes et le nombre de cartes visibles
        function calculateDimensions() {
            // Obtenir la largeur réelle d'une carte incluant les marges
            const firstCard = cards[0];
            const cardStyle = window.getComputedStyle(firstCard);
            cardWidth = firstCard.offsetWidth + 
                        parseInt(cardStyle.marginLeft) + 
                        parseInt(cardStyle.marginRight);
            
            // Calculer combien de cartes peuvent être affichées
            const sliderWidth = slider.offsetWidth - prevBtn.offsetWidth - nextBtn.offsetWidth;
            visibleCards = Math.max(1, Math.floor(sliderWidth / cardWidth));
            
           
            const maxPosition = (cards.length - visibleCards) * cardWidth;
            if (position > maxPosition) {
                position = maxPosition > 0 ? maxPosition : 0;
                updateSliderPosition();
            }
            
           
            updateButtonState();
        }
        
        
        function updateSliderPosition() {
            innerContainer.style.transform = `translateX(-${position}px)`;
        }
        
        // Fonction pour mettre à jour l'état des boutons
        function updateButtonState() {
            prevBtn.style.opacity = position === 0 ? '0.5' : '1';
            prevBtn.style.cursor = position === 0 ? 'default' : 'pointer';
            
            const maxPosition = (cards.length - visibleCards) * cardWidth;
            nextBtn.style.opacity = position >= maxPosition ? '0.5' : '1';
            nextBtn.style.cursor = position >= maxPosition ? 'default' : 'pointer';
        }
        
        // Gestionnaire d'événement pour le bouton précédent
        prevBtn.addEventListener('click', function() {
            if (position > 0) {
                // Décaler d'une carte à la fois
                position = Math.max(0, position - cardWidth);
                updateSliderPosition();
                updateButtonState();
            }
        });
        
        // Gestionnaire d'événement pour le bouton suivant
        nextBtn.addEventListener('click', function() {
            const maxPosition = (cards.length - visibleCards) * cardWidth;
            if (position < maxPosition) {
                // Décaler d'une carte à la fois
                position = Math.min(maxPosition, position + cardWidth);
                updateSliderPosition();
                updateButtonState();
            }
        });
        
        // Recalculer les dimensions lors du redimensionnement de la fenêtre
        window.addEventListener('resize', calculateDimensions);
        
       
        calculateDimensions();
    });
});




