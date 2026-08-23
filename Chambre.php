<!DOCTYPE html>
<html lang="fr">

<head>

<?php
       require_once("Class.php");

            
       $connexion = mysqli_connect("localhost", "root", "");
   
 
       session_start();
       
    
       if (isset($_GET['idc'])) {
           $_SESSION['ChambreId'] = intval($_GET['idc']);
       }
       $Chambre = new ChambreManager($connexion);
       $C = $Chambre->getChambreByID(  $_SESSION['ChambreId']); 
   
 
      
     
       
      
       if (!isset($_SESSION['departure_date']) && !isset($_SESSION['return_date'])) {
           $_SESSION['departure_date'] = date('Y-m-d'); // Date actuelle
           $_SESSION['return_date'] = date('Y-m-d', strtotime('+3 day')); // Date 3 jours après
       }
       
       
       $departure_timestamp = strtotime($_SESSION['departure_date']);
       $return_timestamp = strtotime($_SESSION['return_date']);
       $diff_in_seconds = $return_timestamp - $departure_timestamp;
       $days = round($diff_in_seconds / (60 * 60 * 24)); 
       $_SESSION['days'] = $days;
       
    
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
           
          
           $_SESSION['destination'] = $destination;
           $_SESSION['departure_date'] = $departure_date;
           $_SESSION['return_date'] = $return_date;
           $_SESSION['travelers'] = $travelers;
       
       
           $departure_timestamp = strtotime($departure_date);  
           $return_timestamp = strtotime($return_date); 
           $diff_in_seconds = $return_timestamp - $departure_timestamp;  
           $days = round($diff_in_seconds / (60 * 60 * 24));  
           $_SESSION['days'] = $days;
       
           
           header("Location: index2.php");
           exit();
       }
       
     
       if ($_SERVER['REQUEST_METHOD'] == 'GET') {
           if (isset($_GET['departure_date']) && !empty($_GET['departure_date'])) {
               $_SESSION['departure_date'] = $_GET['departure_date'];
           }
       
           if (isset($_GET['return_date']) && !empty($_GET['return_date'])) {
               $_SESSION['return_date'] = $_GET['return_date'];
           }
       
    
           if (isset($_GET['departure_date']) && isset($_GET['return_date'])) {
               $departure_timestamp = strtotime($_GET['departure_date']);
               $return_timestamp = strtotime($_GET['return_date']);
               $diff_in_seconds = $return_timestamp - $departure_timestamp;
               $days = round($diff_in_seconds / (60 * 60 * 24));
               $_SESSION['days'] = $days;
           }
       }
       ?>
       

    



    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Réservation de Chambre</title>
    <link rel="stylesheet" href="css/style-chambre.css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
</head>

<body>
    <!-- En-tête -->
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
                <a href="Login.php?page='.urlencode(string: 'Chambre.php').'?idc='.$C['idc'].'" class="login-btn">
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


    <!-- Galerie d'images-------------------------------------------------------------->
    
    
        
    </div>
    <div class="hotel-slider">
    <button id="prev-btn" class="arrow left-arrow">❮</button>

    <div class="hotel-images-container">
  <div class="hotel-image image-main">
    <img class="img-chambre" id="main-img" src="<?php echo $C['images'][0]; ?>" alt="Image principale">
  </div>
  <div class="secondary-container">
    <?php for ($i = 1; $i < count($C['images']); $i++) { ?>
      <div class="hotel-image secondary">
        <img class="img-chambre" src="<?php echo $C['images'][$i]; ?>" alt="Image secondaire">
      </div>
    <?php } ?>
  </div>
</div>


    <button id="next-btn" class="arrow right-arrow">❯</button>
</div>
<script>
    const images = <?php echo json_encode($C['images']); ?>;
