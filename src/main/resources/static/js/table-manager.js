function getRowTemplate($table) {
    return $table.find("tr:nth-child(1)");
}

function removeTableRows($table) {
    $table.find("tr:not(tr:nth-child(1))").remove();
    return $table;
}

function writeDataToTable(sortedElements, $table, writingMethod) {
    removeTableRows($table);
    const $rowTemplate = getRowTemplate($table);
    for (let e of sortedElements) {
        let $row = $rowTemplate.clone();
        $row = writingMethod(e, $row);
        $table.append($row);
    }
    return $table;
}

function refreshRow(element, $row, writingMethod) {
    writingMethod(element, $row);
}

function writeLockersWithSortingToTable(lockers, $table, writingMethod) {
    writeDataToTable(
        sort(lockers,
            'plant.plantNumber',
            'lockerNumber'),
        $table,
        writingMethod);
}

