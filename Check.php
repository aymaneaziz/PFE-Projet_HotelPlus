<?php
session_start();



require_once("Class.php");
$connexion = mysqli_connect("localhost", "root", "");
$conne = mysqli_connect("localhost", "root", "",'hotel');
       $rs=new ReservationManager($connexion);




       if(isset( $_SESSION['ChambreId'])&& isset( $_SESSION['Id_Compte']) &&isset( $_SESSION['departure_date'] )&& isset($_SESSION['return_date'] )){
       $rs->ajouterReservation($_SESSION['ChambreId'],$_SESSION['Id_Compte'],$_SESSION['departure_date'],$_SESSION['return_date'],"En cours",$conne);
    
    }

    if (isset($_SESSION['roomPrice']) && isset($_SESSION['selectedServices']) && isset($_SESSION['totalPrice'])) {
        $roomPrice = $_SESSION['roomPrice'];
        $selectedServices = $_SESSION['selectedServices'];
        $totalPrice = $_SESSION['totalPrice'];
    } else {
      
        header("Location: index.php");
        exit;
    }
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réservation Confirmée</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            text-align: center;
        }
        
        .confirmation-container {
            background-color: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
            padding: 40px;
            max-width: 500px;
            width: 90%;
        }
        
        .check-icon {
            width: 80px;
            height: 80px;
            background-color: #4CAF50;
            border-radius: 50%;
            margin: 0 auto 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        .check-icon svg {
            width: 40px;
            height: 40px;
            fill: white;
        }
        
        h1 {
            color: #333;
            margin-bottom: 10px;
        }
        
        p {
            color: #666;
            margin-bottom: 20px;
        }
        
        .spinner {
            margin-top: 20px;
            width: 40px;
            height: 40px;
            border: 4px solid rgba(0, 0, 0, 0.1);
            border-radius: 50%;
            border-top-color: #4CAF50;
            display: inline-block;
            animation: spin 1s ease-in-out infinite;
        }
        
        @keyframes spin {
            to { transform: rotate(360deg); }
        }
        
        .reservation-details {
            background-color: #f9f9f9;
            border-radius: 5px;
            padding: 15px;
            margin-top: 20px;
            text-align: left;
        }
        
        .price-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
            padding: 5px 0;
            border-bottom: 1px solid #eee;
        }
        
        .total {
            font-weight: bold;
            border-top: 2px solid #000;
            margin-top: 15px;
            padding-top: 10px;
        }
    </style>
    <script>
        
        window.onload = function() {
            setTimeout(function() {
                window.location.href = 'index.php';
            }, 2000);
        };
    </script>
</head>
<body>
    <div class="confirmation-container">
        <div class="check-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41L9 16.17z"/>
            </svg>
        </div>
        
        <h1>Réservation Confirmée</h1>
        <p>Votre réservation a été enregistrée avec succès !</p>
        
        <div class="reservation-details">
            <div class="price-item">
                <span>Prix de chambre</span>
                <span><?= number_format($roomPrice, 2) ?>€</span>
            </div>
            
            <?php if (!empty($selectedServices)): ?>
                <?php foreach ($selectedServices as $service): ?>
                    <div class="price-item">
                        <span><?= htmlspecialchars($service['name']) ?></span>
                        <span><?= number_format($service['price'], 2) ?>€</span>
                    </div>
                <?php endforeach; ?>
            <?php endif; ?>
            
            <div class="price-item total">
                <span>Prix total</span>
                <span><?= number_format($totalPrice, 2) ?>€</span>
            </div>
        </div>
        
        <p>Vous allez être redirigé vers la page d'accueil...</p>
        <div class="spinner"></div>
    </div>
</body>
</html>