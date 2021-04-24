function writeClothesToTable($table, clothes) {
    $table = removeTableRows($table);
    clothes = sortClothesByArticleNumberAndOrdinalNumber(clothes);
    return writeClothesInTable(clothes, $table);
}

function writeClothesInTable(clothes, $table) {
    const $rowTemplate =  getRowTemplate($table);
    for(let cloth of clothes) {
        let $row = $rowTemplate.clone();
        $row = writeClothToRow(cloth, $row);
        $table.append($row);
    }
    return $table;
}

function extractClothes(desiredLifeCycleStatus, clothes) {
    let extractedClothes = [];
    for(let cloth of clothes) {
        if(clothIs(desiredLifeCycleStatus, cloth)) {
            extractedClothes.push(cloth);
        }
    }
    return extractedClothes;
}

function getActualStatus(cloth) {
    let length = cloth.statusHistory.length;
    return cloth.statusHistory[length - 1];
}

function getActualLifeCycleStatus(cloth) {
    let actualStatus = getActualStatus(cloth);
    return actualStatus.status.lifeCycleStatus;
}

function clothIs(desiredLifeCycleStatus, cloth) {
    if(desiredLifeCycleStatus == cloth.lifeCycleStatus) {
        return true;
    } else {
        return false;
    }
}

function writeClothToRow(cloth, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
    $row.find(".cell-article-type-number").text(cloth.article.number);
    $row.find(".cell-article-type-name").text(cloth.article.name);
    $row.find(".cell-size").text(cloth.size);
    $row.find(".cell-assignment-date").text(formatDateDMY(cloth.assignment));
    $row.find(".cell-barcode").text(cloth.barcode);
    $row.find(".cell-release-date").text(formatDateDMY(cloth.releaseDate));
    $row.find(".cell-washing-date").text(formatDateDMY(cloth.lastWashing));
    return $row;
}

function sortClothesByArticleNumberAndOrdinalNumber(clothes) {
    clothes.sort(function(a,b) {
        return a.article.number - b.article.number
            || a.ordinalNumber - b.ordinalNumber;
    });
    return clothes;
}



function toStringCloth(cloth) {
    return cloth.article.clothType + " " +
        cloth.article.articleNumber + " " +
        "lp. " + cloth.ordinalNumber + " " +
        "rozm.: " + cloth.size;

}



function getSizeFromInput() {
     return $('input[name="size"]:checked').val();
}

function getArticleFromInput() {
    return $('input[name="article"]:checked').val();
}

function getOrdinalNumberFromInput() {
    return $('#input-ordinal-number').val();
}

function getAssignmentDateFromInput() {
    let year = $('#select-year').val().toString();
    let month = $('#select-month').val().toString();
    let day = "01";
    return year + "-" + month + "-" + day;
}


