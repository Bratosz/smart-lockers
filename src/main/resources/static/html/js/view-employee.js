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
const userId = 1;

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
    let clothIds = new Array;
    let orderType = $("#select-order-type").val();
    $('#table-of-clothes-body').find('input[type="checkbox"]:checked').each(function () {
        let clothId = parseInt($(this).closest('tr').find('.cell-id-bar-code').text());
        clothIds.push(clothId);
    });
    let articleNumber = $('input[name="cloth"]:checked').val();
    if (typeof articleNumber === "undefined") {
        articleNumber = 0;
    }
    let size = $('input[name="size"]:checked').val();
    if (typeof size === "undefined") {
        size = "SIZE_DEFAULT";
    }
    $.ajax({
        url: `http://localhost:8080/orders/place/${articleNumber}/${size}/${orderType}/${userId}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(clothIds),
        success: function () {
            refreshOrders();
        }
    });
}

function performActionOnOrders() {
    let actionType = $('#select-action-on-orders').val();
    let orderIds = new Array;
    $('#table-of-orders-body').find('input[type="checkbox"]:checked').each(function () {
        let orderId = parseInt($(this).closest('tr').find('.cell-id-cloth-order').text());
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
        url: `http://localhost:8080/scrap/update-clothes/${boxId}`,
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

            displayClothes(inRotation);
            displayAcceptedClothes(accepted);
            displayOrders(clothOrders);
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

function displayOrders(clothOrders) {
    writeOrdersToTable($("#table-of-cloth-orders-body"), clothOrders);
    $("#table-of-orders-body > tr:not(#row-template-cloth-order)").remove();
    const $rowTemplate = $("#row-template-cloth-order");
    clothOrders.sort(function (a, b) {
        return a.cloth.article.articleNumber - b.cloth.article.articleNumber
            || a.orderStatus - b.orderStatus
            || a.cloth.ordinalNumber - b.cloth.ordinalNumber;
    });
    for (let i = 0; i < clothOrders.length; i++) {
        if (clothOrders[i].isCancelled == true) {
            continue;
        }
        const $row = $rowTemplate.clone();
        let order = clothOrders[i];
        $row.css("display", "table-row");
        $row.attr("id", "id-row-order-" + i);
        $row.find(".cell-order-type").text(order.orderType);
        $row.find(".cell-order-status").text(order.orderStatus);
        $row.find(".cell-id-cloth-order").text(order.id);
        $row.find(".cell-cloth-to-exchange").text(order.cloth.article.name + " lp. " + order.cloth.ordinalNumber);
        $row.find(".cell-ordered-cloth").text(order.article.name);
        $row.find(".cell-ordered-cloth-size").text(order.size);
        $row.find(".cell-accept-date").text(order.acceptDate);
        $("#table-of-orders-body").append($row);
    }
}

