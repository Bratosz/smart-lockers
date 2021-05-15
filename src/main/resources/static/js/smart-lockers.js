let clientId = 1;
const userId = 6;

function loadPlants($selectPlant, clientId) {
    $.ajax({
        url: getPlantsByClient(clientId),
        method: 'get',
        success: function (plants) {
            appendOptionsToSelect(plants, $selectPlant);
        }
    })
}

function getPlantsByClient(clientId) {
    return getActualLocation() + `/plants/get-all/${clientId}`;
}

function getLockersByPlant(plantId) {
    return getActualLocation() + `/lockers/get-by-plant/${plantId}`;
}

function getLockersByClient(clientId) {
    return getActualLocation() + `/lockers/get-by-client/git ${clientId}`;
}

function getLockersFiltered(plantId, departmentId, locationId) {
    return getActualLocation() + `/lockers/get-filtered` +
        `/${plantId}` +
        `/${departmentId}` +
        `/${locationId}`;
}

function loadDepartments($selectDepartment, clientId) {
    $.ajax({
        url: `http://localhost:8080/department/get_all/${clientId}`,
        method: `get`,
        success: function (departments) {
            console.log(departments);
            $selectDepartment.append(createSelectPlaceholder("Wybierz oddział"));
            appendOptionsToSelect(departments, $selectDepartment);
        }
    })
}

function loadLocations($selectLocation, clientId) {
    $.ajax({
        url: `http://localhost:8080/location/get_all/${clientId}`,
        method: `get`,
        success: function (locations) {
            console.log(locations);
            $selectLocation.append(createSelectPlaceholder("Wybierz lokalizację"));
            appendOptionsToSelect(locations, $selectLocation);
        }
    })
}

function removeActualTableRowsLockers() {
    $("#table-rows-lockers > tr:not(#row-template-lockers)").remove();
}

function appendOptionsToSelect(collection, select) {
    for (let i = 0; i < collection.length; i++) {
        let value = collection[i].id;
        let description = collection[i].name;
        let option = createOption(value, description);
        select.append(option);
    }
}

function createOption(value, description) {
    return `<option value="${value}">${description}</option>`;
}

function createSelectPlaceholder(description) {
    return `<option value="0" selected>${description}</option>`;
}

