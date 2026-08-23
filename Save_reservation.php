<?php

session_start();


$json = file_get_contents('php://input');
$data = json_decode($json, true);

if ($data) {
    $_SESSION['roomPrice'] = $data['roomPrice'];
    $_SESSION['selectedServices'] = $data['selectedServices'];
    $_SESSION['totalPrice'] = $data['totalPrice'];
    
    echo json_encode(['success' => true, 'message' => 'Sessions sauvegardées']);
} else {
    echo json_encode(['success' => false, 'message' => 'Données invalides']);
}
?>