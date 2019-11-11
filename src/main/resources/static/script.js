function reloadTableRows() {
    $.ajax({
        url: "http://localhost:8080/lockers",
        method: "get",
        success: function (lockers) {
            $("#table-rows > tr:not(#row-template)").remove();
            const $rowTemplate = $("#row-template");
            console.log($rowTemplate);
            console.log(lockers);
            for (let i = 0; i < lockers.length; i++) {
                for (let j = 0; j < lockers[i].boxes.length; j++) {
                const locker = lockers[i];
                const $row = $rowTemplate.clone();
                    $row.removeAttr("id");
                    $row.css("display", "table-row");
                    $row.find(".cell-id").text(locker.boxes[j].employee.id);
                    $row.find(".cell-first-name").text(locker.boxes[j].employee.firstName);
                    $row.find(".cell-last-name").text(locker.boxes[j].employee.lastName);
                    $row.find(".cell-locker").text(locker.lockerNumber);
                    $row.find(".cell-box-number").text(locker.boxes[j].boxNumber);
                    $row.find(".cell-department-number").text(locker.departmentNumber);
                    $row.find(".cell-department").text(locker.department);
                    $row.find(".cell-location").text(locker.location);
                    $row.find(".cell-status").text(locker.boxes[j].boxStatus);
                    $row.find(".button-edit-employee").click(function () {
                        window.location.href = `edit-employee.html?id=${locker.boxes[j].id}`;
                    });
                    $("#table-rows").append($row);

                }
            }
        }
    });
}



$("#button-filter").click(function () {
    const departmentNo = $("#select-department-number").val();
    const department = $("#select-department").val();
    const location = $("#select-location").val();
    const boxStatus = $("#select-box-status").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${departmentNo}/${department}/${location}/${boxStatus}`,
        method: "get",
        success: function (lockers) {
            $("#table-rows > tr:not(#row-template)").remove();
            const $rowTemplate = $("#row-template");
            console.log($rowTemplate);
            console.log(lockers);
            let counter = 0;
            for (let i = 0; i < lockers.length; i++) {
                for (let j = 0; j < lockers[i].boxes.length; j++) {
                    const locker = lockers[i];
                    const $row = $rowTemplate.clone();
                    const employeeId = locker.boxes[j].employee.id;
                    counter++;
                    $row.removeAttr("id");
                    $row.css("display", "table-row");
                    $row.find(".cell-id").text(employeeId);
                    $row.find(".cell-first-name").text(locker.boxes[j].employee.firstName);
                    $row.find(".cell-last-name").text(locker.boxes[j].employee.lastName);
                    $row.find(".cell-locker").text(locker.lockerNumber);
                    $row.find(".cell-box-number").text(locker.boxes[j].boxNumber);
                    $row.find(".cell-department-number").text(locker.departmentNumber);
                    $row.find(".cell-department").text(locker.department);
                    $row.find(".cell-location").text(locker.location);
                    $row.find(".cell-status").text(locker.boxes[j].boxStatus);
                    $row.find(".cell-number").text(counter);
                    $row.find(".button-edit-employee").click(function () {
                        window.location.href = `edit-employee.html?id=${employeeId}`;
                    });
                    $("#table-rows").append($row);
                }
            }

        }
    })
});

$("#button-input-lastname").click(function () {
    const lastname = $("#input-lastname").val();
    $.ajax({
        url: `http://localhost:8080/employees/find/${lastname}`,
        method: "get",
        success: function(employees){
            $("#table-rows > tr:not(#row-template)").remove();
            const $rowTemplate = $("#row-template");
            console.log($rowTemplate);
            console.log(employees);
            for (let i = 0; i < employees.length; i++) {
                for (let j = 0; j < employees[i].boxes.length; j++) {
                    const employee = employees[i];
                    const box = employee.boxes[j];
                    const locker = box.locker;
                    const $row = $rowTemplate.clone();
                    $row.removeAttr("id");
                    $row.css("display", "table-row");
                    $row.find(".cell-id").text(employee.id);
                    $row.find(".cell-first-name").text(employee.firstName);
                    $row.find(".cell-last-name").text(employee.lastName);
                    $row.find(".cell-locker").text(locker.lockerNumber);
                    $row.find(".cell-box-number").text(box.boxNumber);
                    $row.find(".cell-department-number").text(locker.departmentNumber);
                    $row.find(".cell-department").text(locker.department);
                    $row.find(".cell-location").text(locker.location);
                    $row.find(".cell-status").text(box.boxStatus);
                    $row.find(".button-edit-employee").click(function () {
                        window.location.href = "edit-employee.html";
                    });

                    $("#table-rows").append($row);
                }
            }
        }
    })
});



reloadTableRows();



