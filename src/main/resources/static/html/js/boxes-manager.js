

function writeBoxToRowForLockerView(box, $row){
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-id").text(box.id);
    $row.find(".cell-first-name").text(box.employee.firstName);
    $row.find(".cell-last-name").text(box.employee.lastName);
    $row.find(".cell-box-number").text(box.boxNumber);
    $row.find(".cell-status").text(box.boxStatus);
    $row.find(".button-view-employee").click(function () {
        window.location.href = `view-employee.html?id=${box.id}`});
    $row.find(".button-dismiss-employee").click(function() {
        if(confirm('Czy na pewno chcesz zwolnić pracownika ' +
        box.employee.firstName + ' ' + box.employee.lastName + '?')) {
            dismissEmployee(box.employee.id);
        }
    });
    $row.find(".button-delete-box").click(function() {
        if(confirm('Czy na pewno chcesz usunąć ten box nr ?' + box.boxNumber)) {
            deleteBox(box.id);
        }
    });
    return $row;
}

function writeBoxToRowWithLockerData(box, $row) {
    let employee = box.employee;
    let locker = box.locker;
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
    return $row;
}

function getBoxesFromLockers(lockers) {
    let allBoxes = [];
    lockers = sortLockersByPlantAndNumber(lockers);
    for(let locker of lockers) {
        let boxesToExtend = locker.boxes;
        delete locker.boxes;
        let boxes = addFieldToObjects("locker", locker, boxesToExtend);
        if(allBoxes.length == 0) {
            allBoxes = boxes;
        } else {
            $.merge(allBoxes, boxes);
        }
    }
    console.log(allBoxes);
    return allBoxes;
}

function getBoxesFromEmployees(employees) {
    let boxes = [];
    for(let employee of employees) {
        let boxToExtend = employee.box;
        delete employee.box;
        boxToExtend[employee] = employee;
        boxes.push(boxToExtend);
    }
    console.log(boxes);
}

function sortBoxesByNumber(boxes) {
    boxes.sort(function (a,b) {
        return a.boxNumber - b.boxNumber;
    });
    return boxes;
}

function sortLockersByPlantAndNumber(lockers) {
    lockers.sort(function (a,b) {
        return a.plant.plantNumber - b.plant.plantNumber ||
            a.lockerNumber - b.lockerNumber;
    });
    return lockers;
}

function deleteBox(boxId) {
    $.ajax({
        url: getActualLocation() + `/boxes/delete/${boxId}`,
        method: "delete",
        success: function (response) {
            alert(response.message);
            loadLocker();
        }
    })
}