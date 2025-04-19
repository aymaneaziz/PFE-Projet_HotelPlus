<?php   
session_start();


unset($_SESSION['Id_Compte']);
unset($_SESSION['Username']);


//sleep(1);

header("Location: index.php");
exit();
?>
