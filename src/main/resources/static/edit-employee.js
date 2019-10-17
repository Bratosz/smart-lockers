const url = new URL(window.location.href);
const boxId = url.searchParams.get("id");

function reloadEmployee() {
    $.ajax({
        url:`http://localhost:8080/lockers/boxes/${boxId}`,
        method: "get",
        success: function(box) {
            console.log(box);

        }
    })
}