</script>


    
    
    

    <!-- Section des prix sous les images -->
    <section class="price-section">
        <div class="price-box">
            <?php echo'<h2 style="text-align: center;"> '.$C["nom"].'</h2>
            <div class="price-details">
                <div class="price-details1">
                
                <div class="hotel-stars">'.getStarsHtml($C["hotelStar"]).'</div>
                <p > '.getHotelType($C['typeH']).'</p>
                <p class="adress"> '.$C['hoteladress'].'</p>
                <p ><strong>Etat de chambre :</strong> '.Ratingtostatus($C['moyenchambre']).'</p>
                <p>'.getChambrebreType($C['typeC']).'</p>


                </div>
       
                <!-- À propos de cet hébergement -->
                <div class="about-accommodation">
                    <h3>À propos de cet hébergement</h3>
                  <div class="options-grid">';
                  foreach($C['options'] as $op){
                             echo'<div class="option-item">'.$op.'</div> ' ;
    
                  }
                         
                       
              echo'        </div>
                </div>
            </div>
            <section class="hotel-info">
                <div class="rating">
                         <span class="score '.getRatingColorClass($C['moyenchambre']).'"> '.number_format($C["moyenchambre"], 1) .' ('.$C['nbrAvis'].' avis)</span>
                </div>
              
            </section> 
        </div>
    
        <div class="price-box">
            <h2>Description</h2>
            <div class="description">';

                if($C['typeoffre'] == "Speciale"){

                  
                   echo'  <p>'.$C['description'].' <br><Strong> Offres spéciales : </Strong> '.$C['speciale'].' </p>';

                }else{
                    
                    echo'   <p>'.$C['description'].'</p>';
                }



          echo' </div>
           </div>';
        
        ?>
        <div class="price-box total-price-box">
            <h2 style="text-align: center;">Tarif d'hébergement</h2>
            <?php echo' <div class="price-details">
                <div class="price-item">
                    <span>Prix de chambre</span>
                    <span id="priceC" value ="'.floatval($C['prix'])* $_SESSION['days'].'"> 
                   '.
                     number_format(floatval($C['prix'])* $_SESSION['days'], 2).'€</span> <!-- Price from data -->
                </div>
                
                <div id="selected-services">
                   <!-- service selctioné -->

                </div>
             
                <div class="price-item total">
                    <span>Prix total</span>
                    <span id="total-price" ></span> <!-- Total price from data -->
                </div>
                <small class="nbr-night">pour '. $_SESSION['days'].' nuits</small>
                <small class="price-night" > '.floatval($C['prix']).'€ par nuit</small>
                <small>taxes et frais compris</small>
                
            </div>';

              









          if(isset( $_SESSION['Id_Compte'])&& isset( $_SESSION['Username'])){
                 echo'  <div class="reservation">
                 <a href="Payement.php" class="reservation-btn">Réserver</a>
            
                </div>';

                
               
            }else {echo'
                <div class="reservation">
                <a href="Login.php?page='.urlencode(string: 'Chambre.php').'?idc='.$C['idc'].'" class="reservation-btn">Réserver</a>
               </div>
              
                 '; }?>
               
               <form method="GET">
                    <div class="search-group">
                        <label class="label">Date de départ</label>
                        <div class="search-input">
                            <input type="date" name="departure_date" id="departure_date" 
                                min="<?php echo date('Y-m-d'); ?>"
                                value="<?php echo isset($_SESSION['departure_date']) ? $_SESSION['departure_date'] : date('Y-m-d'); ?>" 
                                required>
                        </div>
                    </div>

                    <div class="search-group">
                        <label class="label">Date de retour</label>
                        <div class="search-input">
                            <input type="date" name="return_date" id="return_date" 
                                min="<?php echo date('Y-m-d'); ?>"
                                value="<?php echo isset($_SESSION['return_date']) ? $_SESSION['return_date'] : date('Y-m-d'); ?>"
                                required>
                        </div>
                    </div>

    <button   class="update-date"   type="submit">Mettre à jour la date </button>
</form>

        </div>
    </section>
    

    <!-- Détails de l'hôtel -->
    <main class="container">
       

       
        <section class="service-checkboxes ">
            <h3>Inscrivez-vous à notre service</h3>
            <div class="checkboxes-container services-container">

            <?php
        foreach ($C['services_prices'] as $service => $price) {
            echo '<label class="checkbox-label">
                <input type="checkbox" class="service-checkbox" name="'.$service.'" value="' . $service . '" data-price="' . $price . '">
                <span>'  . $service . ' - ' . $price . '€</span>
            </label>';
        }
    ?>
            </div>
        </section>


  
        <section class="nos-services">
            <h3>Nos services</h3>
            <div class="services-images-container">


                <?php
                // Mélanger les images dans un ordre aléatoire
            shuffle($C["image_service"]);

            
            $images_to_display = array_slice($C["image_service"], 0, 5);
            
            $first = true; 
            foreach ($images_to_display as $s) {
                $class = $first ? "service-main" : "service-secondary-1";
                $first = false;
                echo '
                <div class="service-image ' . $class . '">
                    <img src="' . htmlspecialchars($s, ENT_QUOTES, 'UTF-8') . '" alt="Service Image">
                </div>';
            }
                ?>
            </div>
        </section>
      


        <section class="other-rooms">
            <h3>Découvrez d'autres chambres de cet hôtel</h3>
            <div class="rooms-container">
                


                <?php 
                    $Chambre = new ChambreManager($connexion);
                    $Cext = $Chambre->getChambreEXT($C['idc'], $C['idHotel']);

                    
                    $Cext = array_slice($Cext, 0, 6);

                    foreach ($Cext as $cimg) {
                        echo '<a href="Chambre.php?idc=' . $cimg['idc'] . '" class="room-card">
                                <img class="room-image" src="' . htmlspecialchars($cimg['images'][0], ENT_QUOTES, 'UTF-8') . '">
                                <div class="room-details">
                                    <p class="room-type">' . getChambrebreType($cimg['typeC']) . '</p>
                                    <div class="price-container">
                                        <span class="price-label">Prix par nuit</span>
                                        <span class="price-value">' . number_format($cimg['prix'], 2) . ' €</span>
                                    </div>
                                </div>
                            </a>';
                    }
                    ?>



                
                
            </div>
        </section>


        <section class="review-section">
            <h3>Dernière évaluation</h3>
            <div class="review-cards-container">
            <?php    
                $cnnx = mysqli_connect("localhost", "root", "");
                $cnx = mysqli_connect("localhost", "root", "", "hotel");      
                $Avis = new AvisManager($cnx);
                $A = $Avis->getAvis();
                $user = new ClientManager($cnnx);

                // Filtrer et trier les avis par date décroissante
                $avisFiltres = array_filter($A, function($a) use ($C) {
                    return $C['idc'] == $a->getIdChambre();
                });

                // Trier par date décroissante
                usort($avisFiltres, function($a, $b) {
                    return strtotime($b->getDate()) - strtotime($a->getDate());
                });

                
                $avisFiltres = array_slice($avisFiltres, 0, 6);

                foreach ($avisFiltres as $a) {
                    $username = $user->getUsernameByIdClient($a->getIdClient());
                    $dateReview = strtotime($a->getDate());
                    $dateAffichage = date("d/m/Y H:i", $dateReview);

                   
                    $timeDiff = time() - $dateReview;
                    if ($timeDiff < 86400) { // 86400 secondes = 24h
                        if ($timeDiff < 3600) {
                            $timeAgo = floor($timeDiff / 60) . " minutes";
                        } else {
                            $timeAgo = floor($timeDiff / 3600) . " heures";
                        }
                        $dateAffichage = "Il y a " . $timeAgo;
                    }

                    echo '<div class="review-card">
                            <div class="rating">
                                <span class="score ' . getRatingColorClass($a->getNbrsur10()) . '">
                                    ' . number_format($a->getNbrsur10(), 1) . '
                                </span>
                            </div>
                            <div class="review-body">
                                <p>' . Ratingtostatus($a->getNbrsur10()) . '</p>
                                <p>' . htmlspecialchars($a->getDescription(), ENT_QUOTES, 'UTF-8') . '</p>
                            </div>
                            <div class="reviewer-info">
                                <span>' . $username . '</span>
                                <span>' . $dateAffichage . '</span>
                            </div>
                        </div>';
                }
                ?>

                
         
                
          
            </div>
        </section>


        <section class="add-review-section">
    <h3>Donnez votre avis</h3>
    <div class="add-review-form">
    <textarea id="review-text" placeholder="Votre avis"></textarea>
    <div class="rating-scale">
        <?php for ($i = 1; $i <= 10; $i++): ?>
            <input type="radio" id="rating<?= $i ?>" name="rating" value="<?= $i ?>">
            <label for="rating<?= $i ?>"><?= $i ?>.0</label>
        <?php endfor; ?>
    </div>
    <p id="error-message"></p>
    <?php if (isset($_SESSION['Id_Compte']) && isset($_SESSION['Username'])): ?>
        <button class="submit-review" onclick="submitReview();">Envoyer l'avis</button>
    <?php else: ?>
        <a href="Login.php?page=Chambre.php?idc=<?= htmlspecialchars( $_SESSION['ChambreId'], ENT_QUOTES, 'UTF-8'); ?>" class="submit-review">
            Se connecter pour envoyer l'avis
        </a>
    <?php endif; ?>

  
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

        
       

   

    <script src="Script-chambre.js"></script>
</body>

</html>