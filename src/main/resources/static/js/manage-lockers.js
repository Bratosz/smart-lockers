loadDepartments($('#select-department'), clientId);
loadDepartments($('#select-department-for-change'), clientId);
loadLocations($('#select-location'), clientId);
loadLocations($('#select-location-for-change'), clientId);
loadPlants($('#select-plant'), clientId);

loadLockers(clientId);

$('#button-filter').click(function () {
    let plantId = $('#select-plant').val();
    let departmentId = $('#select-department').val();
    let locationId = $('#select-location').val();

    $.ajax({
        url: getActualLocation() + `/lockers/filter` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}`,
        method: 'get',
        success: function (lockers) {
            writeLockersToTable(lockers);
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
                writeLockerForManage);
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
                        writeLockerForManage);
                }
            })
        }
    })
}





