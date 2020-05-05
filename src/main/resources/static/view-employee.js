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
    $.ajax({
        url: `http://localhost:8080/scrap/update-clothes/${boxId}`,
        method: "get",
        success: function (box) {
            displayClothes(box);
        }
    })
});

function displayClothes(box) {
    $("#table-of-clothes > tr:not(#row-template)").remove();
    const $rowTemplate = $("#row-template");
    console.log($rowTemplate);
    const clothes = box.employee.clothing;
    console.log(clothes);
    for(let i = 0; i < clothes.length; i++) {
        const cloth = clothes[i];
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
        $("#table-of-cloths").append($row);
    }

}

reloadEmployee();