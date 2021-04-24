loadArticles(clientId);

function loadArticles(clientId) {
    $.ajax({
        url: getActualLocation()
        + `/client-articles/${clientId}`,
        method: "get",
        success: function (clientArticles) {
            console.log(clientArticles);
            writeDataToTable(
                sort(clientArticles,
                    'article.clothType',
                    'article.number'),
                $('#table-of-articles'),
                writeArticleToRow);
        }
    })
}

function writeArticleToRow(clientArticle, $row) {
    let article = clientArticle.article;
    $row.removeAttr('id');
    $row.css('display', 'table-row');
    $row.find('.cell-id').text(clientArticle.id);
    $row.find('.cell-article-number').text(article.number);
    $row.find('.cell-article-name').text(article.name);
    $row.find('.cell-article-cloth-type').text(article.clothType);
    $row.find('.cell-article-redemption-price').text(clientArticle.redemptionPrice);
    return $row;
}