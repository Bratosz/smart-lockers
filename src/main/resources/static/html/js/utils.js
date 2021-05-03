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

function sort(collection, prop1st, prop2nd, prop3rd) {
    if(prop2nd === undefined && prop3rd === undefined) {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st));
        });
        return collection;
    } else if (prop2nd === undefined) {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd));
        });
        return collection;
    } else {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd))
                || compare(fetchFrom(a, prop3rd), fetchFrom(b, prop3rd));
        });
        return collection;
    }
}

function compare(a,b) {
    if(a < b) return -1;
    if(a > b) return 1;
    return 0;

}

function fetchFrom(obj, prop){
    //property not found
    if(typeof obj === 'undefined') return false;

    //index of next property split
    var index = prop.indexOf('.');

    //property split found; recursive call
    if(index > -1){
        //get object at property (before split), pass on remainder
        return fetchFrom(
            obj[prop.substring(0, index)], prop.substr(index+1));
    }

    //no split; get property
    return obj[prop];
}