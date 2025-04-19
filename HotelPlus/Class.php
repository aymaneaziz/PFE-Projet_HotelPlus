<?php


//------------------------------------------------------Entreprise-----------------------------------------------------------------
class Entreprise {
    private $id_entreprise;
    private $nom_entreprise;
    private $Logo;

    public function __construct($id, $nom,$Logo) {
        $this->id_entreprise = $id;
        $this->nom_entreprise = htmlspecialchars($nom, ENT_QUOTES, 'UTF-8');
        $this->Logo=htmlspecialchars($Logo, ENT_QUOTES, 'UTF-8');;
    }

    public function getId() { return $this->id_entreprise; }
    public function getNom() { return $this->nom_entreprise; }
    public function getLogo() { return $this->Logo;}
}
class EntrepriseManager {
    private $entreprises = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM Entreprise");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->entreprises[] = new Entreprise($ligne->ID_Entreprise, $ligne->Nom_Entreprise
                , $ligne->Logo);
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getEntreprises() { return $this->entreprises; }
}


//------------------------------------------------------Service-----------------------------------------------------------------

class Service{
    private $ID_service , $ID_hotel , $Nom_service ,$Description_service ,$Prix;

    public function __construct($ID_service , $ID_hotel , $Nom_service ,$Description_service ,$Prix){

        $this->ID_service = $ID_service;
        $this->ID_hotel = $ID_hotel;
        $this->Nom_service =htmlspecialchars($Nom_service , ENT_QUOTES, 'UTF-8');
        $this->Description_service = htmlspecialchars($Description_service , ENT_QUOTES, 'UTF-8');
        $this->Prix = $Prix;
        
    }  
    public function getId() { return $this->ID_service; }
    public function getIdHotel() {return $this->ID_hotel;}
    public function getNomService() {return $this->Nom_service;}
    public function getDescService() {return $this->Description_service;}
    public function getPrixService() {return $this->Prix;}


}

class ServiceManager{
    private $services = [];
      
    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM Services");
        if ($stmt) {
           
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->services[] = new Service(
                    $ligne->ID_Service, 
                $ligne->ID_Hotel,
                 $ligne->Nom_Service,
                $ligne->Description_Service, 
                $ligne->Prix,
              
                );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    } 

    public function getServices(): array {return $this->services; }



}

//------------------------------------------------------Hotel-----------------------------------------------------------------
class Hotel {
    private $id_hotel;
    private $id_entreprise;
    private $nom_hotel;
    private $pays;
    private $ville;
    private $adresse;
    private $nbr_chambre;
    private $nbr_etoile;
    private $type_hotel;
    private $latitude ;
    private $longitude ;

    public function __construct($id_h, $id_e, $nom, $pays, $ville, $adresse, $nbr_c, $nbr_e, $type,$latitude,$longitude) {
        $this->id_hotel = $id_h;
        $this->id_entreprise = $id_e;
        $this->nom_hotel = htmlspecialchars($nom, ENT_QUOTES, 'UTF-8');
        $this->pays = htmlspecialchars($pays, ENT_QUOTES, 'UTF-8');
        $this->ville = htmlspecialchars($ville, ENT_QUOTES, 'UTF-8');
        $this->adresse = htmlspecialchars($adresse, ENT_QUOTES, 'UTF-8');
        $this->nbr_chambre = $nbr_c;
        $this->nbr_etoile = $nbr_e;
        $this->type_hotel = $type;
        $this->latitude =$latitude;
        $this->longitude =$longitude;
    }

    public function getId() { return $this->id_hotel; }
    public function getNom() { return $this->nom_hotel; }
    public function getNbrStar() { return $this->nbr_etoile; }
    public function getType() { return $this->type_hotel; }
    public function getPays() { return $this->pays; }
    public function getHotelAdrs() { return $this->adresse; }

    public function getVille() { return $this->ville; }
    public function getLatitude() { return $this->latitude; }
    public function getLongitude() { return $this->longitude; }
}
class HotelManager {
    private $hotels = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM Hotel");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->hotels[] = new Hotel(
                    $ligne->ID_Hotel,
                     $ligne->ID_Entreprise,
                      $ligne->Nom_Hotel, 
                      $ligne->Pays, 
                      $ligne->Ville, 
                      $ligne->Adresse_Hotel,
                       $ligne->Nbr_Chambre, 
                       $ligne->Nbr_Etoile,
                        $ligne->Type_Hotel,
                        $ligne->Latitude,
                        $ligne->Longitude
                    
                    );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getHotels() { return $this->hotels; }

    public function getHotelsNom() { 
        
        $hotelsData = [];
        foreach ($this->hotels as $hotel) {
            $hotelsData[$hotel->getId()] = $hotel->getNom();
        }
        return $hotelsData;
        }
}

