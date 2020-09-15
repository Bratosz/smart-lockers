const url = new URL(window.location.href);
const boxId = url.searchParams.get("id");
let lockerNumber;
let boxNumber;
let lastName;
let employeeId;
const clientId = 1;
const userId = 1;

function reloadEmployee() {
    $.ajax({
        url: `http://localhost:8080/boxes/${boxId}`,
        method: "get",
        success: function (box) {
            console.log(box);
            lockerNumber = box.locker.lockerNumber;
            boxNumber = box.boxNumber;
            employee = box.employee;
            lastName = employee.lastName;
            employeeId = employee.id;

            $("#employee").text(box.locker.lockerNumber + "/" + box.boxNumber
            + " " + box.employee.firstName + " " + box.employee.lastName);

           displayClothes(box);
           displayOrders(employee.clothOrders);
        }
    })
}

$("#refresh-button").click(function () {
    const table = $("#table-of-clothes > tbody > tr");
    for(let i = 1; i < table.length; i++) {
        table[i].remove();
    }
    console.log(table.length);
    $.ajax({
        url: `http://localhost:8080/scrap/update-clothes/${boxId}`,
        method: "get",
        success: function (box) {
            reloadClothes(box.id);
        }
    })
});

function reloadClothes(boxId) {
    $.ajax({
        url: `http://localhost:8080/boxes/${boxId}`,
        method: "get",
        success: function(box) {
            displayClothes(box);
        }
    });
}

function displayClothes(box) {
    $("#table-of-clothes-body > tr:not(#row-template)").remove();
    const $rowTemplate = $("#row-template");
    console.log($rowTemplate);
    const clothes = box.employee.clothing;
    console.log(clothes);
    clothes.sort(function (a, b) {
        return a.article.articleNumber - b.article.articleNumber || a.ordinalNumber - b.ordinalNumber;
    });
    for(let i = 0; i < clothes.length; i++) {
        const cloth = clothes[i];
        if(cloth.isActive == false) {
            continue;
        }
        const $row = $rowTemplate.clone();
        $row.css("display", "table-row");
        $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
        $row.find(".cell-article-number").text(cloth.article.articleNumber);
        $row.find(".cell-article-name").text(cloth.article.name);
        $row.find(".cell-size").text(cloth.size);
        $row.find(".cell-assignment-date").text(cloth.assignment.substring(0,10));
        $row.find(".cell-id-bar-code").text(cloth.id);
        $row.find(".cell-release-date").text(cloth.releaseDate.substring(0,10));
        $row.find(".cell-washing-date").text(cloth.lastWashing.substring(0,10));
        $("#table-of-clothes-body").append($row);
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
    console.log(clothOrders);
    for(let i = 0; i < clothOrders.length; i++) {
        if(clothOrders[i].isCancelled == true) {
            continue;
        }
        const $row = $rowTemplate.clone();
        let order = clothOrders[i];
        $row.css("display", "table-row");
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

function isValueNullOrFalse(val) {
    return !val;
}

// var rows = $( client_table.$('input[type="checkbox"]').map(function () {
//     return $(this).closest('tr');
// } ) );

$('#button-confirm-action-on-orders').click(function() {
    let actionType = $('#select-action-on-orders').val();
    let orderIds = new Array;
    $('#table-of-orders-body').find('input[type="checkbox"]:checked').each(function () {
        let orderId = parseInt($(this).closest('tr').find('.cell-id-cloth-order').text());
        orderIds.push(orderId);
    });
    $.ajax({
        url: `http://localhost:8080/order/action/${actionType}/${userId}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(orderIds),
        success: function(response) {
            console.log(response);
        }
    });
});

$('#button-confirm-order').click(function () {
    let clothIds = new Array;
    let orderType = $("#select-order-type").val();
    $('#table-of-clothes-body').find('input[type="checkbox"]:checked').each(function () {
        let clothId = parseInt($(this).closest('tr').find('.cell-id-bar-code').text());
        clothIds.push(clothId);
    });
    let articleNumber = $('input[name="cloth"]:checked').val();
    if(typeof articleNumber === "undefined"){
        articleNumber = 0;
    }
    let size = $('#select-shirt-size').val();
    $.ajax({
        url: `http://localhost:8080/order/place/${articleNumber}/${size}/${orderType}/${userId}`,
        method: "post",
        contentType: "application/json",
        data: JSON.stringify(clothIds),
        success: function (response) {
            // $('.answer').html(response);
            console.log(response);
        }
    });
    console.log(clothIds);
    console.log(orderType);
});

reloadEmployee();