$(document).ready(function () {
    $('#input-barcode-cloth-acceptance').keypress(function (event) {
        if(pressedKeyIsENTER(event.which)) {
            let barcode = $(this).val();
            if(barcodeIsValid(barcode)) {
                let exchangeType = getExchangeType();
                sendAcceptanceRequest(barcode, exchangeType);
                clearInput($(this));
            } else {
                alert("Nieprawidłowy kod kreskowy!");
            }
        }
    })
});

function getWithdrawnCloth(barcode) {
    let withdrawnCloth = {
        ordinalNumber: getOrdinalNumberFromInput(),
        barcode: barcode,
        assignment: getAssignmentDateFromInput()
    };
    return withdrawnCloth;
}

$(document).ready(function () {
    $('#input-barcode-cloth-assignment')
        .keypress(function (event) {
        if(pressedKeyIsENTER(event.which)) {
            let barcode = $(this).val();
            if(barcodeIsValid(barcode)) {
                const assignmentType = getAssignmentType();
                if(assignmentType == "ASSIGN_WITHDRAWN_CLOTH") {
                    let withDrawnCloth = getWithdrawnCloth(barcode);
                    let article = getArticleFromInput();
                    let size = getSizeFromInput();
                    sendRequestForAssignWithdrawnCloth(withDrawnCloth, article, size)
                } else if (assignmentType == "RELEASE_ROTATIONAL_CLOTH") {
                    sentRequestForReleaseRotationalCloth(barcode)
                }
                reloadEmployee();
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
    $input.val("");
}

function sendRequestForAssignWithdrawnCloth(withdrawnCloth, article, size) {
    let n = clientId + 1;
    $.ajax({
        url: `http://localhost:8080/clothes/assign-withdrawn-cloth/${clientId}/${userId}/${employeeId}/${article}/${size}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(withdrawnCloth),
        success: function (response) {
            console.log(response);
        }
    })
}

function sentRequestForReleaseRotationalCloth(barcode) {
    $.ajax({
        url: `http://localhost:8080/clothes/release-rotational-cloth` +
            `/${clientId}/${userId}/${employeeId}/${barcode}`,
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
        data: JSON.stringify(response),
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