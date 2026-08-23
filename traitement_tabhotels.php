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

        $destination = null;
        if(isset($_SESSION['destination'])) {
            $destination = $_SESSION['destination'];
        }
        
        $hotelid = null;
        if(isset($_SESSION['hotelId'])) {
            $hotelid = $_SESSION['hotelId'];
        }

        // Récupérer les données des hôtels pour la carte
        $hotelsData = $C->setChambreVlaue($filters, $destination, $hotelid);
        
        
        $hotelName = $_POST['hotelName'] ?? '';
        if (!empty($hotelName)) {
            $hotelsData = array_filter($hotelsData, function($hotel) use ($hotelName) {
                return stripos($hotel['name'], $hotelName) !== false;
            });
            // Réindexer le tableau après filtrage
            $hotelsData = array_values($hotelsData);
        }
        
        
        $sortOption = $_POST['sortOption'] ?? '';
        if (!empty($sortOption)) {
            
            $chambres = $C->getFilteredChambres2($filters, $destination, $hotelid);
            
            // Filtrer les chambres par nom d'hôtel si nécessaire
            if (!empty($hotelName)) {
                $chambres = array_filter($chambres, function($chambre) use ($hotelName) {
                    return stripos($chambre['nom'], $hotelName) !== false;
                });
            }
            
           
            $sortData = [];
            foreach ($chambres as $chambre) {
                $hotelName = $chambre['nom'];
                if (!isset($sortData[$hotelName])) {
                    $sortData[$hotelName] = [
                        'prix' => $chambre['prix'],
                        'note' => $chambre['moyenchambre'],
                        'avis' => $chambre['nbrAvis']
                    ];
                } else {
                  
                    if ($chambre['prix'] < $sortData[$hotelName]['prix']) {
                        $sortData[$hotelName]['prix'] = $chambre['prix'];
                    }
                    
                    if ($chambre['moyenchambre'] > $sortData[$hotelName]['note']) {
                        $sortData[$hotelName]['note'] = $chambre['moyenchambre'];
                    }
                    
                    $sortData[$hotelName]['avis'] += $chambre['nbrAvis'];
                }
            }
            
       
            usort($hotelsData, function($a, $b) use ($sortOption, $sortData) {
                $nameA = $a['name'];
                $nameB = $b['name'];
                
                switch($sortOption) {
                    case 'Prix (croissant)':
                        return $sortData[$nameA]['prix'] - $sortData[$nameB]['prix'];
                    case 'Prix (décroissant)':
                        return $sortData[$nameB]['prix'] - $sortData[$nameA]['prix'];
                    case 'Mieux notés':
                        return $sortData[$nameB]['note'] - $sortData[$nameA]['note'];
                    case 'Plus populaires':
                        return $sortData[$nameB]['avis'] - $sortData[$nameA]['avis'];
                    default:
                        return 0;
                }
            });
        }
        
        
        echo json_encode($hotelsData);
    }
?>