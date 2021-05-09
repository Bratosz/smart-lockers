const url = new URL(window.location.href);
const lockerId = url.searchParams.get("id");

loadLocker();

function loadLocker() {
    function toStringLocker(locker) {
        return locker.plant.plantNumber +
            " Szafa nr " + locker.lockerNumber;
    }

    $.ajax({
        url: getActualLocation()
            + `/lockers/get/${lockerId}`,
        method: "get",
        success: function (locker) {
            $("#locker-info").text(toStringLocker(locker));
            writeDataToTable(
                sortBoxesByNumber(locker.boxes),
                $("#table-of-boxes"),
                writeBoxToRowForLockerView);
            console.log(locker);
        }
    })
};