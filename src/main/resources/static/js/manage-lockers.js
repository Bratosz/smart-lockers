loadDepartments(clientId, $('#select-department'));
loadDepartments(clientId, $('#select-department-for-change'));
loadLocations(clientId, $('#select-location'));
loadLocations(clientId, $('#select-location-for-change'));
loadPlants(clientId, $('#select-plant'));

loadLockers(clientId);

$('#button-filter').click(function () {
    let plantId = $('#select-plant').val();
    let departmentId = $('#select-department').val();
    let locationId = $('#select-location').val();

    $.ajax({
        url: getActualLocation() + `/lockers/get-filtered` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}`,
        method: 'get',
        success: function (lockers) {
            writeLockersWithSortingToTable(
                lockers,
                $('#table-of-lockers'),
                writeLockerToRow);
        }
    })
});

$('#button-change-department-and-location').click(function () {
    let startingLockerNumber = $('#input-starting-locker-number').val();
    let endLockerNumber = $('#input-end-locker-number').val();
    let plantId = $('#select-plant').val();
    let departmentId = $('#select-department-for-change').val();
    let locationId = $('#select-location-for-change').val();

    $.ajax({
        url: getActualLocation() +
            `/lockers/change-department-and-location` +
            `/${startingLockerNumber}` +
            `/${endLockerNumber}` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}`,
        method: 'post',
        success: function (lockers) {
            writeLockersWithSortingToTable(
                lockers,
                $('#table-of-lockers'),
                writeLockerToRow);
        }
    })
});

function loadLockers(clientId) {
    $.ajax({
        url: getPlantsByClient(clientId),
        method: 'get',
        success: function(plants) {
            let plantId = plants[0].id;
            $.ajax({
                url: getLockersByPlant(plantId),
                method: 'get',
                success: function (lockers) {
                    writeLockersWithSortingToTable(
                        lockers,
                        $('#table-of-lockers'),
                        writeLockerToRow);
                }
            })
        }
    })
}