//------------------------------------------------------Chambre-----------------------------------------------------------------

class Chambre {
    private $id_chambre  ;
    private $id_hotel;
    private $num_chambre;
    private $type;
    private $prix;
    private $statut;
    private $description;
    private $type_offre ;
    private $speciale ;
    private $promotion ;
  
    public function __construct($id, $id_h, $num, $type, $prix, $statut, $description,$type_offre,$speciale,$promotion) {
        $this->id_chambre = $id;
        $this->id_hotel = $id_h;
        $this->num_chambre = $num;
        $this->type = $type;
        $this->prix = $prix;
        $this->statut = $statut;
        $this->description = htmlspecialchars($description, ENT_QUOTES, 'UTF-8');
        $this->type_offre = $type_offre;
        $this->speciale = $speciale;
        $this->promotion =$promotion;
       
    }

    public function getId() { return $this->id_chambre; }
   public function getIdHotel() {return $this->id_hotel;}
    public function getNum() { return $this->num_chambre; }
    public function getType() { return $this->type; }
    public function getPrix() {return $this->prix;}
    public function getStatut(){   return $this->statut; }
    public function getDescription(){   return $this->description; }
    public function getTypeOffre() { return $this->type_offre; }
   public function getSpeciale() { return $this->speciale; }
   public function getPromotion() { return $this->promotion; }

  

    
}


