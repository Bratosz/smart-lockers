const clientId = 1;
const userId = 1;
const enterValue = 13;
let isSended = true;

$(document).ready(function () {
    $('#input-bar-code').keypress(function (event) {
        let input = $('#input-bar-code');
        let initialInputValue = input.val();
        if(isSended && (initialInputValue.length > 0)){
            isSended = false;
            input.val("");
        } else if (isSended && initialInputValue.length == 0) {
            isSended = false;
        }
        let char = event.which;
        if (char == enterValue) {
            $.ajax({
                url: `http://localhost:8080/clothes/acceptance/${input.val()}`,
                method: "post",
                success: function (response) {
                    console.log(response)
                }
            })
            isSended = true;
        }
    });
});