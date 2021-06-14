loadPlants($('#select-plant'), clientId);
loadDepartments($('#select-department'), clientId);
loadLocations($('#select-location'), clientId);

$("#button-filter").click(function () {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    let boxStatus = $("#select-box-status").val();
    $.ajax({
        url: getActualLocation() + `/boxes/get-filtered` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}` +
            `/${boxStatus}`,
        method: "get",
        success: function (boxes) {
            console.log(boxes);
            writeDataToTable(
                boxes,
                $("#table-of-boxes"),
                writeBoxToRow);
        }
    })
});

$("#button-search-by-last-name").click(function () {
    const lastName = $("#input-last-name").val();
    $.ajax({
        url: getActualLocation() + `/boxes/get-by-last-name` +
            `/${lastName}` +
            `/${clientId}`,
        method: "get",
        success: function (boxes) {
            writeDataToTable(
                boxes,
                $("#table-of-boxes"),
                writeBoxToRow);
        }
    })
});

$("#button-get-boxes-by-locker-number").click(function () {
    let plantId = $("#select-plant").val();
    let lockerNumber = $("#input-locker-number").val();
    $.ajax({
        url: getActualLocation() + `/boxes/get-by-locker-number-and-plant` +
            `/${lockerNumber}` +
            `/${plantId}`,
        method: "get",
        success: function (boxes) {
            console.log(boxes);
            writeDataToTable(
                boxes,
                $("#table-of-boxes"),
                writeBoxToRow);
        }
    });
});

$("#button-input-first-name").click(function () {
    let firstName = $("#input-first-name").val();
    $.ajax({
        url: `http://localhost:8080/employees/find_by_first_name/${firstName}`,
        method: "get",
        success: function (employees) {
            console.log(employees);
            displayEmployees(employees);
        }
    })
});

function reloadBoxes(clientId) {
    $.ajax({
        url: getActualLocation() + `/lockers/${clientId}`,
        method: "get",
        success: function (lockers) {
            let boxes = getBoxesFromLockers(lockers);
            writeDataToTable(boxes,
                $("#table-of-boxes"),
                writeBoxToRowWithLockerData);
        }
    })

}

function displayEmployees(employees) {
    let boxes = getBoxesFromEmployees(employees);
}




