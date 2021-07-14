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

function displayEmployee(employee) {
    let box = employee.box,
        lockerNumber = box.locker.lockerNumber,
        boxNumber = box.boxNumber,
        boxStatus = box.boxStatus,
        clothes = employee.clothes,
        clothOrders = employee.clothOrders,
        lastName = employee.lastName,
        firstName = employee.firstName;

    $("#employee").text(lockerNumber + "/" + boxNumber
        + " " + lastName + " " + firstName);

    let beforeRelease = extractClothes("BEFORE_RELEASE", clothes);
    let inRotation = extractClothes("IN_ROTATION", clothes);
    let accepted = extractClothes("ACCEPTED", clothes);
    let withdrawn = extractClothes("WITHDRAWN", clothes);
    let activeOrders = extractActiveOrders(clothOrders);

    displayClothes(inRotation);
    displayAcceptedClothes(accepted);
    displayWithdrawnClothes(withdrawn);
    displayOrders(activeOrders);
}