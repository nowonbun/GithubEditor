"use strict";
var _this = (function (obj) {
    return obj;
})((function () {
    var __ = {
        property: {},
        fn: {
            getParameterByName: function (name) {
                name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
                var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);
                return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
            },
            getCategoryName: function (code) {
                var $item = $(".category-item[data-code=" + code + "]");
                $item.addClass("active");
                var $parent = $item.closest("ul.sub_category_list");
                if ($parent.length > 0) {
                    //$parent.prev().trigger("click");
                    return $parent.prev().text() + ' / ' + $item.text();
                }
                return $item.text();
            },
            view: function () {
                var category = __.fn.getParameterByName('category');
                var query = __.fn.getParameterByName('query');
                if (query === null && category === null) {
                    $(".searchList").hide();
                }
                else if (category !== null) {
                    var categoryname = __.fn.getCategoryName(category);
                    $(".searchList h1 span").text(categoryname);
                }
                else if (query !== null) {
                    $(".searchList h1 span").text(query);
                }
                _.loading.on();
                if (category !== null) {
                    $("article:not([data-category-code=" + category + "])").hide();
                    $("#searchResultCount").text($("article[data-category-code=" + category + "]").length);
                }
                else if (query !== null) {
                    var count = 0;
                    $("article").each(function () {
                        if (query !== null && $(this).find("h5.ci-link").text().toUpperCase().indexOf(query.toUpperCase()) > -1) {
                            count++;
                            return;
                        }
                        if (query !== null && $(this).find(".tag-column").text().toUpperCase().indexOf(query.toUpperCase()) > -1) {
                            count++;
                            return;
                        }
                        $(this).hide();
                    });
                    $("#searchResultCount").text(count);
                    $("input[type=search]").val(query);
                }
                _.loading.off();
            }
        },
        ev: function () {
        }
    };
    $(__.ev);
    $(function () {
        /* __.fn.getList(); */
        __.fn.view();
    });
    return {};
})());
