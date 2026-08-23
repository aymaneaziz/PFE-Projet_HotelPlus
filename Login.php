<?php
    require_once("Class.php");

  
    session_start();

    $connexion = mysqli_connect("localhost", "root", "","hotel"); 
   


    if (!isset($_GET['page'])) {
        header("Location: index.php");
        exit();
    }
    




    if (!$connexion) {
        die("Échec de la connexion : " . mysqli_connect_error());
    }
   

    if ($_SERVER["REQUEST_METHOD"] === "POST") {
        $Email = isset($_POST["Email"]) ? trim($_POST["Email"]) : "";
        $Password = isset($_POST["password"]) ? trim($_POST["password"]) : "";

        $C = new CompteManager($connexion);
        $conx = $C->getCompteLogin($Email, $Password);
        $page = basename($_GET['page']); 
        if ($conx) {
           
            if (isset($_GET['page'])) {
               
               
                header("Location: $page");
                exit();
            }
        
            
        } else {
            $_SESSION['error'] = "Email ou mot de passe incorrect. Veuillez réessayer.";

            header("Location: Login.php?page=$page"); 
            exit;
        }
    }

    
    $erreur = isset($_SESSION['error']) ? $_SESSION['error'] : "";
    unset($_SESSION['error']);





    
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Connexion</title>
    <link rel="stylesheet" href="css\StyleLogin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
</head>
<body>
    <div class="container">
        <div class="login-box">
            <div class="logo">
                <img src="icon/HotelPlus.png" alt="Logo HotelPlus">
                <h1>HotelPlus</h1>
                <p>Connectez-vous pour continuer vers HotelPlus</p>
            </div>

            <?php if (!empty($erreur)): ?>
                <div class="error-message"><?php echo htmlspecialchars($erreur); ?></div>
            <?php endif; ?>

            <form method="POST">
                <div class="input-group">
                    <label for="Email">E-mail</label>
                    <div class="input-with-icon">
                        <i class="fas fa-user"></i>
                        <input type="text" id="Email" name="Email" required>
                    </div>
                </div>

                <div class="input-group">
                    <label for="password">Mot de passe</label>
                    <div class="input-with-icon">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="password" name="password" required>
                    </div>
                </div>

                <!--
                <div class="remember-me">
                    <input type="checkbox" id="remember" name="remember">
                    <label for="remember">Se souvenir de moi</label>
                </div>
            -->
                <button type="submit">
                    <span class="button-text">Se connecter</span>
                </button>
            </form>
            <?php  $page = basename($_GET['page']);
            echo'
            <p class="signup-link">Vous n\'avez pas de compte ? <a href="Signup.php?page='.$page.'">Inscrivez-vous</a></p>';
        ?></div>
    </div>
</body>
</html>
