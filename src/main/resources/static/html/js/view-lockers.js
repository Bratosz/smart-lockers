loadPlants(clientId);
loadDepartments(clientId);
loadLocations(clientId);
reloadLockers(clientId);

$("#button-filter-lockers").click(function () {
    filterLockers()
});

function reloadLockers(clientId) {
    $.ajax({
        url: `http://localhost:8080/lockers/${clientId}`,
        method: "get",
        success: function (lockers) {
            displayLockers(lockers);
        }
    })
}

function displayLockers(lockers) {
    removeActualTableRowsLockers();
    let $rowTemplate = $("#row-template-lockers");
    console.log(lockers);
    for(let i = 0; i < lockers.length; i++) {
        let locker = lockers[i];
        let $row = $rowTemplate.clone();
        $row.removeAttr("id");
        $row.css("display", "table-row");
        $row.find(".cell-id").text(locker.id);
        $row.find(".cell-locker-number").text(locker.lockerNumber);
        $row.find(".cell-locker-number").css("font-weight", "700");
        $row.find(".cell-capacity").text(locker.capacity);
        $row.find(".cell-plant-number").text(locker.plant.plantNumber);
        $row.find(".cell-department").text(locker.department.name);
        $row.find(".cell-location").text(locker.location.name);
        $row.find(".button-view-locker").click(function () {
            window.location.href = `view-locker.html?id=${locker.id}`;
        });
        $("#table-rows-lockers").append($row);
    }
}

function filterLockers() {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${plantId}/${departmentId}/${locationId}`,
        method: "get",
        success: function (lockers) {
            displayLockers(lockers);
        }
    })
}



