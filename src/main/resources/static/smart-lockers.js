let clientId = 1;
function loadPlants() {
    $.ajax({
        url: `http://localhost:8080/plant/get_all/${clientId}`,
        method: `get`,
        success: function (plants) {
            console.log(plants);
            let select = $("#select-plant");
            // select.append(createSelectPlaceholder("Wybierz zakład"));
            appendOptionsToSelect(plants, select)
        }
    })
}

function loadDepartments() {
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

function loadLocations() {
    $.ajax({
        url: `http://localhost:8080/location/get_all/${clientId}`,
        method: `get`,
        success: function (locations) {
            console.log(locations);
            let select = $("#select-locker-location");
            select.append(createSelectPlaceholder("Wybierz lokalizację"));
            appendOptionsToSelect(locations, select);
        }
    })
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