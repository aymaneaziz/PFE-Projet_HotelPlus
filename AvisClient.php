<?php
session_start();
require_once("Class.php");

if ($_SERVER['REQUEST_METHOD'] === 'POST' && isset($_POST['rating'], $_POST['review'], $_POST['roomId'], $_SESSION['Id_Compte'])) {
    
    $rating = $_POST['rating'];
    $review = trim($_POST['review']) ?: die("Erreur : l'avis ne peut pas être vide.");
    $roomId = $_POST['roomId'];

 
    $cnx = mysqli_connect("localhost", "root", "", "hotel") or die("Erreur de connexion : " . mysqli_connect_error());
    
  
    $c = new ClientManager($cnx);
    $userId = $c->getIdClientbyCompte($_SESSION['Id_Compte']);
    
   
    $avis = new AvisManager($cnx);
    echo $avis->ajouterAvis($roomId, $userId, $rating, $review, date('Y-m-d H:i:s'), $cnx) 
        ? "Avis ajouté avec succès!" 
        : "Erreur lors de l'ajout de l'avis.";

    mysqli_close($cnx);
} else {
    echo "Requête invalide";
}
?>
