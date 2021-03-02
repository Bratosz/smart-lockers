const url = new URL(window.location.href);
const boxId = url.searchParams.get("id");
let lockerNumber;
let boxNumber;
let lastName;
let firstName;
let employeeId;
let boxStatus;
const userId = 1;

reloadPage();
loadDepartments();

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

function reloadPage() {
    $.ajax({
        url: `http://localhost:8080/boxes/${boxId}`,
        method: "get",
        success: function (box) {
            lockerNumber = box.locker.lockerNumber;
            boxNumber = box.boxNumber;
            boxStatus = box.boxStatus;
            employee = box.employee;
            lastName = employee.lastName;
            firstName = employee.firstName;
            employeeId = employee.id;

            $("#employee").text(lockerNumber + "/" + boxNumber
                + " " + firstName + " " + lastName);

            displayClothes(box.employee.clothes);
            console.log(employee.acceptedClothes);
            displayAcceptedClothes(employee.acceptedClothes);
            displayOrders(employee.clothOrders);
        }
    })
}

function loadEmployee() {
    let departmentId = $('#select-department').val();
    if (departmentId == 0) {
        alert("Nie wybrano oddziału.")
    } else if (boxStatus == "Zajęta") {
        alert("Pracownik został już wczytany.")
    } else {
        $.ajax({
            url: `http://localhost:8080/scrap/load-employee/${departmentId}/${boxId}`,
            method: "get",
            success: function (box) {
                console.log(box);
                location.reload();
            }
        })
    }
};

function displayClothes(clothes) {
    $("#table-of-clothes-body > tr:not(#row-template)").remove();
    const $rowTemplate = $("#row-template");
    console.log("table of clothes");
    console.log($rowTemplate);
    console.log(clothes);
    clothes.sort(function (a, b) {
        return a.article.articleNumber - b.article.articleNumber || a.ordinalNumber - b.ordinalNumber;
    });
    for (let i = 0; i < clothes.length; i++) {
        let cloth = clothes[i];
        let assignmentDate = formatDate(cloth.assignment);
        let releaseDate = formatDate(cloth.releaseDate);
        let lastWashingDate = formatDate(cloth.lastWashing);
        const $row = $rowTemplate.clone();
        $row.removeAttr("id");
        $row.css("display", "table-row");
        $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
        $row.find(".cell-article-number").text(cloth.article.articleNumber);
        $row.find(".cell-article-name").text(cloth.article.name);
        $row.find(".cell-size").text(cloth.size);
        $row.find(".cell-assignment-date").text(assignmentDate);
        $row.find(".cell-id-bar-code").text(cloth.barCode);
        $row.find(".cell-release-date").text(releaseDate);
        $row.find(".cell-washing-date").text(lastWashingDate);
        $("#table-of-clothes-body").append($row);
    }
}

function displayAcceptedClothes(acceptedClothes) {
    $("#table-of-accepted-clothes-body > tr:not(#row-template-accepted-clothes)").remove();
    let $rowTemplate = $("#row-template-accepted-clothes");
    if (acceptedClothes !== undefined) {
        acceptedClothes.sort(function (a, b) {
            return a.article.articleNumber - b.article.articleNumber || a.ordinalNumber - b.ordinalNumber;
        });
        for (let i = 0; i < acceptedClothes.length; i++) {
            let cloth = acceptedClothes[i];
            console.log(cloth);
            let $row = $rowTemplate.clone();
            let acceptedDate = formatDate(cloth.acceptedForExchangeDate);
            $row.css("display", "table-row");
            $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
            $row.find(".cell-article-number").text(cloth.article.articleNumber);
            $row.find(".cell-article-name").text(cloth.article.name);
            $row.find(".cell-size").text(cloth.size);
            $row.find(".cell-id-bar-code").text(cloth.id);
            $row.find(".cell-accepted-for-exchange-date").text(acceptedDate);
            $("#table-of-accepted-clothes-body").append($row);
        }
    }
}

function formatDate(date) {
    date = date.substring(0, 10);
    if (date == "1970-01-01") {
        return "";
    } else {
        return date;
    }
}

function displayOrders(clothOrders) {
    $("#table-of-orders-body > tr:not(#row-order-template)").remove();
    const $rowTemplate = $("#row-order-template");
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

