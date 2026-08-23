<!DOCTYPE html>
<html lang="fr">
<head>

      <?php

       require_once("Class.php");


     /*if (isset($_GET['id'])) {
        $id = $_GET['id'];
        echo "La page est : " . htmlspecialchars($id);
       }*/

    
       $connexion = mysqli_connect("localhost", "root", "");
       if (!$connexion) {
        die("Échec de la connexion : " . mysqli_connect_error());
      }
   
   
       $C = new ChambreManager($connexion);
       


            session_start();

            if (isset($_GET['out']) && $_GET['out'] === 'true') {
                unset($_SESSION['destination']);
                unset($_SESSION['departure_date']);
                unset($_SESSION['return_date']);
                unset($_SESSION['travelers']);
                unset($_SESSION['days']);

              
                
            }

            
            if (isset($_GET['id'])) {
                $_SESSION['hotelId'] = intval($_GET['id']);
            }
            
  
        if ($_SERVER['REQUEST_METHOD'] == 'POST') {

            if (isset($_SESSION['hotelId'])) {
                unset($_SESSION['hotelId']);
                echo '<script>
                    history.pushState(null, "", "index2.php");
                </script>';
            }
            $destination = $_POST['destination'];  
            $departure_date = $_POST['departure_date'];  
            $return_date = $_POST['return_date']; 
            $travelers = $_POST['travelers'];  
        
          
            $departure_timestamp = strtotime($departure_date);  
            $return_timestamp = strtotime($return_date); 
            $diff_in_seconds = $return_timestamp - $departure_timestamp;  
            $days = round($diff_in_seconds / (60 * 60 * 24));  
        
           
            $_SESSION['destination'] = $destination;
            $_SESSION['departure_date'] = $departure_date;
            $_SESSION['return_date'] = $return_date;
            $_SESSION['travelers'] = $travelers;
            $_SESSION['days'] = $days;
        }    

   
    /*
        echo $_SESSION['hotelId'] ;
        echo "Destination: " . htmlspecialchars( $_SESSION['destination']) . "<br>";
        echo "Date de départ: " . htmlspecialchars($_SESSION['departure_date']) . "<br>";
        echo "Date de retour: " . htmlspecialchars( $_SESSION['return_date']) . "<br>";
        echo "Nombre de voyageurs: " . htmlspecialchars(  $_SESSION['travelers']) . "<br>";
        echo "Nombre de jours: " .$_SESSION['days']  . " jours<br>";
*/

       ?>     
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Recherche d'hôtels</title>
    <link rel="stylesheet" href="css/Style-recherche.css"  type="text/css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">

   


