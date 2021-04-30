loadPlants(clientId);
loadDepartments(clientId);
loadLocations(clientId);
reloadBoxes(clientId);

$("#button-filter").click(function () {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    let boxStatus = $("#select-box-status").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${plantId}/${departmentId}/${locationId}/${boxStatus}`,
        method: "get",
        success: function (lockers) {
            console.log(lockers);
            displayBoxes(lockers);
        }
    })
});

$("#button-input-lastname").click(function () {
    const lastName = $("#input-lastname").val();
    $.ajax({
        url: `http://localhost:8080/employees/find/${lastName}`,
        method: "get",
        success: function (employees) {
            console.log(employees);
            displayEmployees(employees);
        }
    })
});

$("#button-get-locker-by-number").click(function () {
    let plantId = $("#select-plant").val();
    let lockerNumber = $("#input-locker-number").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${plantId}/${lockerNumber}`,
        method: "get",
        success: function (lockers) {
            console.log(lockers);
            displayBoxes(lockers);
        }
    })
})

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