class ChambreManager {
    private $chambres = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM chambre");
        if ($stmt) {
           
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->chambres[] = new Chambre(
                    $ligne->ID_Chambre, 
                $ligne->ID_Hotel,
                 $ligne->Num_Chambre,
                $ligne->Type, 
                $ligne->Prix,
                 $ligne->Statut_Chambre,
                $ligne->Description_Chambre,
                $ligne->Type_Offre,
                $ligne->Speciale,
                $ligne->Promotion
               
                );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getChambres() { return $this->chambres; }

    public function getChambreTypePromo() {
        $chambresPromo = [];
        foreach ($this->chambres as $c) {
            if ($c->getTypeOffre() == "Promotion" && $c->getStatut() =="Libre") {
                $chambresPromo[$c->getId()] = [
                    'promotion' => $c->getPromotion()
                ];
            }
        }
        return $chambresPromo;
    }
    public function getChambreTypeSpec(){
        $chambresSpec = [];
        foreach ($this->chambres as $c) {
            if ($c->getTypeOffre() == "Speciale" && $c->getStatut() =="Libre") {
                $chambresSpec[$c->getId()] = [
                    'speciale' => $c->getSpeciale()
                ];
            }
        }
        return $chambresSpec;

    }
    public function getChambreData() {
        $chambresData = [];
        foreach ($this->chambres as $c) {
            $chambresData[$c->getId()] = [
                'prix' => $c->getPrix(),
                'idHotel' => $c->getIdHotel()
            ];
        }
        return $chambresData;
    }
    public function getChambreAllData($destination=null,$hotelid=null) {

        $chambresData = [];

        $connexion = mysqli_connect("localhost", "root", "");
        
        // Récupération des données
        $imagechambre = new ImageManagerC($connexion);
        $ImageC = $imagechambre->getImages();

        $imageservice = new ImageManagerS($connexion);
        $ImageS = $imageservice->getImages();
        
        $optionchambre = new OptionManager($connexion);
        $OptionC = $optionchambre->getOptions();
        
        $avischambre = new AvisManager($connexion);
        $AvisC = $avischambre->getAvis();
        
        $hotelchambre = new HotelManager($connexion);
        $HotelsC = $hotelchambre->getHotels();

        $servicehotel = new ServiceManager($connexion);
       $ServicesC = $servicehotel->getServices(); // Tableau d'objets Services
        
        foreach ($this->chambres as $c) {

            if ($c->getStatut() !== "Libre") {
                continue; 
            }
            $images = [];
            foreach ($ImageC as $i) {
                if ($c->getId() == $i->getIdChambre()) {
                    $images[] = $i->getUrl();
                }
            }
        
            
            $options = [];
            foreach ($OptionC as $o) {
                if ($c->getId() == $o->getIdChambre()) {
                    $options[] = $o->getOptionChambre();
                }
            }

            $imageservices= [];
           foreach ($ServicesC as $s) {
              if ($s->getIdHotel() == $c->getIdHotel()) {
                     foreach($ImageS as $img){
                          if ( $s->getID() == $img->getIdService() ){
                            $imageservices []= $img->getUrl() ;
                          }

                     }

                 
              }

            }

        
            // Tableau des services avec leurs prix
            $services_prices = [];

            $services = [];
            foreach ($ServicesC as $s) {
                if ($c->getIdHotel() == $s->getIdHotel()) {
                    $services[] = $s->getNomService();
                   
                    $servicePrice = $s-> getPrixService();
            
                    
                    $services_prices[$s->getNomService()] = $servicePrice;
                }
            }

            
            $hoteType = null;
            $hotelNom = null;
            $hotelStar = null;
            $hoteladress =null;
            $Latitude = null;
            $Longitude = null;

            foreach ($HotelsC as $h) {
                if ($c->getIdHotel() == $h->getId() && ($hotelid ==$c->getIdHotel() || $hotelid==null) && ($destination === null || 
                strtolower(trim($h->getVille())) == strtolower(trim($destination)) 
                 )) { 
                    $hotelStar = $h->getNbrStar() ;
                    $hotelNom = $h->getNom() ;
                    $hoteType = $h->getType() ;
                    $hoteladress = $h->getHotelAdrs() ;
                    $Latitude = $h->getLatitude();
                    $Longitude = $h->getLongitude();
                    break;
                }
            }
                    
            
            $avisTotal = 0;
            $nbAvis = 0;
        
            foreach ($AvisC as $a) {
                if ($c->getId() == $a->getIdChambre()) {
                    $avisTotal += $a->getNbrsur10();
                    $nbAvis++;
                }
            }
        
            // Calcul de la moyenne sur 10
            $moyenneChambre = ($nbAvis > 0) ? $avisTotal / $nbAvis : 0;
        
            $prix=$c->getPrix();
            if($c->getPromotion() !== null){

                $prix = calcprixpromo($c->getPrix(),$c->getPromotion());

            }

           // Remplissage des données de la chambre
            $chambresData[$c->getId()] = [
                'idHotel' => $c->getIdHotel(),
                'idc'=>$c->getId(),
                'typeH' => $hoteType ,
                'nom' => $hotelNom ,
                'hotelStar' => $hotelStar, 
                'hoteladress' => $hoteladress,
                'images' => $images,
                'service' =>$services ,
                'options' => $options,
                'nbrAvis' => $nbAvis,
                'moyenchambre' => $moyenneChambre,
              
                'num_chambre' => $c->getNum(),
                'typeC' => $c->getType(),
                'prix' => $prix ,
                'statut' => $c->getStatut(),
                'description' => $c->getDescription(),
                'typeoffre' => $c->getTypeOffre(),
                'speciale' => $c->getSpeciale(),
                'promotion' => $c->getPromotion(),
                "location" => [
                    "lat" => $Latitude,
                    "lng" =>  $Longitude
                ],
                'services_prices' =>$services_prices ,
                'image_service'=> $imageservices

            ];
        }
        
        return $chambresData;
        
}      


  public function getChambreEXT($ID_Chmabre,$ID_Hotel){
    $Chambre= $this->getChambreAllData();
    $Chambrebdecouver=[];
         foreach( $Chambre as $C){
                 if($C['idHotel'] ==$ID_Hotel && $C['idc'] != $ID_Chmabre  ){

                    $Chambrebdecouver[] = $C;

                 }
                   
         }
         return $Chambrebdecouver; 

  }



   public function getChambreByID($IDChmabre){
    $Chambreext = $this->getChambreAllData();
    $chambrebyid=[];
         foreach($Chambreext as $c){
           if($c["idc"] == $IDChmabre){

            $chambrebyid = $c;

           }



         }
         return $chambrebyid;
   }



    public function getFilteredChambres2($filters,$destination=null,$hotelid=null) {
        $allChambres = $this->getChambreAllData($destination,$hotelid);

        
        $filteredChambres = array_filter($allChambres, function ($chambre) use ($filters) {
            // Filtrer par type d'hôtel
            if ($filters['typeH'] !== 'Indifférent' && $filters['typeH'] !== null && $chambre['typeH'] !== $filters['typeH']) {
                return false;
            }

            // Filtrer par options
            if (!empty($filters['option']) && !array_intersect($filters['option'], $chambre['options'])) {
                return false;
            }

            // Filtrer par services
            if (!empty($filters['service']) && !array_intersect($filters['service'], $chambre['service'])) {
                return false;
            }

            // Filtrer par prix
            if ($filters['prix'] !== null && $chambre['prix'] > $filters['prix']) {
                return false;
            }

           if (!empty($filters['nbrstar']) && (!isset($chambre['hotelStar']) || !array_intersect($filters['nbrstar'], (array) $chambre['hotelStar']))) {
                return false;
            }
    

            // Filtrer par type de chambre
            if (!empty($filters['typechambre']) && !in_array($chambre['typeC'], $filters['typechambre'])) {
                return false;
            }

            // Filtrer par avis
            if ($filters['avisnotes']  !== null && $chambre['moyenchambre'] < $filters['avisnotes']) {
                return false;
            }

            if ($chambre['nom'] == null) {
                return false;
            }

            return true;
        });

        return $filteredChambres;
    }



   /*  public function setChambre($filters,$nbrjour=null,$destination=null, $hotelid=null){
       
          $Chambres = $this->getFilteredChambres2($filters,$destination,$hotelid);
          
        foreach ($Chambres as $index => $chambre) {
            echo '<div class="hotel-card"  data-index="' . $index . '">
                    <div class="hotel-image"  data-images=\'' . json_encode($chambre["images"]) . '\'>
                             
                        <button class="carousel-button prev-button">❮</button>
                        <img class="hotel-image-element" src="' . htmlspecialchars($chambre["images"][0]) . '" alt="' . htmlspecialchars($chambre["nom"]) . '">
                        <button class="carousel-button next-button" id="next-btn">❯</button>
                    </div>
                    <a class="hotel-info"   href="Chambre.php?idc='.$chambre["idc"].'";>
                        <div class="hotel-header">
                            <div>
                                <p class="hotel-type">' .getHotelType($chambre["typeH"]) . '</p>
                                <h3 class="hotel-title">' . htmlspecialchars($chambre["nom"]) . '</h3>
                                <div class="hotel-stars">' . getStarsHtml($chambre["hotelStar"]) . '</div>
                                <p class="hotel-address">' . htmlspecialchars($chambre["hoteladress"]) . '</p>
                            </div>
                            <div class="hotel-price">';

                               if($nbrjour!==null){
                               echo' <p class="price-total">' . $chambre["prix"] * $nbrjour . '€</p>
                                <p class="price-per-night"> '.$chambre["prix"]. '€ par nuit</p>
                                <p class="price-nights">pour '. $nbrjour .' nuits</p>';
                            } else{
                                  echo' <p class="price-total">' . $chambre["prix"] . '€</p>
                                <p class="price-per-night">  par nuit</p>';
 
                            }
                          echo'  
                            </div>
                        </div>
                        <div class="hotel-features">';
                            foreach ($chambre["options"] as $options) {
                                echo '<span class="feature-tag">' . htmlspecialchars($options) . '</span>';
                            }
            echo       '</div>
                        <div class="hotel-services">';
                            foreach ($chambre["service"] as $service) {
                                echo '<span class="service-tag">' . htmlspecialchars($service) . '</span>';
                            }
            echo       '</div>
                        <div class="hotel-footer">
                            <span class="room-type">' . getChambrebreType($chambre["typeC"]) . '</span>
                            <span class="hotel-rating ' . getRatingColorClass($chambre["moyenchambre"]) . '">
                                ' . number_format($chambre["moyenchambre"], 1) . ' (' . $chambre["nbrAvis"] . ' avis)
                            </span>
                        </div>
                    </a>
                </div>';

            
        }
         
     }*/


     public function setChambreVlaue($filters,$destination=null, $hotelid=null) {
        $Chambres = $this->getFilteredChambres2($filters,$destination, $hotelid);
        $hotelsData = [];
    
        foreach ($Chambres as $index => $chambre) {
            $hotelsData[] = [
                "name" => $chambre["nom"],
                "stars"=>$chambre["hotelStar"],
                "address" => $chambre["hoteladress"], 
                "location"=>$chambre["location"],
                "index"=> $index
                
            ];
        }
    
        return $hotelsData;  
    
    }


}





//------------------------------------------------------Option-----------------------------------------------------------------

class Option {
    private $id_option;
    private $id_chambre;
    private $option_chambre;

 

