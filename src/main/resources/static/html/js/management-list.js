loadManagementList(userId);

function loadManagementList(userId) {
    $.ajax({
        url: getActualLocation() + `/users/management-list` +
            `/${userId}`,
        method: "get",
        success: function (managementList) {
            console.log(managementList);
            writeArticlesToTable(managementList);
        }
    })
}

function writeArticlesToTable(managementList) {
    writeDataToTable(
        sort(managementList.employees,
            'plant.plantNumber',
            'locker.lockerNumber',
            'box.boxNumber'),
        $('#table-of-employees'),
        writeEmployeeToRow);
}

function writeEmployeeToRow(employee, $row) {
    let box = employee.box;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').text(employee.id);
    $row.find('.cell-first-name').text(employee.firstName);
    $row.find('.cell-last-name').text(employee.lastName);
    $row.find('.cell-locker').text(box.locker.lockerNumber);
    $row.find('.cell-box-number').text(box.boxNumber);
    $row.find('.cell-plant-number').text(box.locker.plant.plantNumber);
    $row.find('.cell-department').text(box.locker.department.name);
    $row.find('.cell-location').text(box.locker.location.name);
    $row.find('.cell-redemption-price').text(employee.redemptionPrice);
    return $row;
}