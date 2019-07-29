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
                    $row.find(".cell-first-name").text(locker.boxes[j].employee.firstName);
                    $row.find(".cell-last-name").text(locker.boxes[j].employee.lastName);
                    $row.find(".cell-locker").text(locker.lockerNumber);
                    $row.find(".cell-box-number").text(locker.boxes[j].boxNumber);
                    $row.find(".cell-department-number").text(locker.departmentNumber);
                    $row.find(".cell-department").text(locker.department);
                    $row.find(".cell-location").text(locker.location);
                    $row.find(".cell-status").text(locker.boxes[j].boxStatus);

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
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${departmentNo}/${department}/${location}`,
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
                    $row.find(".cell-id").text(locker.id);
                    $row.find(".cell-first-name").text(locker.boxes[j].employee.firstName);
                    $row.find(".cell-last-name").text(locker.boxes[j].employee.lastName);
                    $row.find(".cell-locker").text(locker.lockerNumber);
                    $row.find(".cell-box-number").text(locker.boxes[j].boxNumber);
                    $row.find(".cell-department-number").text(locker.departmentNumber);
                    $row.find(".cell-department").text(locker.department);
                    $row.find(".cell-location").text(locker.location);
                    $row.find(".cell-status").text(locker.boxes[j].boxStatus);

                    $("#table-rows").append($row);
                }
            }
        }
    })
});

reloadTableRows();