    public function __construct($id_option, $id_chambre, $option_chambre) {
        $this->id_option = $id_option;
        $this->id_chambre = $id_chambre;
        $this->option_chambre = $option_chambre;
    }

    public function getId() { return $this->id_option; }
    public function getIdChambre() { return $this->id_chambre; }
    public function getOptionChambre() { return $this->option_chambre; }
    
}

class OptionManager {
    private $options = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM Options_Chambre");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->options[] = new Option(
                    $ligne->ID_Option,
                     $ligne->ID_Chambre,
                      $ligne->Option_Chambre 
                   );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getOptions() { return $this->options; }

 
}


//------------------------------------------------------function -----------------------------------------------------------------

function calcprixpromo($prix,$promotion){

    $mprix =$promotion * $prix /100;
    return $prix-$mprix;

 }

 function getStarsHtml($count) {
    $totalStars = 5;
    return str_repeat("★", $count) . str_repeat("☆", $totalStars - $count);
}
function getRatingColorClass($rating) {
    if ($rating >= 8) return "rating-excellent";
    if ($rating >= 6) return "rating-good";
    return "rating-bad";
}
function Ratingtostatus($rating) {
    if ($rating == 10) return 'Parfait';
    if ($rating >= 9 && $rating <10) return 'Merveilleux';
    if ($rating >= 8 && $rating < 9 ) return 'Très bien';
    if ($rating >= 6 && $rating < 8 ) return 'Bien';
    if ($rating >= 4 && $rating < 6 ) return 'Moyen';

    return 'Acceptable';
}

