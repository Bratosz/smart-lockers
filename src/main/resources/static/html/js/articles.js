loadArticles(clientId);

function loadArticles(clientId) {
    $.ajax({
        url: getActualLocation()
        + `/client-articles/get/${cilentId}`,
        method: "get",
        success: function (articles) {
            writeDataToTable(
                sort(articles, "articleNumber"),
                $("#table-of-articles"),
                writeArticleToRow);
        }
    })
}