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
    for(let e of sortedElements) {
        let $row = $rowTemplate.clone();
        $row = writingMethod(e, $row);
        $table.append($row);
    }
    return $table;
}

function writeArticleToRow(article, $row) {
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').val();
    $row.find('.cell-article-number').val();
    $row.find('.cell-article-name').val();
    $row.find('.cell-article-cloth-type').val();
    $row.find('.cell-article-redemption-price').val();
}