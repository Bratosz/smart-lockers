function reloadTableRows() {
    $.ajax({
        url: `http://localhost:8080/lockers/${clientId}`,
        method: "get",
        success: function (lockers) {
            $("#table-rows > tr:not(#row-template)").remove();
            const $rowTemplate = $("#row-template");
            console.log($rowTemplate);
            console.log(lockers);
            displayLockers(lockers);
        }
    });
}

function dismissEmployee(employeeId) {
    $.ajax({
        url: `http://localhost:8080/employees/dismiss_by_id/${employeeId}`,
        method: "post",
        success: function (box) {
            console.log("Zwolniono pracownika");
            console.log(box);
            reloadTableRows();
        }
    })
}

$("#button-filter").click(function () {
    let plantId = $("#select-plant").val();
    let departmentId = $("#select-department").val();
    let locationId = $("#select-locker-location").val();
    let boxStatus = $("#select-box-status").val();
    $.ajax({
        url: `http://localhost:8080/lockers/filter/${plantId}/${departmentId}/${locationId}/${boxStatus}`,
        method: "get",
        success: function (lockers) {
            displayLockers(lockers);
        }
    })
});


$("#button-input-lastname").click(function () {
    const lastname = $("#input-lastname").val();
    $.ajax({
        url: `http://localhost:8080/employees/find/${lastname}`,
        method: "get",
        success: function (employees) {
            console.log(employees);
            displayEmployees(employees);
        }
    })
});

$("#button-input-first-name").click(function () {
    let firstName = $("#input-first-name").val();
    $.ajax({
        url: `http://localhost:8080/employees/find_by_first_name/${firstName}`,
        method: "get",
        success: function(employees) {
            console.log(employees);
            displayEmployees(employees);
        }
    })
});

function displayLockers(lockers){
    $("#table-rows > tr:not(#row-template)").remove();
    const $rowTemplate = $("#row-template");
    console.log($rowTemplate);
    for (let i = 0; i < lockers.length; i++) {
        let locker = lockers[i];
        let boxes = locker.boxes.sort(function (a,b) {
            return a.boxNumber - b.boxNumber;
        });
        for (let j = 0; j < boxes.length; j++) {
            let box = boxes[j];
            let employee= box.employee;
            const $row = $rowTemplate.clone();
            $row.removeAttr("id");
            $row.css("display", "table-row");
            $row.find(".cell-id").text(employee.id);
            $row.find(".cell-first-name").text(employee.firstName);
            $row.find(".cell-last-name").text(employee.lastName);
            $row.find(".cell-locker").text(locker.lockerNumber);
            $row.find(".cell-box-number").text(box.boxNumber);
            $row.find(".cell-plant-number").text(locker.plant.plantNumber);
            $row.find(".cell-department").text(locker.department.name);
            $row.find(".cell-location").text(locker.location.name);
            $row.find(".cell-status").text(box.boxStatus);
            $row.find(".button-view-employee").click(function () {
                window.location.href = `view-employee.html?id=${locker.boxes[j].id}`;
            });
            $row.find(".button-dismiss-employee").click(function() {
                dismissEmployee(employee.id);
            });

            $("#table-rows").append($row);
        }
    }
}

function displayEmployees(employees) {
    $("#table-rows > tr:not(#row-template)").remove();
    const $rowTemplate = $("#row-template");
    console.log($rowTemplate);
    console.log(employees);
    for (let i = 0; i < employees.length; i++) {
            let employee = employees[i];
            let box = employee.box;
            let locker = box.locker;
            let $row = $rowTemplate.clone();
            $row.removeAttr("id");
            $row.css("display", "table-row");
            $row.find(".cell-id").text(employee.id);
            $row.find(".cell-first-name").text(employee.firstName);
            $row.find(".cell-last-name").text(employee.lastName);
            $row.find(".cell-locker").text(locker.lockerNumber);
            $row.find(".cell-box-number").text(box.boxNumber);
            $row.find(".cell-plant-number").text(locker.plant.plantNumber);
            $row.find(".cell-department").text(locker.department.name);
            $row.find(".cell-location").text(locker.location.name);
            $row.find(".cell-status").text(box.boxStatus);
            $row.find(".button-view-employee").click(function () {
                window.location.href = `view-employee.html?id=${box.id}`;
            });
            $row.find(".button-dismiss-employee").click(function() {
                console.log("działa");
                dismissEmployee(employee.id);
            });
            $("#table-rows").append($row);
    }
}


loadPlants();
loadDepartments();
loadLocations();
reloadTableRows();




