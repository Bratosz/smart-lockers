$("#button-add-locker").click(function () {
    const departmentNo = $("#pass-locker-department-no").val();
    $.ajax({
        url: `http://localhost:8080/lockers/quantity/${departmentNo}`,
        method: "get",
        success: function (lockersQuantity) {
            const amount = $("#locker-amount-input").val();
            for(let i = 0; i < amount; i++) {
                const capacity = $("#pass-locker-capacity").val();
                const department = $("#pass-locker-department").val();
                const location = $("#pass-locker-location").val();

                lockersQuantity++;

                const locker = {
                    lockerNumber: lockersQuantity,
                    capacity: capacity,
                    departmentNumber: departmentNo,
                    department: department,
                    location: location
                };

                $.ajax({
                    url: "http://localhost:8080/lockers",
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