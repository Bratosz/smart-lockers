$(document).ready(function () {
    $('#input-barcode-cloth-acceptance').keypress(function (event) {
        if(pressedKeyIsENTER(event.which)) {
            let barcode = $(this).val();
            if(barcodeIsValid(barcode)) {
                let exchangeType = getExchangeType();
                sendAcceptanceRequest(barcode, exchangeType);
                clearInput($(this));
                reloadBox();
            } else {
                alert("Nieprawidłowy kod kreskowy!");
            }
        }
    })
});

function getWithdrawnCloth() {
    let cloth
}

$(document).ready(function () {
    $('#input-barcode-cloth-assignment')
        .keypress(function (event) {
        if(pressedKeyIsENTER(event.which)) {
            let barcode = $(this).val();
            if(barcodeIsValid(barcode)) {
                const assignmentType = getAssignmentType();
                if(assignmentType == "ASSIGN_WITHDRAWN_CLOTH") {
                    let withDrawnCloth = getWithdrawnCloth();
                    sendRequestForAssignWithdrawnCloth(withDrawnCloth)
                } else if (assignmentType == "RELEASE_ROTATIONAL_CLOTH") {
                    sentRequestForReleaseRotationalCloth(barcode)
                }
                reloadBox();
                clearInput($(this));
            } else {
                alert("Nieprawidłowy kod kreskowy!");
            }
        }
    });
});

function getAssignmentType() {
    return $('input[name="assignment"]:checked').val();
}

function barcodeIsValid(barCode) {
    if(barCode.length == 13) {
        return true;
    } else {
        return false;
    }
}

function getExchangeType() {
    return $('input[name="exchange"]:checked').val();
}

function clearInput($input) {
    $input().val("");
}

function sendRequestForAssignWithdrawnCloth() {

}

function sentRequestForReleaseRotationalCloth(barcode) {
    $.ajax({
        url: `http://localhost:8080/clothes/rotation-release/${clientId}/${userId}/${barcode}/${employeeId}`,
        method: "post",
        success: function (response) {
            console.log(response);
        }
    })
}

function sendAcceptanceRequest(barcode) {
    $.ajax({
        url: `http://localhost:8080/clothes/acceptance/${clientId}/${userId}/${barcode}/${exchangeType}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(barCodes),
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