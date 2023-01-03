let script = {
  init: function() {
    let path = location.pathname;
    $(".nav-item").each(function() {
      if ($(this).attr("class").indexOf(path) > -1) {
        $(this).addClass("active");
        $(this).find("ul").addClass("show");
        $(this).find("ul > li > a").each(function() {
          if ($(this).attr("href").indexOf(path) > -1) {
            $(this).addClass("active");
          }
        });
      } else {
        $(this).removeClass("active");
      }
    });
  },
  ajax: function(url, method, async, type, dataObj, callback) {
    $.ajax({
      url: url,
      method: method,
      async: async,
      dataType: type,
      data: dataObj,
      beforeSend: function() {
        if (async) $(".div_load_image").show();
      },
      success: function(r) {
        callback(r);
      },
      error: function(jqXHR, textStatus, errorThrown) {
        alert("오류가 발생하였습니다.\n잠시 후 다시 시도해주세요.");
      },
      complete: function() {
      if (async) $(".div_load_image").hide();
      }
    });
  },
  multipartAjax: function(url, method, async, type, dataObj, callback) {
      $.ajax({
        url: url,
        method: method,
        async: async,
        dataType: type,
        data: dataObj,
        enctype:"multipart/form-data",
        processData:false,
        contentType:false,
        cache:false,
        beforeSend: function() {
          if (async) $(".div_load_image").show();
        },
        success: function(r) {
          callback(r);
        },
        error: function(jqXHR, textStatus, errorThrown) {
          alert("오류가 발생하였습니다.\n잠시 후 다시 시도해주세요.");
        },
        complete: function() {
          if (async) $(".div_load_image").hide();
        }
      });
    },
    ckeditorInit: function(className, callback) {
      ClassicEditor.create(document.querySelector("." + className), {
        toolbar: {
          items: [
            'undo',
            'redo',
            '|',
            'fontFamily',
            'fontSize',
            'fontColor',
            '|',
            'bold',
            'italic',
            'alignment',
            '|',
            'link',
            'insertTable'
          ]
        },
        language: 'ko',
        image: {
          toolbar: [
            'imageTextAlternative',
            'imageStyle:inline',
            'imageStyle:block',
            'imageStyle:side'
          ]
        },
        table: {
          contentToolbar: [
            'tableColumn',
            'tableRow',
            'mergeTableCells'
          ]
        }
      })
      .then(editor => {
        callback(editor);
      })
      .catch(error => {
        console.error(error);
      });
    }
};