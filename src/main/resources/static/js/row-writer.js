
function writeLockerToRow(locker, $row) {
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-locker-number').text(locker.lockerNumber);
    $row.find('.cell-capacity').text(locker.capacity);
    $row.find('.cell-plant-number').text(locker.plant.plantNumber);
    $row.find('.cell-department').text(locker.department.name);
    $row.find('.cell-location').text(locker.location.name);
    $row.find('.cell-id').text(locker.id);
    return $row;
}

function writeLockerToRowWithViewButton(locker, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-id").text(locker.id);
    $row.find(".cell-locker-number").text(locker.lockerNumber);
    $row.find(".cell-locker-number").css("font-weight", "700");
    $row.find(".cell-capacity").text(locker.capacity);
    $row.find(".cell-plant-number").text(locker.plant.plantNumber);
    $row.find(".cell-department").text(locker.department.name);
    $row.find(".cell-location").text(locker.location.name);
    $row.find(".button-view-locker").click(function () {
        window.location.href = `view-locker.html?id=${locker.id}`;
    });
    return $row;
}

function writeBoxToRow(box, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-id").text(box.id);
    $row.find(".cell-last-name").text(box.employee.lastName);
    $row.find(".cell-first-name").text(box.employee.firstName);
    $row.find(".cell-locker-number").text(box.locker.lockerNumber);
    $row.find(".cell-locker-number").css("font-weight", "700");
    $row.find(".cell-box-number").text(box.boxNumber);
    $row.find(".cell-box-number").css("font-weight", "700");
    $row.find(".cell-plant-number").text(box.locker.plant.plantNumber);
    $row.find(".cell-department").text(box.locker.department.name);
    $row.find(".cell-location").text(box.locker.location.name);
    $row.find(".cell-status").text(box.boxStatus);
    if(box.boxStatus == "Zajęta") {
        $row.find(".button-view-employee").css("display", "table-cell");
        $row.find(".button-view-employee").click(function () {
            window.location.href = `view-employee.html?id=${box.employee.id}`;
        });
    } else {
        $row.find(".button-add-employee").css("display", "table-cell");
        $row.find(".button-add-employee").click(function () {
            window.location.href = `add-employee.html?id=${box.id}`;
        });
    }
    // $row.find(".button-view-employee").css("display", "table-cell");
    $row.find(".button-view-employee").click(function () {
        window.location.href = `view-employee.html?id=${box.id}`;
    });
    return $row;
}