function getHotelType($type) {
    switch ($type) {
        case "Business":
            return "Hôtel Business";
        case "Luxe":
            return "Hôtel de Luxe";
        case "Standard":
            return "Hôtel Standard";
        case "Familiale":
            return "Hôtel Familial";
        case "Maisons":
            return "Maisons";
        default:
            return null;
    }
}
function getChambrebreType($type) {
    switch ($type) {
        case "Standard":
            return "Chambre Standard";
        case "Familiale":
            return "Chambre Familiale";
        case "Suite":
            return "Suite";
        case "Deluxe":
            return "Chambre Deluxe";
        case "VIP":
            return "Chambre VIP";
        default:
            return null;
    }
}



//------------------------------------------------------Client-----------------------------------------------------------------

class Client {
    private $ID_client , $ID_compte, $Nom_client, $Prenom_client , $DD_Naissance ,$Telephone,$Adresse_Client ;
    public function __construct($ID_client, $ID_compte, $Nom_client, $Prenom_client, $DD_Naissance,$Telephone,$Adresse_Client) {
        $this->ID_client = $ID_client;
        $this->ID_compte = $ID_compte;
        $this->Nom_client = htmlspecialchars($Nom_client, ENT_QUOTES, 'UTF-8');
        $this->Prenom_client = htmlspecialchars($Prenom_client, ENT_QUOTES, 'UTF-8');
        $this->DD_Naissance = $DD_Naissance;
        $this->Telephone = $Telephone;
        $this->Adresse_Client = htmlspecialchars($Adresse_Client, ENT_QUOTES, 'UTF-8');

   
    }
    public function getIdClient(){return $this->ID_client;}
    public function getIdCompte(){return $this->ID_compte;}
    public function getNomClient(){return $this->Nom_client;}
    public function getPrenomClient(){return $this->Prenom_client;}
    public function getDateN(){return $this->DD_Naissance;}
    public function getTelephone(){return $this->Telephone;}
    public function getAdresseClient(){return $this->Adresse_Client;}

}

class ClientManager {
    private $clients = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM client");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->clients[] = new Client(
                    $ligne->ID_Client, 
                    $ligne->ID_Compte , 
                    $ligne->Nom_Client, 
                    $ligne->Prenom_Client, 
                    $ligne->DD_Naissance,
                    $ligne->Telephone,
                    $ligne->Adresse_Client
                );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }
    public function getComptes() { return $this->clients; }

    public function getIdClientbyCompte($idcompte) { 
        
        foreach($this->clients as $c){
                if($idcompte == $c->getIdCompte()  ){
                   return $c->getIdClient();
     
                }
      
               



        } 
        
        return null;
      

    }
    public function getUsernameByIdClient($idclient){
        $cnx = mysqli_connect("localhost", "root", "", "hotel"); 
       $compte =new CompteManager($cnx);
       $C=$compte->getComptes();


       $idcompte=null;
       foreach($this->clients as $a){
            if($a->getIdClient() == $idclient ){
                $idcompte= $a->getIdCompte();
            }
              
       }

       foreach($C as $c){
        if($c->getId() ==   $idcompte ){
           return $c->getUsername();
        }
   }
     return null;

    }

}
//------------------------------------------------------Compte-----------------------------------------------------------------
class Compte {
    private $ID_compte , $Username , $Email, $Password ,$Typecompte;
    public function __construct($ID_compte, $Username, $Email, $Password, $Typecompte) {
        $this->ID_compte = $ID_compte;
        $this->Username = htmlspecialchars($Username, ENT_QUOTES, 'UTF-8');
        $this->Email = $Email;
        $this->Password = htmlspecialchars($Password, ENT_QUOTES, 'UTF-8');
        $this->Typecompte = $Typecompte;
   
    }
    public function getId(){return $this->ID_compte;}
    public function getUsername(){return $this->Username;}
    public function getEmail(){return $this->Email;}
    public function getPassword(){return $this->Password;}
    public function getTypeCompte(){return $this->Typecompte;}

  
}

