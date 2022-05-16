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

function extractClientArticles(clothes) {
    let articles = [];
    let articleIds = [];
    let articleId;
    for(let cloth of clothes) {
        articleId = cloth.clientArticle.id;
        if($.inArray(articleId, articleIds) == -1) {
            articleIds.push(articleId);
            articles.push(cloth.clientArticle);
        }
    }
    return articles;
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
    if(desiredLifeCycleStatus == cloth.lifeCycleStatus && cloth.active) {
        return true;
    } else {
        return false;
    }
}

function writeClothToRow(cloth, $row) {
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-ordinal-number").text(cloth.ordinalNumber);
    $row.find(".cell-article-number").text(cloth.clientArticle.article.number);
    $row.find(".cell-article-name").text(cloth.clientArticle.article.name);
    $row.find(".cell-size").text(writeSize(cloth));
    $row.find(".cell-assignment-date").text(formatDateDMY(cloth.assignment));
    $row.find(".cell-barcode").text(cloth.barcode);
    $row.find(".cell-release-date").text(formatDateDMY(
        cloth.releaseDate));
    $row.find(".cell-released-to-employee-date").text(formatDateDMY(
        cloth.releasedToEmployeeAsRotation));
    $row.find(".cell-washing-date").text(formatDateDMY(cloth.lastWashing));
    $row.click(function () {
        let $checkBox = $row.find('.cloth-check-box');
        let checked = $checkBox.prop('checked');
        if(checked) {
            $checkBox.prop('checked', false);
        } else {
            $checkBox.prop('checked', true);
        }
    });
    return $row;
}

function writeSize(cloth) {
    let lengthModification = cloth.lengthModification;
    if(lengthModification == "NONE") {
        return cloth.size;
    } else {}
    return cloth.size + " " + cloth.lengthModification;
}

function sortClothesByArticleNumberAndOrdinalNumber(clothes) {
    clothes.sort(function(a,b) {
        return a.clientArticle.article.number - b.clientArticle.article.number
            || a.ordinalNumber - b.ordinalNumber;
    });
    return clothes;
}



function toStringCloth(cloth) {
    return cloth.clientArticle.article.clothType + " " +
        cloth.clientArticle.article.number + " " +
        "lp. " + cloth.ordinalNumber + " " +
        "rozm.: " + cloth.size;
}

function toStringArticle(clientArticle) {
    return clientArticle.article.name + " " +
        clientArticle.article.number;
}

function getSize(orderType) {
    if(orderType == 'EXCHANGE_FOR_A_NEW_ONE') {
        return 'SIZE_SAME';
    } else {
        return getSizeFromTextInput(
            $('#input-size-for-order-clothes').val());
    }
}

function getSizeFromTextInput(size) {
    if (size == null || size == "" || size == undefined) {
        return "SIZE_SAME";
    } else {
        return size.trim().toUpperCase();
    }
}


function getLengthModification() {
    return getLengthModificationFromInput(
        $('#input-length-modification-for-order-clothes').val()
    );
}

function getLengthModificationFromInput(lengthModification) {
    if(lengthModification == "" || lengthModification == 0) {
        return "NONE";
    } else {
        return lengthModification;
    }
}

function getDesiredClientArticleId() {
    let clientArticleId = 0;
    if(actualOrderType == "CHANGE_ARTICLE" || actualOrderType == 'NEW_ARTICLE') {
        clientArticleId = $('#select-desired-article-for-order-clothes').val();
    }
    return clientArticleId;
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


