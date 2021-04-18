const url = new URL(window.location.href);
const boxId = url.searchParams.get("id");
let lockerNumber,
    boxNumber,
    lastName,
    firstName,
    employeeId,
    employee,
    clothes,
    clothOrders,
    boxStatus;


reloadBox();

$("#button-load-employee").click(function () {
    loadEmployee();
});

$("#refresh-button").click(function () {
    if (boxStatus == "Wolna") {
        loadEmployee();
    } else {
        updateClothes();
    }
});

$("#button-perform-action-on-orders").click(function () {
    performActionOnOrders();
});

$('#button-add-order').click(function () {
    addOrder();

});

function refreshOrders() {
    $.ajax({
            url: `http://localhost:8080/orders/get-by-employee/${employeeId}`,
            method: "get",
            success: function (actualOrders) {
                displayOrders(actualOrders);
            }
        }
    )
}

function addOrder() {
    let barcodes = new Array;
    let articleNumber;
    let size;
    let orderType = $("#select-order-type").val();
    $('#table-of-clothes-body').find('input[type="checkbox"]:checked').each(function () {
        let clothBarcode = parseInt($(this).closest('tr').find('.cell-barcode').text());
        barcodes.push(clothBarcode);
    });
    articleNumber = $('input[name="cloth"]:checked').val();
    if (typeof articleNumber === "undefined") {
        articleNumber = 0;
    }
    size = getSizeFromInput();
    if (typeof size === "undefined") {
        size = "SIZE_SAME";
    }
    $.ajax({
        url: `http://localhost:8080/orders/place/${articleNumber}/${size}/${orderType}/${userId}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(barcodes),
        success: function () {
            refreshOrders();
        }
    });
}

function performActionOnOrders() {
    let actionType = $('#select-action-on-orders').val();
    let orderIds = new Array;
    $('#table-of-orders-body').find('input[type="checkbox"]:checked').each(function () {
        let orderId = parseInt($(this).closest('tr').find('.cell-order-id').text());
        orderIds.push(orderId);
    });
    $.ajax({
        url: `http://localhost:8080/orders/action/${actionType}/${userId}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(orderIds),
        success: function (clothOrders) {
            console.log(clothOrders);
            displayOrders(clothOrders);
        }
    });
}

function updateClothes() {
    $.ajax({
        url: `http://localhost:8080/scrap/update-clothes/${boxId}/${userId}`,
        method: "get",
        success: function () {
            location.reload();
        }
    })
}

function reloadBox() {
    $.ajax({
        url: `http://localhost:8080/boxes/${boxId}`,
        method: "get",
        success: function (box) {
            lockerNumber = box.locker.lockerNumber;
            boxNumber = box.boxNumber;
            boxStatus = box.boxStatus;
            employee = box.employee;
            clothes = employee.clothes;
            clothOrders = employee.clothOrders;
            lastName = employee.lastName;
            firstName = employee.firstName;
            employeeId = employee.id;

            console.log(employee);

            $("#employee").text(lockerNumber + "/" + boxNumber
                + " " + lastName + " " + firstName);

            let beforeRelease = extractClothes("BEFORE_RELEASE", clothes);
            let inRotation = extractClothes("IN_ROTATION", clothes);
            let accepted = extractClothes("ACCEPTED", clothes);
            let withdrawn = extractClothes("WITHDRAWN", clothes);
            let activeOrders = extractActiveOrders(clothOrders);

            displayClothes(inRotation);
            displayAcceptedClothes(accepted);
            displayWithdrawnClothes(withdrawn);
            displayOrders(activeOrders);
        }
    })
}

function loadEmployee() {
    $.ajax({
        url: `http://localhost:8080/scrap/load-employee/${boxId}`,
        method: "get",
        success: function (box) {
            console.log(box);
            location.reload();
        }
    })
};

function displayClothes(clothes) {
    writeClothesToTable($("#table-of-clothes-body"), clothes);
}

function displayRotationalClothes(clothes) {
    writeClothesToTable($("#table-of-rotational-clothes-body"), clothes);
}

function displayAcceptedClothes(clothes) {
    writeClothesToTable($("#table-of-accepted-clothes-body"), clothes);
}

function displayWithdrawnClothes(clothes) {
    writeClothesToTable($("#table-of-withdrawn-clothes-body"), clothes);
}

function displayOrders(clothOrders) {
    writeOrdersToTable($("#table-of-orders-body"), clothOrders);
}

