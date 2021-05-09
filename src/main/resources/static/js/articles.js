loadArticles(clientId);

$('#button-set-depreciation-period').click(function () {
    let period = $('#select-depreciation-period').val();
    $.ajax({
        url: getActualLocation() + `/client-articles/set-depreciation-period/for-all/` +
            `/${period}` +
            `/${clientId}`,
        method: "post",
        success: function (clientArticles) {
            writeArticlesToTable(clientArticles);
        }
    })
});

$('#button-set-depreciation-cap').click(function () {
    let percentageCap = $('input[name="percentage-cap"]:checked').val();
    $.ajax({
        url: getActualLocation() + `/client-articles/set-percentage-cap/for-all/` +
            `/${percentageCap}` +
            `/${clientId}`,
        method: "post",
        success: function(clientArticles) {
            // console.log(clientArticles);
            writeArticlesToTable(clientArticles);
        }
    })
});

function loadArticles(clientId) {
    $.ajax({
        url: getActualLocation() + `/client-articles` +
            `/${clientId}`,
        method: "get",
        success: function (clientArticles) {
            console.log(clientArticles);
            writeArticlesToTable(clientArticles);
        }
    })
}

function writeArticlesToTable(clientArticles) {
    writeDataToTable(
        sort(clientArticles,
            'article.clothType',
            'rticle.number'),
        $('#table-of-articles'),
        writeArticleToRow);
}

function writeArticleToRow(clientArticle, $row) {
    let article = clientArticle.article;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').text(clientArticle.id);
    $row.find('.cell-article-number').text(article.number);
    $row.find('.cell-article-name').text(article.name);
    $row.find('.cell-cloth-type').text(article.clothType);
    $row.find('.cell-depreciation-period').text(clientArticle.depreciationPeriod);
    $row.find('.cell-depreciation-cap').text(clientArticle.depreciationPercentageCap + "%");
    $row.find('.cell-article-redemption-price').text(clientArticle.redemptionPrice);
    $row.find('.button-save').click(function () {
        let newPrice = $row.find('.input-new-redemption-price').val();
        $.ajax({
                url: getActualLocation() + `/client-articles/update-price` +
                    `/${newPrice}` +
                    `/${clientArticle.id}`,
                method: "post",
                success: function (actualArticle) {
                    refreshRow(actualArticle, $row, writeArticleToRow);
                }
            },
        )
    });
    return $row;
}