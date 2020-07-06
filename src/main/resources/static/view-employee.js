const url = new URL(window.location.href);
const boxId = url.searchParams.get("id");
let lockerNumber;
let boxNumber;
let lastName;
let departmentNumber;
let employeeId;

function reloadEmployee() {
    $.ajax({
        url: `http://localhost:8080/boxes/${boxId}`,
        method: "get",
        success: function (box) {
            console.log(box);
            lockerNumber = box.locker.lockerNumber;
            boxNumber = box.boxNumber;
            lastName = box.employee.lastName;
            employeeId = box.employee.id;

            $("#employee").text(box.locker.lockerNumber + "/" + box.boxNumber
            + " " + box.employee.firstName + " " + box.employee.lastName);

           displayClothes(box);

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
        return a.articleNo - b.articleNo || a.ordinalNo - b.ordinalNo;
    });
    for(let i = 0; i < clothes.length; i++) {
        const cloth = clothes[i];
        if(cloth.isActive == false) {
            continue;
        }
        const $row = $rowTemplate.clone();
        $row.css("display", "table-row");
        $row.find(".cell-ordinal-number").text(cloth.ordinalNo);
        $row.find(".cell-article-number").text(cloth.articleNo);
        $row.find(".cell-article-name").text(cloth.name);
        $row.find(".cell-size").text(cloth.size);
        $row.find(".cell-assignment-date").text(cloth.assignment.substring(0,10));
        $row.find(".cell-id-bar-code").text(cloth.id);
        $row.find(".cell-release-date").text(cloth.releaseDate.substring(0,10));
        $row.find(".cell-washing-date").text(cloth.lastWashing.substring(0,10));
        $("#table-of-clothes-body").append($row);
    }

}

// var rows = $( client_table.$('input[type="checkbox"]').map(function () {
//     return $(this).closest('tr');
// } ) );

$('#button-confirm-order').click(function () {
    $('#table-of-clothes-body').find('input[type="checkbox"]:checked').each(function () {
        let row = $(this).closest('tr').find('.cell-id-bar-code').text();
        console.log(row);
    });
});

reloadEmployee();