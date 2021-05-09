loadDepartments(clientId);

$("#button-add-alias").click(function () {
    let departmentId = $("#select-department").val();
    let alias = $("#input-department-alias").val();
    addAlias(departmentId, alias);
});

function displayDepartment(department) {
    console.log(department)
}

function addAlias(departmentId, alias) {
    $.ajax({
        url: `http://localhost:8080/department/add_alias/${departmentId}/${alias}`,
        method: "post",
        success: function (department) {
            displayDepartment(department)
        }
    });
}