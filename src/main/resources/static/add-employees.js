$("#button-add-employee").click(function () {
    const lockerNo = $("#input-locker-number").val();
    const boxNo = $("#input-box-number").val();
    const plantId = $("#select-plant").val();
    const departmentId = $("#select-department").val();
    const firstName = $("#input-first-name").val();
    const lastName = $("#input-last-name").val();

    $.ajax({
        url: `http://localhost:8080/employees/create_employee/${plantId}/${departmentId}/${lockerNo}/${boxNo}/` +
            `${firstName}/${lastName}`,
        method: "post",
        success: function () {
            console.log("Dodano pracownika");
        }
    })
});

loadPlants();
loadDepartments();

