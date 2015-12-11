$(document).ready(function () {
    $('button').click(function () {


        //$('#todo-list').append("<ul>" + $("input[name=task]").val() + " <a href='#' class='close' aria-hidden='true'>&times;</a></ul>");

      //$('#todo-list').append("<tr><td><div class=\"show-item row-fluid\"><div class=\"todo-indicator pull-left item-buttons\"><span class=\"todo-done\"></span></div><span class=\"todo-content\">"+$("input[name=title]").val()+"</span><div class=\"pull-right item-buttons\"><span class=\"todo-edit\"></span><span class=\"todo-destroy\"></span></div></div></td></tr>");



    });


  $( "#todo-list .todo-destroy" ).click(function() {
    $(this).closest("tr").remove();
  });

  $( "#todo-list .todo-edit" ).click(function() {

    window.location="/article/"+$(this).attr("articleid");

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
