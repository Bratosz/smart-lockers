let clientId = 1;
const userId = 6;

function loadPlants(clientId) {
    $.ajax({
        url: `http://localhost:8080/plant/get_all/${clientId}`,
        method: `get`,
        success: function (plants) {
            console.log(plants);
            let select = $("#select-plant");
            appendOptionsToSelect(plants, select)
        }
    })
}

function loadDepartments(clientId) {
    $.ajax({
        url: `http://localhost:8080/department/get_all/${clientId}`,
        method: `get`,
        success: function (departments) {
            console.log(departments);
            let select = $("#select-department");
            select.append(createSelectPlaceholder("Wybierz oddział"));
            appendOptionsToSelect(departments, select);
        }
    })
}

function loadLocations(clientId) {
    $.ajax({
        url: `http://localhost:8080/location/get_all/${clientId}`,
        method: `get`,
        success: function (locations) {
            console.log(locations);
            let select = $("#select-location");
            select.append(createSelectPlaceholder("Wybierz lokalizację"));
            appendOptionsToSelect(locations, select);
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

