const url = new URL(window.location.href);
const boxId = url.searchParams.get('box-id');

loadDepartments(clientId, $('#select-department'));
loadBoxInfo(boxId);

$('#button-add-employee').click(function () {
    const departmentId = $('#select-department').val();
    const firstName = $('#input-first-name').val();
    const lastName = $('#input-last-name').val();

    $.ajax({
        url: getActualLocation() +
            `/employees/create-employee-and-add-to-box` +
            `/${boxId}` +
            `/${departmentId}` +
            `/${firstName}` +
            `/${lastName}`,
        method: 'post',
        success: function (response) {
            window.alert(response);
        }
    })
});

function loadBoxInfo(boxId) {
    $.ajax({
        url: getActualLocation() + `/boxes`+
            `/${boxId}`,
        method: 'get',
        success: function (box) {
            writeBoxInfoToElement(box, $('#box-info'));
        }
    });
}