class CompteManager {
    private $comptes = [];
   
    public function __construct($connexion) {
        if (!$connexion) {
            throw new Exception("Erreur de connexion à la base de données.");
        }
        $stmt = mysqli_prepare($connexion, "SELECT * FROM compte");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->comptes[] = new Compte(
                    $ligne->ID_Compte, 
                    $ligne->Username, 
                    $ligne->Email, 
                    $ligne->Password, 
                    $ligne->Type_Compte);
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }
    public function getComptes() { return $this->comptes; }


    public function getCompteLogin($Email, $Password) {
        
        foreach ($this->comptes as $c) {
            var_dump($c->getPassword());
            if ($c->getEmail() == $Email && password_verify($Password, $c->getPassword()) && $c->getTypeCompte() == "Client") {
                
                $_SESSION['Id_Compte'] = $c->getId();
                $_SESSION['Username'] = $c->getUsername(); 
                return true;
            }
        }
        return false;
    } 
    public function getCompteLogin2($Email, $Password) {
        
        foreach ($this->comptes as $c) {
            var_dump($c->getPassword());
            if ($c->getEmail() == $Email && password_verify($Password, $c->getPassword()) && $c->getTypeCompte() == "Client") {
                
              
                
                return  $c->getId();
            }
        }
        return null;
    } 
    public function emailExiste($Email) {
        foreach ($this->comptes as $c) {
            if ($c->getEmail() == $Email) {
                return true;
            }
        }
        return false;
    }

   
    public function ajouterCompte($Username, $Email, $Password, $cnx) {
     
        if ($this->emailExiste($Email)) {
            return false; 
        }
    
    
        $hashedPassword = password_hash($Password, PASSWORD_DEFAULT);
    
    
        $stmt = mysqli_prepare($cnx, "INSERT INTO compte (Username, Email, Password, Type_Compte) VALUES (?, ?, ?, 'Client')");
    
        if ($stmt) {
          
            mysqli_stmt_bind_param($stmt, "sss", $Username, $Email, $hashedPassword);
    
   
            $success = mysqli_stmt_execute($stmt);
    
        
            mysqli_stmt_close($stmt);
    
         
            if ($success) {
                $compteId = mysqli_insert_id($cnx); 
    
               
                $insertClientStmt = mysqli_prepare($cnx, "INSERT INTO client (ID_Compte) VALUES (?)");
                if ($insertClientStmt) {
                    mysqli_stmt_bind_param($insertClientStmt, "i", $compteId); 
                    mysqli_stmt_execute($insertClientStmt); 
                    mysqli_stmt_close($insertClientStmt);
                }
    
              
                $this->comptes[] = new Compte($compteId, $Username, $Email, $hashedPassword, "Client");
            }
    
            return $success;
        } else {
          
            error_log("Erreur SQL : " . mysqli_error($cnx));
            return false;
        }
    }
    public function ajouterinfoclient($idcompte, $nom, $prenom, $date_naissance, $telephone, $adresse,$cnx) {
        // Vérifier si un client existe déjà pour ce compte
        $sql = "SELECT ID_Client FROM Client WHERE ID_Compte = ?";
        $stmt = $cnx->prepare($sql);
        $stmt->bind_param("i", $idcompte);
        $stmt->execute();
        $result = $stmt->get_result();
        
        if ($result->num_rows > 0) {
            // Client existe, faire un UPDATE
            $sql = "UPDATE Client SET 
                    Nom_Client = ?, 
                    Prenom_Client = ?, 
                    DD_Naissance = ?, 
                    Telephone = ?, 
                    Adresse_Client = ? 
                    WHERE ID_Compte = ?";
            
            $stmt = $cnx->prepare($sql);
            $stmt->bind_param("sssssi", $nom, $prenom, $date_naissance, $telephone, $adresse, $idcompte);
        } else {
            // Client n'existe pas, faire un INSERT
            $sql = "INSERT INTO Client (ID_Compte, Nom_Client, Prenom_Client, DD_Naissance, Telephone, Adresse_Client) 
                    VALUES (?, ?, ?, ?, ?, ?)";
            
            $stmt = $cnx->prepare($sql);
            $stmt->bind_param("isssss", $idcompte, $nom, $prenom, $date_naissance, $telephone, $adresse);
        }
        
        if ($stmt->execute()) {
            return true;
        } else {
            return $stmt->error;
        }
    }
    
    
    

}  
    
