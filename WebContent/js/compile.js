"use strict";
var _this = (function (obj) {
    return obj;
})((function () {
    var __ = {
        property: {
            isFinished: false
        },
        fn: {
            status: function () {
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    url: "./status.ajax",
                    success: function (data) {
                        //console.log(data.status);
                        if (data.status === 5) {
                            $(".complie-card").addClass("disabled");
                            __.property.isFinished = true;
                        }
                        else if (data.status !== 0) {
                            $(".complie-card").addClass("disabled");
                        }
                        else {
                            $(".complie-card").removeClass("disabled");
                        }
                        $("#status").val(data.message);
                        $("#timestamp").val(data.time);
                        $(".progress .progress-bar").attr("aria-valuenow", data.progress);
                        $(".progress .progress-bar").css("width", data.progress + "%");
                        if (!__.property.isFinished) {
                            setTimeout(__.fn.status, 1000);
                        }
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                        toastr.error("エラーが発生しました。ログを確認してください。");
                    },
                    complete: function (jqXHR, textStatus) {
                    }
                });
            }
        },
        ev: function () {
            $(".compile-btn").on("click", function () {
                _.loading.on();
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    url: "./compile.ajax",
                    success: function (data) {
                        console.log(data);
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        console.log(jqXHR);
                        console.log(errorThrown);
                        toastr.error("エラーが発生しました。ログを確認してください。");
                    },
                    complete: function (jqXHR, textStatus) {
                        _.loading.off();
                    }
                });
            });
        }
    };
    $(__.ev);
    $(function () {
        // setInterval(__.fn.status, 1000);
        setTimeout(__.fn.status, 1000);
    });
    return {};
})());
