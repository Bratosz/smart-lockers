function formatDateDMY(date) {
    if(date == null) {
        return "";
    }
    date = date.substring(0, 10);
    if (date == "1970-01-01") {
        return "";
    } else {
        return date;
    }
}

function getActualLocation() {
    return window.location.origin;
}

function addFieldToObjects(fieldName, content, objects) {
    let result = [];
    for(let o of objects) {
        o[fieldName] = content;
        result.push(o);
    }
    return result;
}

function sort(collection, property) {
    collection.sort(function (a,b) {
        return a[property] - b[property];
    });
    return collection
}