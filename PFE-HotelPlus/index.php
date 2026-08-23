<!DOCTYPE html>
<html lang="fr">
<head>
    <?php
    require_once("Class.php");
    $connexion = mysqli_connect("localhost", "root", "");
    $E = new EntrepriseManager($connexion);

    $H = new HotelManager($connexion);
    $hotels = $H->getHotels();  //tableau des hotel

    $E = new EntrepriseManager($connexion);
    $entreprise = $E->getEntreprises();

    $C = new ChambreManager($connexion);
    $chambres = $C->getChambres(); //tableau des chambres

    
    $Ih = new ImageManagerH($connexion);
    $Hotel_Image = $Ih->getImages();//tableau des image de hotel

  
    $Ic = new ImageManagerC($connexion); 
    $chambre_Image = $Ic->getImages(); // Tableau des images de chambres

   
 
    session_start();

  



    
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
    

       
        header("Location: index2.php");
        exit();
    }

    
//------------------





    ?>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="css/style.css" type="text/css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
    <title>HotelPlus - Accueil</title>
</head>
<body>
  
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
                <a href="Login.php?page='.urlencode(string: 'index.php').'" class="login-btn">
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
                <button class="search-btn"  >
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.3-4.3"/></svg>
                    Rechercher
                </button>
               
            </form>
        </div>
    </header>


   
  

    <!-- Promo Section -->
    <section class="promo-section">
        <div class="promo-header">
            <h2>Trouvez et réservez le séjour idéal</h2>
        </div>
        <div class="promo-cards">
            <div class="promo-card">
                <i class="fas fa-star"></i>
                <p>Réservez votre séjour parfait dès maintenant </p>
            </div>
            <div class="promo-card">
                <i class="fas fa-tag"></i>
                <p>Profitez de plus d'économies avec les Prix membres</p>
            </div>
            <div class="promo-card">
                <i class="fas fa-calendar-check"></i>
                <p>Options d'annulation gratuite en cas de changement de programme</p>
            </div>
        </div>
    </section>

    <!-- Featured Hotels Section -->
    <!-- 2. Modify the first hotel section structure -->
<section class="hotel-section">
  <h2>Découvrez votre nouvel hébergement préféré</h2>
  
  <div class="hotel-slider">
    <button id="hotels-prev-btn" class="arrow left-arrow">❮</button>
    <div class="slider-container" id="hotels-slider">
      <?php
       $hotelImages = []; 

      foreach ($Hotel_Image as $image) {
           $hotelId = $image->getIdHotel();
  
         
       if (!isset($hotelImages[$hotelId])) {
           $hotelImages[$hotelId] = $image;
          }
       }

        foreach ($hotels as $hotel) {
         $hotelId = $hotel->getId();

              if (isset($hotelImages[$hotelId])) {
                   $image = $hotelImages[$hotelId];
                   echo '<a href="index2.php?id='.urlencode($hotelId ).'&out=true" class="hotel-card">
                        <img src="'.$image->getUrl().'" alt="'.$hotel->getNom().'">
                           <div class="hotel-info">
                                <h4>'.$hotel->getNom().' <span id="hotel-star" 
                                
                                ">'.getStarsHtml($hotel->getNbrStar()) .'</span></h4>
                           </div>
                          </a>';
              }
          }
      ?>  
    </div>
    <button id="hotels-next-btn" class="arrow right-arrow">❯</button>
  </div>
</section>

