loadPlants(clientId);
loadDepartments(clientId);
loadLocations(clientId);

$("#button-add-lockers").click(function () {
    const startingLockerNumber = $("#input-starting-locker-number").val();
    const endingLockerNumber = $("#input-ending-locker-number").val();
    const capacity = $("#select-capacity").val();
    const plantId = $("#select-plant").val();
    const departmentId = $("#select-department").val();
    const locationId = $("#select-location").val();

    $.ajax({
        url: `http://localhost:8080/lockers/create/${startingLockerNumber}/${endingLockerNumber}/${capacity}/${plantId}/${departmentId}/${locationId}`,
        method: "post",
        contentType: "application/json",
        success: function (lockers) {
            console.log(lockers);
        }
    })
});