//------------------------------------------------------Reservation-----------------------------------------------------------------

class Reservation {
    private $ID ,$ID_chambre ,$ID_client, $date_dpt , $date_rtn ,$statut ;
    public function __construct($ID,  $ID_chambre, $ID_client, $date_dpt,$date_rtn,$statut) {
        $this->ID = $ID;
        $this->ID_chambre = $ID_chambre;
        $this->ID_client = $ID_client;
        $this->date_dpt = $date_dpt;
        $this->date_rtn = $date_rtn;
        $this->statut = $statut;
     
   

   
    }
    public function getIdClient(){return $this->ID_client;}
    public function getIdChambre(){return $this->ID_chambre;}
   

}

class ReservationManager {
    private $reservations = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}
        $stmt = mysqli_prepare($connexion, "SELECT * FROM Reservation");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->reservations[] = new Reservation(
                    $ligne->ID_Reservation, 
                    $ligne->ID_Chambre , 
                    $ligne->ID_Client, 
                    $ligne->Date_Debut, 
                    $ligne->Date_Fin,
                    $ligne->Statut_Reservation,
                
                );
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }
    public function getReservations() { return $this->reservations; }

    
    public function ajouterReservation($id_chambre, $idclient1, $datedp, $datedrt, $statut, $cnx) {
       
        $c = new ClientManager($cnx);
        $idclient = $c->getIdClientbyCompte($idclient1);
    
        if (!$idclient) {
            error_log("Erreur : ID du client introuvable pour le compte $idclient1");
            return false;
        }
    
    
        $stmt = mysqli_prepare($cnx, "INSERT INTO Reservation (ID_Chambre, ID_Client, Date_Debut, Date_Fin, Statut_Reservation) VALUES (?, ?, ?, ?, ?)");
    
        if ($stmt) {
        
            mysqli_stmt_bind_param($stmt, "iisss", $id_chambre, $idclient, $datedp, $datedrt, $statut);
    
        
            $success = mysqli_stmt_execute($stmt);
    
         
            if ($success) {
                $reserId = mysqli_insert_id($cnx);
                $this->reservations[] = new Reservation($reserId, $id_chambre, $idclient, $datedp, $datedrt, $statut);
            }
    
            
            mysqli_stmt_close($stmt);
    
            return $success;
        } else {
            error_log("Erreur SQL : " . mysqli_error($cnx));
            return false;
        }
    }
    

}
//------------------------------------------------------Avis-----------------------------------------------------------------

class Avis {
    private $id_avis;
    private $id_chambre;
    private $id_client;
    private $nbr;
    private $description;
    private $date;

    public function __construct($id_A, $id_Chambre, $id_Client, $nbr, $description, $date) {
        $this->id_avis = $id_A;
        $this->id_chambre = $id_Chambre;
        $this->id_client = $id_Client;
        $this->nbr = $nbr;
        $this->description = $description;
        $this->date = $date;
    }

    public function getId() { return $this->id_avis; }
    public function getNbrsur10() { return $this->nbr; }
    public function getIdChambre() { return $this->id_chambre; }
    public function getDescription() { return $this->description; }
    public function getDate() { return $this->date; }
    public function getIdClient() { return $this->id_client; }
}

class AvisManager {
    private $avis = [];

