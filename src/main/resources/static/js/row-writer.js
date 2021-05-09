
function writeLockerForManage(locker, $row) {
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

function writeLockerForView(locker, $row) {
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