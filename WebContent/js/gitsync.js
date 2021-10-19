"use strict";
(function (obj) {
    $(obj.onLoad);
})((function () {
    var __ = {
        property: {
            isStart: false
        },
        fn: {
            status: function () {
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    url: "./gitstatus.ajax",
                    success: function (data) {
                        if (data.status !== 0) {
                            $(".complie-card").addClass("disabled");
                            __.property.isStart = true;
                        }
                        else {
                            if (!__.property.isStart) {
                                $(".complie-card").removeClass("disabled");
                            }
                        }
                        if (!(__.property.isStart && data.status === 0)) {
                            $("#status").val(data.message);
                            $("#timestamp").val(data.time);
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
                loading.on();
                $.ajax({
                    type: 'POST',
                    dataType: 'json',
                    url: "./gitsync.ajax",
                    success: function (data) {
                        console.log(data);
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
        }
    };
    $(__.ev);
    $(function () {
        // setInterval(__.fn.status, 1000);
        setTimeout(__.fn.status, 1000);
    });
    return {
        onLoad: function () {
        }
    };
})());