    public function __construct($connexion) {
        if (!$connexion) {
            throw new Exception("Erreur de connexion à la base de données.");
        }
        
        $stmt = mysqli_prepare($connexion, "SELECT * FROM avis");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->avis[] = new Avis(
                    $ligne->ID_Avis, 
                    $ligne->ID_Chambre, 
                    $ligne->ID_Client, 
                    $ligne->Nb_etoile, 
                    htmlspecialchars($ligne->Description_Avis, ENT_QUOTES, 'UTF-8'), 
                    $ligne->Date_Avis
                );
            }
            mysqli_stmt_close($stmt);
        } else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getAvis() { return $this->avis; }

    public function ajouterAvis($idc, $idclient, $rating, $avis, $date, $cnx) {
        $stmt = mysqli_prepare($cnx, "INSERT INTO Avis (ID_Chambre, ID_Client, Nb_etoile, Description_Avis, Date_Avis) VALUES (?, ?, ?, ?, ?)");
    
        if ($stmt) {
            mysqli_stmt_bind_param($stmt, "iiiss", $idc, $idclient, $rating, $avis, $date);
            $success = mysqli_stmt_execute($stmt);
            
            if (!$success) {
                error_log("Erreur MySQL lors de l'ajout d'avis: " . mysqli_stmt_error($stmt));
                echo "Erreur lors de l'ajout de l'avis: " . mysqli_stmt_error($stmt);
            }
            
            mysqli_stmt_close($stmt);
    
            if ($success) {
                $avisId = mysqli_insert_id($cnx);
                $this->avis[] = new Avis($avisId, $idc, $idclient, $rating, htmlspecialchars($avis, ENT_QUOTES, 'UTF-8'), $date);
                return true;
            }
            return false;
        } else {
            $error = mysqli_error($cnx);
            error_log("Erreur de préparation SQL : " . $error);
            echo "Erreur de préparation SQL : " . $error;
            return false;
        }
    }
    


}

//------------------------------------------------------Image Hotel-----------------------------------------------------------------

class ImageH {
    private $id_image;
    private $id_Hotel;
    private $url;

    public function __construct($id, $id_Hotel, $url) {
        $this->id_image = $id;
        $this->id_Hotel = $id_Hotel;
        $this->url = htmlspecialchars($url, ENT_QUOTES, 'UTF-8');
    }

    public function getId() { return $this->id_image; }
    public function getIdHotel() { return $this->id_Hotel; }
    public function getUrl() { return $this->url; }
}



class ImageManagerH {
    private $images = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}

        $stmt = mysqli_prepare($connexion, "SELECT * FROM  Image_H ");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->images[] = new ImageH($ligne->ID_Image, $ligne->ID_Hotel, $ligne->URL);
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getImages() { return $this->images; }
}

//------------------------------------------------------Image Chambre-----------------------------------------------------------------

class ImageC {
    private $id_image;
    private $id_Chambre;
    private $url;

    public function __construct($id, $id_Chambre, $url) {
        $this->id_image = $id;
        $this->id_Chambre =$id_Chambre;
        $this->url = htmlspecialchars($url, ENT_QUOTES, 'UTF-8');
    }

    public function getId() { return $this->id_image; }
    public function getIdChambre() { return $this->id_Chambre; }
    public function getUrl() { return $this->url; }
}


class ImageManagerC {
    private $images = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}

        $stmt = mysqli_prepare($connexion, "SELECT * FROM  Image_C ");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->images[] = new ImageC($ligne->ID_Image, $ligne->ID_Chambre, $ligne->URL);
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getImages() { return $this->images; }
}
//------------------------------------------------------Image service-----------------------------------------------------------------

class ImageS {
    private $id_image;
    private $id_Service;
    private $url;

    public function __construct($id, $id_Service, $url) {
        $this->id_image = $id;
        $this->id_Service =$id_Service;
        $this->url = htmlspecialchars($url, ENT_QUOTES, 'UTF-8');
    }

    public function getId() { return $this->id_image; }
    public function getIdService() { return $this->id_Service; }
    public function getUrl() { return $this->url; }
}


class ImageManagerS {
    private $images = [];

    public function __construct($connexion) {
        if (!$connexion) {throw new Exception("Erreur de connexion à la base de données.");}
        if (!mysqli_select_db($connexion, 'hotel')) {throw new Exception("Accès à la base impossible.");}

        $stmt = mysqli_prepare($connexion, "SELECT * FROM  Image_S ");
        if ($stmt) {
            mysqli_stmt_execute($stmt);
            $result = mysqli_stmt_get_result($stmt);
            while ($ligne = mysqli_fetch_object($result)) {
                $this->images[] = new ImageS($ligne->ID_Image, $ligne->ID_Service, $ligne->URL);
            }
            mysqli_stmt_close($stmt);
        }else {
            error_log("Erreur SQL : " . mysqli_error($connexion));
        }
    }

    public function getImages() { return $this->images; }
}


?>