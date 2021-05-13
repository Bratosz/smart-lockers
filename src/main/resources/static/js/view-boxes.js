loadPlants(clientId);
loadDepartments(clientId);
loadLocations(clientId);

$("#button-filter").click(function () {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    let boxStatus = $("#select-box-status").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${plantId}/${departmentId}/${locationId}/${boxStatus}`,
        method: 'get',
        success: function (lockers) {
            console.log(lockers);
            loadBoxesFromLockers(lockers)
        }
    })
});

$("#button-input-lastname").click(function () {
    const lastName = $("#input-lastname").val();
    $.ajax({
        url: getActualLocation() + `/employees/find-by-last-name` +
            `/${lastName}` +
            `/${clientId}`,
        method: 'get',
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


function displayEmployees(employees) {
    let boxes = getBoxesFromEmployees(employees);
}