<!-- 3. Modify the offers section structure -->
<section class="hotel-section offers-section">
  <h2>Offres hôtelières exceptionnelles</h2>
  <div class="hotel-slider">
    <button id="offers-prev-btn" class="arrow left-arrow">❮</button>
    <div class="slider-container" id="offers-slider">
      <?php
        // 1. Associer chaque chambre à son type promotion
        $chambresPromo = $C->getChambreTypePromo();

        // 2. Associer chaque chambre à son hôtel et son prix
        $chambresData = $C->getChambreData();

        // 3. Associer chaque hôtel à son nom
        $hotelsData = $H->getHotelsNom();

        // 4. Afficher une seule image par chambre qui a une offre "Promotion"
        $afficheImages = [];

        foreach ($chambre_Image as $C_images) {
            $idChambre = $C_images->getIdChambre();
            
            if (isset($chambresPromo[$idChambre]) && !isset($afficheImages[$idChambre])) {
                $afficheImages[$idChambre] = true;

                $promotion = $chambresPromo[$idChambre]['promotion'];
                $prix = isset($chambresData[$idChambre]) ? $chambresData[$idChambre]['prix'] : 'Non spécifié';
                $idHotel = isset($chambresData[$idChambre]) ? $chambresData[$idChambre]['idHotel'] : null;
                $nomHotel = isset($hotelsData[$idHotel]) ? $hotelsData[$idHotel] : 'Non spécifié';

                echo '<a href="Chambre.php?idc=' . $idChambre .'" class="hotel-card">
                        <img src="'.$C_images->getUrl().'" >
                            <div class="hotel-info">
                                <div>
                                    <Strong>'.$nomHotel.'</Strong>
                                    <div class="discount-badge"> -'.$promotion.'%</div>
                                </div>
                        
                                <div >
                                    Prix par nuit
                                    <span class="price">
                                    '.calcprixpromo($prix,$promotion).'€ 
                                    </span>
                                    <span class="promotion">
                                       '.$prix.'€
                                    </span>
                                </div>
                         
                            </div>
                        </a>';
                     
            }
        }
      ?>
    </div>
    <button id="offers-next-btn" class="arrow right-arrow">❯</button>
  </div>
</section>

<!-- 4. Modify the special packages section -->
<section class="hotel-section packages-section">
  <h2>Offres spéciales</h2>
  <div class="hotel-slider">
    <button id="packages-prev-btn" class="arrow left-arrow">❮</button>
    <div class="slider-container" id="packages-slider">
      <?php
        // 1. Associer chaque chambre à son type promotion
        $chambresSpec = $C->getChambreTypeSpec();

        // 2. Associer chaque chambre à son hôtel et son prix
        $chambresData = $C->getChambreData();

        // 3. Associer chaque hôtel à son nom
        $hotelsData = $H->getHotelsNom();

        // 4. Afficher une seule image par chambre qui a une offre "speciale"
        $afficheImages = [];

        foreach ($chambre_Image as $C_images) {
            $idChambre = $C_images->getIdChambre();
            
            if (isset($chambresSpec[$idChambre]) && !isset($afficheImages[$idChambre])) {
                $afficheImages[$idChambre] = true;

                $Speciale = $chambresSpec[$idChambre]['speciale'];
                $prix = isset($chambresData[$idChambre]) ? $chambresData[$idChambre]['prix'] : 'Non spécifié';
                $idHotel = isset($chambresData[$idChambre]) ? $chambresData[$idChambre]['idHotel'] : null;
                $nomHotel = isset($hotelsData[$idHotel]) ? $hotelsData[$idHotel] : 'Non spécifié';

                echo '<a  href="Chambre.php?idc=' . $idChambre .'" class="package-card">
                        <img src="'.$C_images->getUrl().'" alt="Offre spéciale" >
                            <div class ="package-info">
                                <p>
                                    <Strong>'.$nomHotel.'</Strong>
                                    <div class="speciale">'.$Speciale.'</div>
                                   <strong>Prix par nuit </strong> <span class="price1">'.$prix.'€ </span>
                                      
                                </p>
                            </div>
                        </a>';
            }
        }
      ?>
    </div>
    <button id="packages-next-btn" class="arrow right-arrow">❯</button>
  </div>
</section>

    <!-- Rewards Banner -->
    <section class="rewards-banner">
    <button class="rewards-btn" onclick="toggleHotelList()">Découvrir plus</button>
</section>

<!-- Bloc caché avec la liste des hôtels -->
<div id="hotel-list" class="hotel-list">
    
    <ul>
        <?php                  
        foreach($entreprise as $e){
            echo '<li>
                <div class="hotel-info">
                    <span class="hotel-name">Entreprise - '.$e->getNom().'</span>
                   
                </div>
                <div class="hotel-logo2">
                    <img src="'.$e->getLogo().'" alt="Logo '.$e->getNom().'">
                </div>
            </li>';
        }
        ?>
    </ul>
</div>

    <!-- Footer -->
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

    <script src="script.js"></script>
</body>
</html>