function selectPaymentMethod(element) {
    // Remove selected class from all payment methods
    const paymentMethods = document.querySelectorAll('.payment-method');
    paymentMethods.forEach(method => {
        method.classList.remove('selected');
    });
    
    // Add selected class to clicked element
    element.classList.add('selected');
}

function reserveNow() {
    const cardName = document.getElementById('cardName').value.trim();
    const cardNumber = document.getElementById('cardNumber').value.trim();
    const month = document.getElementById('month').value.trim();
    const year = document.getElementById('year').value.trim();
    const cvv = document.getElementById('cvv').value.trim();
    
    if (!cardName || !cardNumber || !month || !year || !cvv) {
        alert('Veuillez remplir tous les champs de paiement');
        return;
    }
    
    
    window.location.href = 'Check.php';
}
