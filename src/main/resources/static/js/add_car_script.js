$(document).ready(function () {

 $('#brands').change(function () {
        var brand = $('#brands').val();
        $.ajax({
            type: 'get',
            url: window.location.protocol + '//' + window.location.host + '/carsales/getmodels',
            data: 'brandId=' + brand,
            dataType: 'json',
            error: [function (xhr, status, error) {
                alert(xhr.responseText + '|\n' + status + '|\n' + error);
            }],
            success: [function (response) {
                $('#models').attr('disabled', false);
                 $.each(response, function () {
                      $('#models').append($("<option></option>")
                          .attr('value', this.id)
                          .text(this.name));
                 })
            }]
        })
    });


    $('#models').change(function () {
        if ($('#models').val() === undefined) {
            $('#models').after('wrong input');
            disabledButton();
        }
        var model = $('#model').val();
        model.id = $('#models').val();
        model.name = $('#models').text();
        $('#models').val(model);
    });

    $('#year').change(function () {
        if (!parseInt($('#year').val())) {
            $('#year').after('wrong input');
            disabledButton();
        }
    });

    $('#color').change(function () {
        if (!(/^[A-z]+$/).test($('#color').val())) {
            $('#color').after('wrong input');
            disabledButton();
        }
    });

    function checkModel() {
        return $('#models').val() !== undefined
    }

    function checkYear() {
        return parseInt($('#year').val());
    }

    function checkColor() {
        return (/^[A-z]+$/).test($('#color').val());
    }

    function checkAllFields() {
        if (checkModel() && checkYear() && checkColor()) {
            $('#input_button').prop('disabled', false);
        }
    }

    setInterval(checkAllFields, 2000);
});

function disabledButton() {
    $('#input_button').prop('disabled', true);
}

