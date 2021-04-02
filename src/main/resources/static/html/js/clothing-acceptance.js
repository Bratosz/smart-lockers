$(document).ready(function () {
    $('#input-bar-code').keypress(function (event) {
        let keyPressed = event.which;
        let barCode = getBarCodeValue();
        if(pressedKeyIsENTER(keyPressed) && barCodeIsValid(barCode)) {
            let exchangeType = getExchangeType();
            sendAcceptanceRequest(barCode, exchangeType);
            clearInputBarCodeField();
        } else if(pressedKeyIsENTER(keyPressed)) {
            alert("Nieprawidłowy kod kreskowy!")
        }
    })
});

function getInputBarCode() {
    return $("#input-bar-code");
}

function getBarCodeValue() {
    return getInputBarCode().val()
}

function barCodeIsValid(barCode) {
    if(barCode.length == 13) {
        return true;
    } else {
        return false;
    }
}

function getExchangeType() {
    return $('input[name="exchange"]:checked').val();
}

function clearInputBarCodeField() {
    getInputBarCode().val("");
}

function sendAcceptanceRequest(barCode, exchangeType) {
    $.ajax({
        url: `http://localhost:8080/clothes/acceptance/${clientId}/${userId}/${barCode}/${exchangeType}`,
        method: "post",
        success: function (response) {
            console.log(response)
        }
    })
}



function pressedKeyIsENTER(char) {
    let ENTERKeyValue = 13;
    if(char == ENTERKeyValue) {
        return true;
    } else {
        return false;
    }
}