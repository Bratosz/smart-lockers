const url = new URL(window.location.href);
const employeeId = url.searchParams.get("employee-id");
const boxId = url.searchParams.get("box-id");
let employeeMain;


reloadEmployee();

$("#button-load-to-managed-employees").click(function () {
    addToManagedEmployees();
});

$("#refresh-button").click(function () {
        updateEmployee();
});

$("#button-perform-action-on-orders").click(function () {
    performActionOnOrders();
});

$('#button-add-order').click(function () {
    addOrder();

});

function updateEmployee() {
    $.ajax({
        url: getActualLocation() + `/employees/update` +
            `/${employeeId}/${userId}`, // with complete info
        method: 'post',
        success: function(response) {
            console.log(response);
            if(response.succeed) {
                window.alert(response.message)
                reloadEmployee();
            } else {
                window.alert(response.message);
            }
        }
    })
}

function refreshOrders() {
    $.ajax({
        url: `http://localhost:8080/orders/get-by-employee/${employeeId}`,
        method: "get",
        success: function (actualOrders) {
            displayOrders(actualOrders);
        }
    })
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

function reloadEmployee(employee) {
    employeeMain = employee;
    displayEmployee(employeeMain);
}

function reloadEmployee() {
    $.ajax({
        url: getActualLocation() + `/employees/with-complete-info/${employeeId}`,
        method: 'get',
        success: function (employee) {
            console.log(employee);
            employeeMain = employee;
            displayEmployee(employeeMain);
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
}

function addToManagedEmployees() {
    $.ajax({
        url: getActualLocation() + `/users/add-employee-to-management-list` +
            `/${employeeId}` +
            `/${userId}`,
        method: "post",
        success: function (response) {
            window.alert(response.message)
        }
    })
}

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

