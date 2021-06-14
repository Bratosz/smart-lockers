loadDepartments(clientId, $('#select-department'));
loadLocations(clientId, $('#select-location'));
loadPlants(clientId, $('#select-plant'));

loadLockers(clientId);

$("#button-filter-lockers").click(function () {
    filterLockers()
});

function loadLockers(clientId) {
    $.ajax({
        url: getLockersByClient(clientId),
        method: 'get',
        success: function (lockers) {
            writeLockersWithSortingToTable(
                lockers,
                $('#table-of-lockers'),
                writeLockerToRowWithViewButton)
        }
    })
}

function filterLockers() {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-location").val();
    $.ajax({
        url: getLockersFiltered(plantId, departmentId, locationId),
        method: 'get',
        success: function (lockers) {
            console.log(lockers);
            writeLockersWithSortingToTable(
                lockers,
                $('#table-of-lockers'),
                writeLockerToRowWithViewButton);
        }
    });
}



