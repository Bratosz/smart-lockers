loadDepartments($('#select-department'), clientId);

$('#button-add-position').click(function () {
    let positionName = $('#input-position-name').val(),
        departmentId = $('#select-department').val();
    $.ajax({
        url: getActualLocation() + `/positions` +
            `/${positionName}` +
            `/${departmentId}`,
        method: 'post',
        success: function (response) {
            window.alert(response);
        }
    })
})