</head>
<body >
    <header>
        <div class="header-container">
            <div class="header-top">
                <div class="header-left">
                    
                <a href="index.php">  <img src="icon/HotelPlus.png" alt="Logo HotelPlus" class="hotel-logo"></a>
                    <h1>HotelPlus</h1>
                </div>
            
                <?php if(isset( $_SESSION['Id_Compte'])&& isset( $_SESSION['Username'])){
                 echo' <a href="Logout.php" class="login-btn">
                 
                 '.$_SESSION['Username'].' - Se déconnecter

                 <img style="width :20px; color:withe;" src="icon\Log out.svg">
              </a>';
               
            }else {echo'
                <a href="Login.php?page='.urlencode(string: 'index2.php').'" class="login-btn">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/>
                        <circle cx="12" cy="7" r="4"/>
                    </svg>
                    Se connecter
                </a>'; }?>
            </div>
            
            
            <form class="search-bar" method="POST">

            <div class="search-group">
            <label class="label" >Destination</label>
                <div class="search-input">
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z"/><circle cx="12" cy="10" r="3"/></svg>
                    <input type="text" id="destination" name="destination" class="search-input" placeholder="Où allez-vous ?" required>
                    <div id="suggestions" class="suggestions"></div>
                </div>
                </div>
                

                <div class="search-group">
                <label class="label"> Date de départ </label>
                <div class="search-input ">
                   <input type="date" name="departure_date" id="departure_date" min="<?php echo date('Y-m-d'); ?>" required >
                </div>
                </div>

                <div class="search-group">
                <label class="label">Date de retour </label>
                <div class="search-input ">
                      <input type="date" name="return_date" id="return_date" min="<?php echo date('Y-m-d'); ?>" required>
                </div>
                </div>

                <div class="search-group">
                <label class="label">Nombre de voyageur</label>
                <div class="search-input" required>
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
                    <select name="travelers" style="cursor: pointer;">
                        <option>1 voyageur</option>
                        <option>2 voyageurs</option>
                        <option>3 voyageurs</option>
                        <option>4+ voyageurs</option>
                    </select>
                </div>
                </div>
                <button class="search-btn" >
                    
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
                    Rechercher
                </button>
               
            </form>
        </div>
    </header>

    <main  >
        <aside class="sidebar">


      


            <div class="map-preview">
                <div class="map-container" id="map-container">
                  
                    <div id="hotel-map" style="width:100%; height:100%;"></div>
                </div>

                <button class="map-btn" id="open-map-btn">Afficher en plein écran</button>
            </div>


            <div id="map-modal" class="modal">
                <div class="modal-content">
                    <img src="icon\close-x-svgrepo-com.svg" id="close-map-btn">
                    
                    <!-- GRANDE carte -->
                    <div id="hotel-map-modal" style="width: 100%; height: 100%;"></div>
                </div>
            </div>

            
            <form id="filters-form" method="POST" action="" onsubmit="return false;">
                <div class="filters">
                    
                    <div class="filter-section">
                        <h2>Filtrer par</h2>
                    </div>
                    <div class="filter-section">
                        <h3>Type d'hébergement</h3>
                        <label><input type="radio" name="accommodation" value="Indifférent" checked> Indifférent</label>
                
                        <label><input type="radio" name="accommodation" value="Maisons"> Maisons</label>
                        <label><input type="radio" name="accommodation" value="Business"> Business</label>
                        <label><input type="radio" name="accommodation" value="Luxe"> Deluxe</label>
                        <label><input type="radio" name="accommodation" value="Standard" > Standard</label>
                        <label><input type="radio" name="accommodation" value="Familiale" > Familiale</label>
                    
                        
                    </div>

                    <div class="filter-section">
                        <h3>Option d’hébergement</h3>
                        <div class="filter-section">
                        <label><input type="checkbox" name="option[]" value="Espace extérieur"> Espace extérieur</label>
                        <label><input type="checkbox" name="option[]" value="Vue sur l'océan"> Vue sur l'océan</label>
                        <label><input type="checkbox" name="option[]" value="Vue sur la ville"> Vue sur la ville</label>
                        <label><input type="checkbox" name="option[]" value="Terrasse"> Terrasse</label>
                        <label><input type="checkbox" name="option[]" value="Climatisation"> Climatisation</label>
                        <label><input type="checkbox" name="option[]" value="Chauffage"> Chauffage</label>
                        <label><input type="checkbox" name="option[]" value="Wi-Fi inclus"> Wi-Fi inclus</label>
                        <label><input type="checkbox" name="option[]" value="Cuisine"> Cuisine</label>
                        
                        </div>
                    </div>

                    <div class="filter-section">
                            <h3>Prix par nuit </h3>
                            <input id="price" type="range" name="prix" min="0" max="10000" value="10000" oninput="updatePrice()">
                            
                            <div class="price-range">
                                <span>0€</span>
                                <div class="price-display">
                                    <input type="text" id="price-value" value="10000€" oninput="updateRange()" >
                                </div>
                                <span>10000€</span>
                            </div>
                        </div>
                    <div class="filter-section">
                        <h3>Services et équipements</h3>
                        <label><input type="checkbox"  name="service[]"  value="Restaurant"> Restaurant</label>
                        <label><input type="checkbox" name="service[]"  value="Salle de Sport"> Salle de Sport</label>
                        <label><input type="checkbox" name="service[]"  value="Piscine"> Piscine</label>
                        <label><input type="checkbox" name="service[]"  value="Massages"> Massages</label>
                        <label><input type="checkbox" name="service[]"  value="Parking"> Parking</label>
                    </div>

                    <div class="filter-section">
                        <h3>Nombre d’étoiles</h3>
                        <div class="stars-filter">
                        <label><input type="checkbox" name="nbrstar[]" value="1" > 1★</label>
                        <label><input type="checkbox" name="nbrstar[]" value="2" > 2★</label>
                        <label><input type="checkbox" name="nbrstar[]" value="3" > 3★</label>
                        <label><input type="checkbox" name="nbrstar[]" value="4" > 4★</label>
                        <label><input type="checkbox" name="nbrstar[]" value="5" > 5★</label>
                        </div>
                    </div>
                    <div class="filter-section">
                        <h3>Type de chambre</h3>
                        <label><input type="checkbox" name="typechambre[]" value="Standard" > Standard</label>
                        <label><input type="checkbox" name="typechambre[]" value="Familiale" > Familiale</label>
                        <label><input type="checkbox" name="typechambre[]" value="Suite"> Suite</label>
                        <label><input type="checkbox" name="typechambre[]" value="Deluxe"> Deluxe</label>
                        <label><input type="checkbox" name="typechambre[]" value="VIP"> VIP</label>
                    </div>
                    <div class="filter-section">
                        <h3>Notes des voyageurs</h3>
                        <label><input type="radio" name="avisnotes" value="0"> Toutes les notes</label>
                        <label><input type="radio" name="avisnotes" value="10"> Parfait 10</label>
                        <label><input type="radio" name="avisnotes" value="9"> Merveilleux 9+</label>
                        <label><input type="radio" name="avisnotes" value="8"> Très bien 8+</label>
                        <label><input type="radio" name="avisnotes" value="7"> Bien 7+</label>
                        <label><input type="radio" name="avisnotes" value="6">Acceptable 6+</label>
                    </div>
                
                </div>
           </form> 

          
        </aside>

        <section class="hotels-section">
            <div class="hotels-header">
                <div class="search-input2">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <circle cx="11" cy="11" r="8"></circle>
                        <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                      </svg>
                    <input type="text" placeholder="Nom de l'hébergement">
                </div>
                
                <select class="sort-select">
                    <option>Prix (croissant)</option>
                    <option>Prix (décroissant)</option>
                    <option>Mieux notés</option>
                    <option>Plus populaires</option>
                </select>
            </div>

            <div class="hotels-list" id="hotelsList">
              
              
             
                
          </div>

                
           
        </section>
    </main>

    <footer>
        <div class="footer-container">
            <div class="footer-header">
                <h2>HotelPlus</h2>
                <div class="social-icons">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect width="20" height="20" x="2" y="2" rx="5" ry="5"/><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"/><line x1="17.5" x2="17.51" y1="6.5" y2="6.5"/></svg>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22.54 6.42a2.78 2.78 0 0 0-1.94-2C18.88 4 12 4 12 4s-6.88 0-8.6.46a2.78 2.78 0 0 0-1.94 2A29 29 0 0 0 1 11.75a29 29 0 0 0 .46 5.33A2.78 2.78 0 0 0 3.4 19c1.72.46 8.6.46 8.6.46s6.88 0 8.6-.46a2.78 2.78 0 0 0 1.94-2 29 29 0 0 0 .46-5.25 29 29 0 0 0-.46-5.33z"/><polygon points="9.75 15.02 15.5 11.75 9.75 8.48 9.75 15.02"/></svg>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M16 8a6 6 0 0 1 6 6v7h-4v-7a2 2 0 0 0-2-2 2 2 0 0 0-2 2v7h-4v-7a6 6 0 0 1 6-6z"/><rect width="4" height="12" x="2" y="9"/><circle cx="4" cy="4" r="2"/></svg>
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 4s-.7 2.1-2 3.4c1.6 10-9.4 17.3-18 11.6 2.2.1 4.4-.6 6-2C3 15.5.5 9.6 3 5c2.2 2.6 5.6 4.1 9 4-.9-4.2 4-6.6 7-3.8 1.1 0 3-1.2 3-1.2z"/></svg>
                </div>
            </div>

            <div class="footer-links">
                <div class="footer-section">
                    <h3>Cas d'utilisation</h3>
                    <ul>
                        <li><a href="#">UI design</a></li>
                        <li><a href="#">UX design</a></li>
                        <li><a href="#">Wireframing</a></li>
                        <li><a href="#">Diagramming</a></li>
                        <li><a href="#">Brainstorming</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h3>Explorer</h3>
                    <ul>
                        <li><a href="#">Design</a></li>
                        <li><a href="#">Prototyping</a></li>
                        <li><a href="#">Development</a></li>
                        <li><a href="#">Design systems</a></li>
                        <li><a href="#">Collaboration</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h3>Ressources</h3>
                    <ul>
                        <li><a href="#">Blog</a></li>
                        <li><a href="#">Best practices</a></li>
                        <li><a href="#">Support</a></li>
                        <li><a href="#">Developers</a></li>
                        <li><a href="#">Resource library</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </footer>

    <script src="Script-recherche.js"> </script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCHdbq23tZu_6SObSqRiTdl_4loAoJZA68&callback=initMap"
    async defer></script>
</body>
</html>