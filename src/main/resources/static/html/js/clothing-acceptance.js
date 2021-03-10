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
        let keyPressed = event.which;
        if (isEnterKeyPressed(keyPressed)) {
            let clothBarCode = input.val();
            console.log(clothBarCode);
            console.log(userId);
            let exchangeType = $('input[name="exchange"]:checked').val();
            $.ajax({
                url: `http://localhost:8080/clothes/acceptance/${clientId}/${userId}/${clothBarCode}/${exchangeType}`,
                method: "post",
                success: function (response) {
                    console.log(response)
                }
            })
            isSended = true;
        }
    });
});

function isEnterKeyPressed(char) {
    let enterKeyValue = 13;
    if(char == enterKeyValue) {
        return true;
    } else {
        return false;
    }
}