<?php
require_once("Class.php");
session_start();

// Vérifier si l'utilisateur est connecté
if (!isset($_SESSION['Id_Compte'])) {
    header('Location: login.php');
    exit();
}

if (!isset($_GET['page'])) {
    header("Location: index.php");
    exit();
}


$cnx = mysqli_connect("localhost", "root", "", "hotel");

if (!$cnx) {
    die("Erreur de connexion à la base de données : " . mysqli_connect_error());
}

$page = isset($_GET['page']) ? basename($_GET['page']) : '';
$compteManager = new CompteManager($cnx);


$id_compte = $_SESSION['Id_Compte'];
$message = "";


if ($_SERVER["REQUEST_METHOD"] == "POST") {
 
    $nom = htmlspecialchars($_POST['nom']);
    $prenom = htmlspecialchars($_POST['prenom']);
    $date_naissance = htmlspecialchars($_POST['date_naissance']);
    $telephone = htmlspecialchars($_POST['telephone']);
    $adresse = htmlspecialchars($_POST['adresse']);
    
    
    $result = $compteManager->ajouterinfoclient(
        $id_compte,
        $nom,
        $prenom, 
        $date_naissance, 
        $telephone, 
        $adresse,
        $cnx
    );
    
    if ($result === true) {
        
       
        if (isset($_GET['page'])) {
            header("Location: $page");
            exit();
        }
    
    } else {
        $message = "<div class='alert alert-danger'>Erreur: " . $result . "</div>";
    }
}



mysqli_close($cnx);
?>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>HotelPlus - Vos Informations</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link rel="icon" href="icon/icons8-hôtel-5-étoiles-96.png" type="image/png">
    <style>
        body {
            background-color:#e4e4e4;
            padding-top: 50px;
        }
        .form-container {
            background-color: #fff;
            border-radius: 10px;
            transition: all 0.3s;
            padding: 30px;
            max-width: 700px;
            margin: 0 auto;
        }
        .form-container:hover {
            transform: scale(1.01);
            box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
        }
        .form-title {
            color: #009aff;
            margin-left: -90px;
            text-align: center;
            font-weight: bold;
        }
        .btn-primary {
            background-color: #009aff;
            border: none;
            width: 100%;
            padding: 12px;
            font-weight: bold;
            margin-top: 20px;
            transition: all 0.3s;

        }
        .btn-primary:hover {
            background-color: #1d4ed8;
            box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
            transform: translateY(-2px);

           
        }
        .form-control:focus {
            border-color: #1d4ed8;
               box-shadow: 0px 4px 15px rgba(0, 0, 0, 0.2);
        }
        .form-label {
            font-weight: 600;
            color: #495057;
        }
        .header-container {
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }
        .logo {
            max-width: 120px;
            margin-right: 20px;
            cursor: pointer;
           
        }
     
        .header-title {
            flex-grow: 1;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="form-container">
            <div class="header-container">
                <img src="icon/HotelPlus.png" class="logo" alt="Hotel Plus Logo">
                <h2 class="form-title header-title">Vos Informations</h2>
            </div>
            
            <?php echo $message; ?>
            
            <form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]); if(isset($_GET['page'])) echo '?page=' . htmlspecialchars($_GET['page']); ?>">
                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="nom" class="form-label">Nom</label>
                        <input type="text" class="form-control" id="nom" name="nom"  placerequired>
                    </div>
                    <div class="col-md-6">
                        <label for="prenom" class="form-label">Prénom</label>
                        <input type="text" class="form-control" id="prenom" name="prenom"  required>
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="date_naissance" class="form-label">Date de naissance</label>
                    <input type="date" class="form-control" id="date_naissance" name="date_naissance"  required>
                </div>
                
                <div class="mb-3">
    <label for="telephone" class="form-label">Téléphone</label>
    <input type="tel" class="form-control" id="telephone" name="telephone" placeholder="000-000-000-000"  title="Veuillez entrer un numéro de téléphone valide (ex : 000-000-000-000)">
</div>

                
                <div class="mb-3">
                    <label for="adresse" class="form-label">Adresse</label>
                    <textarea class="form-control" id="adresse" name="adresse" rows="3" required></textarea>
                </div>
                
                <button type="submit" class="btn btn-primary">Envoyer</button>
            </form>
        </div>
    </div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
</body>
</html>