
function extractActiveOrders(orders) {
    let activeOrders = [];
    for(let order of orders) {
        if(order.active) {
            activeOrders.push(order)
        }
    }
    return activeOrders;
}

function sortOrdersByArticleNumberAndOrdinalNumber(orders) {
    orders.sort(function(a,b) {
        return a.clothToRelease.article.articleNumber
        - b.clothToRelease.article.articleNumber
        || a.clothToRelease.ordinalNumber
        - b.clothToRelease.ordinalNumber
    });
    return orders;
}

function getActualOrderStatus(order) {
    let actualOrderIndex = order.orderStatusHistory.length - 1;
    return order.orderStatusHistory[actualOrderIndex];
}

function writeOrderToRow(order, $row) {
    let actualOrderStatus = getActualOrderStatus(order);
    $row.removeAttr("id");
    $row.css("display", "table-row");
    $row.find(".cell-order-id").text(order.id);
    $row.find(".cell-order-type").text(order.orderType);
    $row.find(".cell-order-status").text(actualOrderStatus.orderStage);
    $row.find(".cell-status-changed-date").text(formatDateDMY(
        actualOrderStatus.dateOfUpdate));
    $row.find(".cell-cloth-to-exchange").text(
        toStringCloth(order.clothToExchange));
    $row.find(".cell-cloth-to-release").text(
        toStringCloth(order.clothToRelease));
    return $row;

}

function iterateOrdersAndWriteInTable(orders, $table) {
    const $rowTemplate = getRowTemplate($table);
    for(let order of orders) {
        let $row = $rowTemplate.clone();
        $row = writeOrderToRow(order, $row);
        $table.append($row);
    }
    return $table;
}

function writeOrdersToTable($table, orders) {
    $table = removeTableRows($table);
    orders =
        sortOrdersByArticleNumberAndOrdinalNumber(orders);
    return iterateOrdersAndWriteInTable(orders, $table);
}