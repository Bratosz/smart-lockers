function dismissEmployee(employeeId) {
    $.ajax({
        url: `http://localhost:8080/employees/dismiss-by-id/${employeeId}/${userId}`,
        method: "post",
        success: function (response) {
            alert(response)
            loadLocker();
        }
    })
}

function writeEmployeeInfoToElement(employee, $element) {
    let location,
        plantNumber,
        lockerNumber,
        boxNumber,
        lastName,
        firstName;
    location = employee.box.locker.location.name;
    plantNumber = employee.box.locker.plant.plantNumber;
    lockerNumber = employee.box.locker.lockerNumber;
    boxNumber = employee.box.boxNumber;
    lastName = employee.lastName;
    firstName = employee.firstName;
    $element.text(
        location +
        " " + plantNumber +
        " " + lockerNumber + "/" + boxNumber +
        " " + lastName +
        " " + firstName);
}