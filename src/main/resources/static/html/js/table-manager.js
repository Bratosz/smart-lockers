function getRowTemplate($table) {
    return $table.find("tr:nth-child(1)");
}

function removeTableRows($table) {
    $table.find("tr:not(tr:nth-child(1))").remove();
    return $table;
}