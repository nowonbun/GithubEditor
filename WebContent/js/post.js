"use strict";
(function (obj) {
    $(obj.onLoad);
})((function () {
    var __ = {
        property: {
            maximumImageFileSize: 1024 * 1024,
            modifyMode: false,
            idx: $("#idx").val()
        },
        fn: {
            uploadAttachFile: function (filename, type, data, cb, er) {
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        filename: filename,
                        type: type,
                        data: data
                    },
                    url: "./addAttachFile.ajax",
                    success: function (data) {
                        cb.call(this, data);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                        toastr.error("エラーが発生しました。ログを確認してください。");
                        er.call(this, data);
                    },
                    complete: function (jqXHR, textStatus) {
                    }
                });
            },
            getBase64Data: function (data) {
                var item = data.split(",");
                if (item.length != 2) {
                    return null;
                }
                var type = item[0].split(";");
                if (type.length != 2) {
                    return null;
                }
                if (type[1] !== 'base64') {
                    return null;
                }
                return {
                    type: type[0],
                    item: item[1]
                };
            },
            updatePost: function () {
                var titletxt = $('#title_txt').val();
                if (titletxt === undefined || $.trim(titletxt.toString()) === "") {
                    toastr.error("empty title");
                    return;
                }
                loading.on();
                var state = 0;
                var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
                function checkNmodifyPost() {
                    state++;
                    if (state === count) {
                        //__.fn.modifyPost();
                    }
                }
                if (count === 0) {
                    //__.fn.modifyPost();
                }
                $("img[data-filename]").each(function () {
                    var $this = $(this);
                    var data = __.fn.getBase64Data($(this).prop("src"));
                    if (data === null) {
                        checkNmodifyPost();
                        return;
                    }
                    __.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function (data) {
                        $this.prop("src", data.message);
                        checkNmodifyPost();
                    }, function () {
                        $this.prop("src", "");
                        checkNmodifyPost();
                    });
                });
                $("a.attachfile[data-filename]").each(function () {
                    var $this = $(this);
                    var data = __.fn.getBase64Data($(this).prop("href"));
                    if (data === null) {
                        checkNmodifyPost();
                        return;
                    }
                    __.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function (data) {
                        $this.prop("href", data.message);
                        checkNmodifyPost();
                    }, function () {
                        $this.prop("href", "");
                        checkNmodifyPost();
                    });
                });
            }
        },
        ev: function () {
            $("#modify_btn").on("click", function () {
                var form = document.createElement('form');
                var submit = document.createElement('input');
                form.action = "./modify.html?idx=" + __.property.idx;
                form.method = "POST";
                submit.type = "submit";
                form.appendChild(submit);
                document.body.appendChild(form);
                submit.click();
            });
            $("#delete_btn").on("click", function () {
                loading.on();
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        idx: __.property.idx
                    },
                    url: "./deletePost.ajax",
                    success: function (data) {
                        if (data.ret) {
                            location.href = data.message;
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                        toastr.error("エラーが発生しました。ログを確認してください。");
                    },
                    complete: function (jqXHR, textStatus) {
                        loading.off();
                    }
                });
            });
            $("input[type=file].note-image-input").on("change", function () {
                var el = $(this)[0];
                if (el.files !== null) {
                    if (el.files[0].size > __.property.maximumImageFileSize) {
                        toastr.error("big file.");
                    }
                }
            });
            $("input[type=file].note-attach-input").on("change", function () {
                var el = $(this)[0];
                if (el.files !== null) {
                    if (el.files[0].size > __.property.maximumImageFileSize) {
                        toastr.error("big file.");
                        $("input[type=file].note-attach-input").val("");
                        $(".attachment-dialog").modal("hide");
                        return;
                    }
                    // https://summernote.org/deep-dive/#insertimage
                    var file = el.files[0];
                    var filename_1 = file.name;
                    var reader_1 = new FileReader();
                    reader_1.onload = function (e) {
                        var node = document.createElement('p');
                        var ret = reader_1.result;
                        if (ret !== null) {
                            $(node).append($("<a class='attachfile'><img src='./img/zip.gif'> " + filename_1 + "</a>").attr("href", ret.toString()).attr("data-filename", filename_1));
                            $('#article_contents').summernote('insertNode', node);
                            $("input[type=file].note-attach-input").val("");
                            $(".attachment-dialog").modal("hide");
                        }
                    };
                    reader_1.readAsDataURL(file);
                }
            });
            $("pre code.hljs").each(function () {
                $(this).before($("<a href='javascript:void(0);' class='code-copy'><i class='fa fa-copy'></i>Copy!</a>"));
                $(this).before($("<div class='code-title'></div>").append($("<i class='fa fa-minus-square code-collapse'></i>"))
                    .append("&nbsp;[Source view]&nbsp;" + $(this).data("type")));
                $(this).parent().addClass("code-view");
            });
            $(document).on("click", ".code-title", function () {
                var $this = $(this);
                var $i = $this.find("i.code-collapse");
                if ($i.hasClass("fa-plus-square")) {
                    $i.removeClass("fa-plus-square");
                    $i.addClass("fa-minus-square");
                    $this.parent().removeClass("code-view-disabled");
                }
                else {
                    $i.removeClass("fa-minus-square");
                    $i.addClass("fa-plus-square");
                    $this.parent().addClass("code-view-disabled");
                }
            });
            $(document).on("click", '.code-copy', function () {
                var $parent = $(this).closest(".code-view");
                if ($parent.hasClass("code-view-disabled")) {
                    var $i = $parent.find("i.code-collapse");
                    $i.removeClass("fa-plus-square");
                    $i.addClass("fa-minus-square");
                    $parent.removeClass("code-view-disabled");
                }
                toastr.success("", "コピーされました。", { timeOut: 700 });
                var code_element = $(this).closest("pre").find("code")[0];
                var value = code_element.innerText.replace(/\n\n\n/ig, '').replace('    \n', '');
                var selection = window.getSelection();
                var body_element = document.getElementsByTagName('body')[0];
                var newdiv = document.createElement('div');
                newdiv.style.position = 'absolute';
                newdiv.style.left = '-10000px';
                newdiv.style.top = '-10000px';
                body_element.appendChild(newdiv);
                newdiv.innerHTML = "<pre>" + escapeHTML(value) + "</pre>";
                selection === null || selection === void 0 ? void 0 : selection.selectAllChildren(newdiv);
                setTimeout(function () {
                    newdiv.remove();
                }, 10000);
                document.execCommand('copy');
            });
        }
    };
    $(__.ev);
    return {
        onLoad: function () {
        }
    };
})());
