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
    let actualLifeCycleStatus = getActualLifeCycleStatus(cloth);
    if(desiredLifeCycleStatus == actualLifeCycleStatus) {
        return true;
    } else {
        return false;
    }
}

function writeClothToRow(cloth, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
    $row.find(".cell-article-number").text(cloth.article.articleNumber);
    $row.find(".cell-article-name").text(cloth.article.name);
    $row.find(".cell-size").text(cloth.size);
    $row.find(".cell-assignment-date").text(formatDateDMY(cloth.assignment));
    $row.find(".cell-id-bar-code").text(cloth.barCode);
    $row.find(".cell-release-date").text(formatDateDMY(cloth.releaseDate));
    $row.find(".cell-washing-date").text(formatDateDMY(cloth.lastWashing));
    return $row;
}

function sortClothesByArticleNumberAndOrdinalNumber(clothes) {
    clothes.sort(function(a,b) {
        return a.article.articleNumber - b.article.articleNumber
            || a.ordinalNumber - b.ordinalNumber;
    });
    return clothes;
}



function iterateClothesAndWriteInTable(clothes, $table) {
    const $rowTemplate =  getRowTemplate($table);
    for(let cloth of clothes) {
        let $row = $rowTemplate.clone();
        $row = writeClothToRow(cloth, $row);
        $table.append($row);
    }
    return $table;
}

function writeClothesToTable($table, clothes) {
    $table = removeTableRows($table);
    clothes = sortClothesByArticleNumberAndOrdinalNumber(clothes);
    return iterateClothesAndWriteInTable(clothes, $table);
}

function toStringCloth(cloth) {
    return cloth.article.clothType + " " +
        cloth.article.articleNumber + " " +
        "lp. " + cloth.ordinalNumber + " " +
        "rozm.: " + cloth.size;

}


