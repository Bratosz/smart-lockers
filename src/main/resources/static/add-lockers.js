$("#button-add-locker").click(function () {
    const plantId = $("#select-plant").val();
    $.ajax({
        url: `http://localhost:8080/lockers/quantity/${plantId}`,
        method: "get",
        success: function (lockersQuantity) {
            const amount = $("#input-lockers-amount").val();
            for (let i = 0; i < amount; i++) {
                const capacity = $("#select-locker-capacity").val();
                const departmentId = $("#select-department").val();
                const locationId = $("#select-locker-location").val();

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
