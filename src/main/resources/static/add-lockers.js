$("#button-add-locker").click(function () {
    let plantId = $("#select-plant").val();
    $.ajax({
        url: `http://localhost:8080/lockers/quantity/${plantId}`,
        method: "get",
        success: function (lockersQuantity) {
            const amount = $("#input-lockers-amount").val();
            for (let i = 0; i < amount; i++) {
                let capacity = $("#select-locker-capacity").val();
                let departmentId = $("#select-department").val();
                let locationId = $("#select-locker-location").val();

                lockersQuantity++;

                const locker = {
                    lockerNumber: lockersQuantity,
                    capacity: capacity,
                };

                $.ajax({
                    url: `http://localhost:8080/lockers/create/${plantId}/${departmentId}/${locationId}`,
                    method: "post",
                    data: JSON.stringify(locker),
                    contentType: "application/json",
                    success: function () {
                        console.log(locker);
                    }
                })
            }
        }
    })
});

loadPlants();
loadDepartments();
loadLocations();
