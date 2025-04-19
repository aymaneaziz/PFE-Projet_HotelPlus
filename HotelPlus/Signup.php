<?php

require_once("Class.php");

$cnx = mysqli_connect("localhost", "root", "", "hotel");


if ( !$cnx ) {
    die("Erreur de connexion à la base de données : " . mysqli_connect_error());
}

$compteManager = new CompteManager($cnx);

$message = "";
session_start();

$page = basename($_GET['page']);
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $username = trim($_POST['username']);
    $email = trim($_POST['email']);
    $password = trim($_POST['password']);

    if (empty($username) || empty($email) || empty($password)) {
        $message = "Veuillez remplir tous les champs.";
    } elseif ($compteManager->ajouterCompte($username, $email, $password,$cnx)) {

        
        if ($compteManager->getCompteLogin($email, $password)) {

            if (isset($_GET['page'])) {
               
               
                header("Location: info_client.php?page=$page");
                exit();
            }
        
         
        }
      
    } else {
        $message = "Cet e-mail est déjà utilisé !!";
    }
}
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Inscription</title>
    <link rel="stylesheet" href="css/StyleLogin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
</head>
<body>
      <div class="container">
        <div class="login-box">
            <div class="logo">
                <img src="icon\HotelPlus.png" alt="Logo HotelPlus">
                <h1>HotelPlus</h1>
                <p>Inscrivez-vous pour un nouveau compte</p>
            </div>
            <?php if (!empty($message)):?>
                <div class="error-message"><?php echo htmlspecialchars($message); ?></div>
            <?php endif; ?>
            <form id="signup-form"  method="POST"> <!--  signup_process.php         -->
                <div class="input-group">
                    <label for="username">Nom d'utilisateur</label>
                    <div class="input-with-icon">
                        <i class="fas fa-user"></i>
                        <input type="text" id="username" name="username" required>
                    </div>
                </div>
                <div class="input-group">
                    <label for="email">E-mail</label>
                    <div class="input-with-icon">
                        <i class="fas fa-envelope"></i>
                        <input type="text" id="email" name="email" required>
                    </div>
                </div>
                <div class="input-group">
                    <label for="password">Mot de passe</label>
                    <div class="input-with-icon">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="password" name="password" required minlength="8">
                    </div>
                    <small class="password-hint">Le mot de passe doit contenir au moins 8 caractères</small>
                </div>
                <button type="submit">
                    <span class="button-text">S'inscrire</span>
                    <span class="button-loader" style="display: none;">
                        <i class="fas fa-spinner fa-spin"></i>
                    </span>
                </button>
            </form><?php
            $page = basename($_GET['page']);
            echo'
            <p class="signup-link">Vous avez déjà un compte ? <a href="Login.php?page='.$page.'">Connectez-vous</a></p>';?>
            <div class="triangle"></div>
        </div>
    </div>
</body>
</html>
