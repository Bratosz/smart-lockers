function dismissEmployee(employeeId) {
    $.ajax({
        url: `http://localhost:8080/employees/dismiss-by-id/${employeeId}/${userId}`,
        method: "post",
        success: function (response) {
            alert(response)
            loadLocker();
        }
    })
}