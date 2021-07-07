const url = new URL(window.location.href);
const employeeId = url.searchParams.get('employee-id');

loadPlants(clientId, $('#select-plant'));
loadDepartments(clientId, $('#select-department'));
loadLocations(clientId, $('#select-location'));

loadPlants(clientId, $('#select-plant-for-assign-by-numbers'));

loadEmployeeInfo();

//PRZENOSZENIE PRACOWNIKA DO INNEJ SZAFKI NA BAZIE PARAMETRÓW I PO NUMERZE SZAFKI

function loadEmployeeInfo() {
    $.ajax({
        url: getActualLocation() +
            `/employees` +
            `/${employeeId}`,
        method: 'get',
        success: function(employee) {
            writeEmployeeInfoToElement(
                employee,
                $('#employee-info'));
        }
    })
}

$('#button-relocate-employee').click(function() {
    let plantId = $('#select-plant').val(),
        departmentId = $('#select-department').val(),
        locationId = $('#select-location').val();
    $.ajax({
        url: getActualLocation() +
            `/employees/relocate` +
            `/${plantId}` +
            `/${departmentId}` +
            `/${locationId}` +
            `/${employeeId}`,
        method: 'post',
        success: function(response) {
            window.alert(response)
        }
    })
})