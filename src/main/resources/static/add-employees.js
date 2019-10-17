$("#button-add-employee").click(function () {
    const lockerNo = $("#employee-locker-input").val();
    const boxNo = $("#employee-box-input").val();
    const departmentNo = $("#employee-department-number-select").val();

    const firstName = $("#employee-first-name-input").val();
    const lastName = $("#employee-last-name-input").val();
    const department = $("#employee-department-select").val();

    const employee = {
        firstName: firstName,
        lastName: lastName,
        department: department
    };
    $.ajax({
        url: `http://localhost:8080/employees/create_employee/${departmentNo}/${lockerNo}/${boxNo}`,
        method: "post",
        data: JSON.stringify(employee),
        contentType: "application/json",
        success: function () {
            console.log(employee);
        }
    })
});

