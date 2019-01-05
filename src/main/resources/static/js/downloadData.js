    //отправляет регистрационные данные на сервер
function registration() {
    var name = $('#name').val();
    var pass = $('#pass').val();
    var data = 'name=' + name + '&pass=' + pass;
    $.ajax({
        type: 'post',
        url: window.location.protocol + '//' + window.location.host + '/carsales/resource/registration',
        data: data,
        dataType: 'text',
        error: [function(xhr, status, error) {
            alert(xhr.responseText + '|\n' + status + '|\n' + error);
        }],
        success: [function (response) {
            $('#message').html(response);
        }]
    })
}

function fillTable(response) {
    var picturePath = window.location.protocol + '//' + window.location.host + '/carsales/image/';
    $.each(response, function () {
        var year = 0, month = 0, day = 0;
        if (this.data !== null) {
            var curData = new Date(this.data);
            year = curData.getFullYear();
            month = curData.getMonth() + 1;
            day = curData.getDate();
        }
        $('#cars_table').append('<tr><td><img src="' + picturePath  + this.picturePath + '"></td>'
            + '<td>Brand: ' + this.model.brand.name
            + '<br>Model: ' + this.model.name
            + '<br>Color: ' + this.color
            + '<br>Body type: ' + this.bodyType.name
            + '<br>Year of issue: ' + this.year
            + '<br>Seller: ' + this.seller.name
            + '<br>Application date: ' + year + '-' + month + '-' + day
            + '<br>Status: ' + this.status
            + '</td></tr>');
    });
}

function getAllCars() {
    $.ajax({
        type: 'get',
        url: window.location.protocol + '//' + window.location.host + '/carsales/resource/allcars',
        dataType: 'json',
        error: [function(xhr, status, error) {
            alert(xhr.responseText + '|\n' + status + '|\n' + error);
        }],
        success: [function (response) {
            fillTable(response);
        }]
    });
}

function filterRequest() {
    var brandId = $('#brandFilter').val();
    var onlyFoto = $('#onlyFoto').prop('checked') ? 1 : 0;
    var currentData = $('#currentData').prop('checked') ? 1 : 0;
    $('#cars_table').empty();
    $.ajax({
        type: 'get',
        url: window.location.protocol + '//' + window.location.host + '/carsales/resource/filters',
        data: 'brandId=' + brandId + '&onlyFoto=' + onlyFoto + '&currentData=' + currentData,
        dataType: 'json',
        error: [function(xhr, status, error) {
            alert(xhr.responseText + '|\n' + status + '|\n' + error);
        }],
        success:  [function (response) {
            fillTable(response);
        }]
    });
}

$(document).ready(function () {
    //применяет фильтр "за текущую дату"
    $('#currentData').change(function () {
        filterRequest();
    });
    //применяет фильтр "только с фотографией"
    $('#onlyFoto').change(function () {
        filterRequest();
    });
    //применяет фильтр по марке авто
    $('#brandFilter').change(function () {
        filterRequest();
    });

    //заполняет таблицу объявлений при загрузке страницы
    getAllCars();

    //выполняет запрос для заполнения марок машин в фильтре
    $.ajax({
        type: 'get',
        url: window.location.protocol + '//' + window.location.host + '/carsales/resource/allbrand',
        dataType: 'json',
        error: [function(xhr, status, error) {
            alert(xhr.responseText + '|\n' + status + '|\n' + error);
        }],
        success: [function (response) {
            $.each(response, function () {
                $('#brandFilter').append('<option value="' + this.id + '">' + this.name + '</option>');
            });
        }]
    });
});