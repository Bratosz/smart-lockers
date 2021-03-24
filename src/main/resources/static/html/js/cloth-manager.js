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
    $row.find(".cell-assignment-date").text(formatDate(cloth.assignment));
    $row.find(".cell-id-bar-code").text(cloth.barCode);
    $row.find(".cell-release-date").text(formatDate(cloth.releaseDate));
    $row.find(".cell-washing-date").text(formatDate(cloth.lastWashing));
    return $row;
}

function formatDate(date) {
    date = date.substring(0, 10);
    if (date == "1970-01-01") {
        return "";
    } else {
        return date;
    }
}

function sortClothesByArticleNumberAndOrdinalNumber(clothes) {
    clothes.sort(function(a,b) {
        return a.article.articleNumber - b.article.articleNumber || a.ordinalNumber - b.ordinalNumber;
    });
    return clothes;
}

function getRowTemplate($table) {
    return $table.find("tr:nth-child(1)");
}

function removeTableRows($table) {
    $table.find("tr:not(tr:nth-child(1))").remove();
    return $table;
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
