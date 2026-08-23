<!DOCTYPE html>
<html lang="fr">
<head>






        <?php
        session_start();
       

        if (isset($_SESSION['roomPrice']) && isset($_SESSION['selectedServices']) && isset($_SESSION['totalPrice']) &&isset($_SESSION['days'])) {
            $roomPrice = $_SESSION['roomPrice'];
            $selectedServices = $_SESSION['selectedServices'];
            $totalPrice = $_SESSION['totalPrice'];
            $days =$_SESSION['days'];
        }
     
          
        ?>

    
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Paiement</title>
    <link rel="stylesheet" href="css/style-payement.css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
    
</head>
<body>
    <div class="container">
        <div class="header">
            <img src="icon/HotelPlus.png" alt="Logo HotelPlus" class="logo">
            <div class="brand">HotelPlus</div>
        </div>
        
        <div class="content">
            <div class="payment-section">
                <h2>SÉLECTIONNER LE MODE DE PAIEMENT</h2>
                
                <div class="payment-methods">
                    <div class="payment-method selected" onclick="selectPaymentMethod(this)">
                        <div class="card-icon"></div>
                    </div>
                    <div class="payment-method" onclick="selectPaymentMethod(this)">
                        <div class="card-icon"></div>
                    </div>
                    <div class="payment-method" onclick="selectPaymentMethod(this)">
                        <div class="card-icon"></div>
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="cardName">NOM SUR LA CARTE</label>
                    <input type="text" id="cardName">
                </div>
                
                <div class="form-group">
                    <label for="cardNumber">NUMÉRO DE CARTE</label>
                    <input type="text" id="cardNumber">
                </div>
                
                <div class="form-group expiry-cvv">
                    <div class="form-group">
                        <label>MOIS</label>
                        <select id="month">
                            <option value="" selected disabled>Sélectionner</option>
                            <option value="01">01</option>
                            <option value="02">02</option>
                            <option value="03">03</option>
                            <option value="04">04</option>
                            <option value="05">05</option>
                            <option value="06">06</option>
                            <option value="07">07</option>
                            <option value="08">08</option>
                            <option value="09">09</option>
                            <option value="10">10</option>
                            <option value="11">11</option>
                            <option value="12">12</option>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label>ANNÉE</label>
                        <select id="year">
                            <option value="" selected disabled>Sélectionner</option>
                            <option value="2025">2025</option>
                            <option value="2026">2026</option>
                            <option value="2027">2027</option>
                            <option value="2028">2028</option>
                            <option value="2029">2029</option>
                            <option value="2030">2030</option>
                        </select>
                    </div>
                    
                    <div class="form-group cvv">
                        <label for="cvv">CVV</label>
                        <input type="text" id="cvv" maxlength="3">
                    </div>
                </div>
            </div>
            
            <div class="summary-section">
                <h2>TARIF D'HÉBERGEMENT</h2>
                
                <div class="price-item">
                   <?php
                   echo'  <span>Prix de chambre</span>
                           <span>'.  number_format($roomPrice, 2).'€</span>';
                    ?>
                </div>
                
               
                <?php
                foreach($selectedServices as $s){
                  
                echo   ' 
                 <div class="price-item">
                    <span>'. htmlspecialchars($s['name']) .' </span>
                    <span>'. number_format($s['price'], 2) .'€</span>
                </div>';
                }

                ?>
                   
                
                <div class="price-total">

                <?php
                   echo'  <span>Prix total</span>
                           <span>'.  number_format($totalPrice,2).'€</span>';
                    ?>
                 
                </div>
                <?php echo'
                <div class="summary-details">
                    pour '.$days.' nuits<br>
                    '.$roomPrice / $days.'€ par nuit<br>
                    taxes et frais compris
                </div>';
                ?>
                 
                <button class="reserve-button" onclick="reserveNow()">RÉSERVER MAINTENANT</button>
            </div>
        </div>
    </div>

    <script src="script-payement.js"></script>
</body>
</html>