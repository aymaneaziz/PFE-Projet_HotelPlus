<?php 
    require_once("Class.php");
    
    session_start();
    $connexion = mysqli_connect("localhost", "root", "");
    $C = new ChambreManager($connexion);

    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
       
        $filters = [
            'typeH' => $_POST['accommodation'] ?? 'Indifférent',
            'option' => $_POST['option'] ?? [],
            'service' => $_POST['service'] ?? [],
            'prix' => $_POST['prix'] ?? null,
            'nbrstar' => $_POST['nbrstar'] ?? [],
            'typechambre' => $_POST['typechambre'] ?? [],
            'avisnotes' => $_POST['avisnotes'] ?? null
        ];

        $nbrjour = null;
        $destination = null;
        
        if(isset($_SESSION['destination']) && isset($_SESSION['days'])) {
            $nbrjour = $_SESSION['days'];
            $destination = $_SESSION['destination'];
        }
        
        $hotelid = null;
        if(isset($_SESSION['hotelId'])) {
            $hotelid = $_SESSION['hotelId'];
        }

     
        $chambres = $C->getFilteredChambres2($filters, $destination, $hotelid);
        
     
        $hotelName = $_POST['hotelName'] ?? '';
        if (!empty($hotelName)) {
            $chambres = array_filter($chambres, function($chambre) use ($hotelName) {
                return stripos($chambre['nom'], $hotelName) !== false;
            });
        }
        
       
        $sortOption = $_POST['sortOption'] ?? '';
        if (!empty($sortOption)) {
            usort($chambres, function($a, $b) use ($sortOption) {
                switch($sortOption) {
                    case 'Prix (croissant)':
                        return $a['prix'] - $b['prix'];
                    case 'Prix (décroissant)':
                        return $b['prix'] - $a['prix'];
                    case 'Mieux notés':
                        return $b['moyenchambre'] - $a['moyenchambre'];
                    case 'Plus populaires':
                        return $b['nbrAvis'] - $a['nbrAvis'];
                    default:
                        return 0;
                }
            });
        }
        
      
        foreach ($chambres as $index => $chambre) {

                         
            echo '<div class="hotel-card" data-index="' . $index . '">
                    <div class="hotel-image" data-images=\'' . json_encode($chambre["images"]) . '\'>
                        <button class="carousel-button prev-button">❮</button>';


                        if (!empty($chambre["images"])) {
                            echo '<img class="hotel-image-element" src="' . htmlspecialchars($chambre["images"][0]) . '" alt="' . htmlspecialchars($chambre["nom"]) . '">';
                        } else {
                            echo '<img class="hotel-image-element" src="default.jpg" alt="Image non disponible">';
                        }
                     echo '   <button class="carousel-button next-button" id="next-btn">❯</button>
                    </div>
                    <a class="hotel-info" href="Chambre.php?idc='.$chambre["idc"].'">
                        <div class="hotel-header">
                            <div>
                                <p class="hotel-type">' . getHotelType($chambre["typeH"]) . '</p>
                                <h3 class="hotel-title">' . htmlspecialchars($chambre["nom"]) . '</h3>
                                <div class="hotel-stars">' . getStarsHtml($chambre["hotelStar"]) . '</div>
                                <p class="hotel-address">' . htmlspecialchars($chambre["hoteladress"]) . '</p>
                            </div>
                            <div class="hotel-price">';

            if($nbrjour !== null) {
                echo '<p class="price-total">' . $chambre["prix"] * $nbrjour . '€</p>
                      <p class="price-per-night"> '.$chambre["prix"]. '€ par nuit</p>
                      <p class="price-nights">pour '. $nbrjour .' nuits</p>';
            } else {
                echo '<p class="price-total">' . $chambre["prix"] . '€</p>
                      <p class="price-per-night">par nuit</p>';
            }
                          
            echo '</div>
                </div>
                <div class="hotel-features">';
                
            foreach ($chambre["options"] as $options) {
                echo '<span class="feature-tag">' . htmlspecialchars($options) . '</span>';
            }
            
            echo '</div>
                <div class="hotel-services">';
                
            foreach ($chambre["service"] as $service) {
                echo '<span class="service-tag">' . htmlspecialchars($service) . '</span>';
            }
            
            echo '</div>
                <div class="hotel-footer">
                    <span class="room-type">' . getChambrebreType($chambre["typeC"]) . '</span>
                    <span class="hotel-rating ' . getRatingColorClass($chambre["moyenchambre"]) . '">
                        ' . number_format($chambre["moyenchambre"], 1) . ' (' . $chambre["nbrAvis"] . ' avis)
                    </span>
                </div>
                </a>
            </div>';
        }
    }
?>