"use strict";
(function (obj) {
    $(obj.onLoad);
})((function () {
    var __ = {
        property: {
            maximumImageFileSize: 1024 * 1024,
            idx: $("#idx").val()
        },
        fn: {
            writePost: function () {
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        title: (function () {
                            var a = $('#title_txt').val();
                            if (a !== undefined) {
                                return $.trim(a.toString());
                            }
                            return "";
                        })(),
                        category: $('#category_sel').val(),
                        contents: $('#article_contents').summernote('code'),
                        tags: (function () {
                            var a = $('#tag_txt').val();
                            if (a !== undefined) {
                                return $.trim(a.toString());
                            }
                            return "";
                        })(),
                        reservation: $("#reservation").prop("checked") ? $("#reservationDate").val() : null
                    },
                    url: "./createPost.ajax",
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
            },
            modifyPost: function (isDate) {
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    data: {
                        idx: __.property.idx,
                        title: (function () {
                            var a = $('#title_txt').val();
                            if (a !== undefined) {
                                return $.trim(a.toString());
                            }
                            return "";
                        })(),
                        category: $('#category_sel').val(),
                        contents: $('#article_contents').summernote('code'),
                        tags: (function () {
                            var a = $('#tag_txt').val();
                            if (a !== undefined) {
                                return $.trim(a.toString());
                            }
                            return "";
                        })(),
                        reservation: $("#reservation").prop("checked") ? $("#reservationDate").val() : null,
                        date: isDate
                    },
                    url: "./modifyPost.ajax",
                    success: function (data) {
                        if (data.ret) {
                            location.href = data.message;
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                        toastr.error("예상치 못한 에러가 발생했습니다. 로그를 확인해 주십시오.");
                    },
                    complete: function (jqXHR, textStatus) {
                        loading.off();
                    }
                });
            },
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
            }
        },
        ev: function () {
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
                    //https://summernote.org/deep-dive/#insertimage
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
            $('#add_btn').on('click', function () {
                var titletxt = $('#title_txt').val();
                if (titletxt !== undefined && $.trim(titletxt.toString()) === "") {
                    toastr.error("empty title");
                    return;
                }
                loading.on();
                var state = 0;
                var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
                function checkNwritePost() {
                    state++;
                    if (state === count) {
                        __.fn.writePost();
                    }
                }
                if (count === 0) {
                    __.fn.writePost();
                }
                $("img[data-filename]").each(function () {
                    var _this = $(this);
                    var data = __.fn.getBase64Data($(this).prop("src"));
                    if (data === null) {
                        checkNwritePost();
                        return;
                    }
                    __.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function (data) {
                        _this.prop("src", data.message);
                        checkNwritePost();
                    }, function () {
                        _this.prop("src", "");
                        checkNwritePost();
                    });
                });
                $("a.attachfile[data-filename]").each(function () {
                    var _this = $(this);
                    var data = __.fn.getBase64Data($(this).prop("href"));
                    if (data === null) {
                        checkNwritePost();
                        return;
                    }
                    __.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function (data) {
                        _this.prop("href", data.message);
                        checkNwritePost();
                    }, function () {
                        _this.prop("href", "");
                        checkNwritePost();
                    });
                });
            });
            $("#modify_btn").on("click", function () {
                var titletxt = $('#title_txt').val();
                if (titletxt !== undefined && $.trim(titletxt.toString()) === "") {
                    toastr.error("empty title");
                    return;
                }
                loading.on();
                var state = 0;
                var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
                function checkNmodifyPost() {
                    state++;
                    if (state === count) {
                        __.fn.modifyPost(false);
                    }
                }
                if (count === 0) {
                    __.fn.modifyPost(false);
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
            });
            $("#modify_btn_update").on("click", function () {
                var titletxt = $('#title_txt').val();
                if (titletxt !== undefined && $.trim(titletxt === null || titletxt === void 0 ? void 0 : titletxt.toString()) === "") {
                    toastr.error("empty title");
                    return;
                }
                loading.on();
                var state = 0;
                var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
                function checkNmodifyPost() {
                    state++;
                    if (state === count) {
                        __.fn.modifyPost(true);
                    }
                }
                if (count === 0) {
                    __.fn.modifyPost(true);
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
            $("#reservation").change(function () {
                if ($("#reservation").prop("checked")) {
                    $("#reservationDate").prop("disabled", false);
                }
                else {
                    $("#reservationDate").val("");
                    $("#reservationDate").prop("disabled", true);
                }
            });
            $('.date-picker').datepicker({
                dateFormat: "yy-mm-dd",
                minDate: 1
            });
        }
    };
    $(__.ev);
    loading.on();
    var height = $(window).height();
    if (height !== undefined) {
        var node_height = height - 400;
        if (node_height < 250) {
            node_height = 250;
        }
        $('#article_contents').summernote({
            height: node_height,
            maximumImageFileSize: __.property.maximumImageFileSize,
            callbacks: {
                onInit: function () {
                    //attachfile
                    var button = $('<button type="button" role="button" tabindex="-1" title="" aria-label="Attachfile" data-original-title="Attachfile"></button>');
                    button.addClass("note-btn btn btn-light btn-sm attachment-tools");
                    button.append($('<i class="fa fa-paperclip"></i>'));
                    button.on("click", function () {
                        $(".attachment-dialog").modal("show");
                    });
                    $(".note-btn-group.btn-group.note-insert").append(button);
                }
            }
        });
    }
    loading.off();
    return {
        onLoad: function () {
        }
    };
})());
