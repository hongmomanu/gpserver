$(document).ready(function () {
    $('button').click(function () {


        //$('#todo-list').append("<ul>" + $("input[name=task]").val() + " <a href='#' class='close' aria-hidden='true'>&times;</a></ul>");

      //$('#todo-list').append("<tr><td><div class=\"show-item row-fluid\"><div class=\"todo-indicator pull-left item-buttons\"><span class=\"todo-done\"></span></div><span class=\"todo-content\">"+$("input[name=title]").val()+"</span><div class=\"pull-right item-buttons\"><span class=\"todo-edit\"></span><span class=\"todo-destroy\"></span></div></div></td></tr>");



    });
    $("body").on('click', '#todo-list .todo-destroy', function () {
        $(this).closest("tr").remove();
    });


  function readURL(input) {

    if (input.files && input.files[0]) {
        var reader = new FileReader();

        reader.onload = function (e) {
            $('#blah').attr('src', e.target.result);
        }

        reader.readAsDataURL(input.files[0]);
    }
}

    $("#titleimage").change(function(){
        readURL(this);
    });

});
