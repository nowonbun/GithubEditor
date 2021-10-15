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
                    $parent.prev().trigger("click");
                    return $parent.prev().text() + ' / ' + $item.text();
                }
                return $item.text();
            },
            getList: function () {
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
                $.ajax({
                    type: 'GET',
                    dataType: 'json',
                    url: "./list.json",
                    success: function (data) {
                        // console.log(data);
                        // var ret = $(data).find("item");
                        // console.log(data);
                        var list = [];
                        for (var i = 0; i < data.length; i++) {
                            var node = data[i];
                            if (query !== null) {
                                if (node.title.toUpperCase().indexOf(query.toUpperCase()) > -1) {
                                    list.push(node);
                                }
                                else if (node.tags.toUpperCase().indexOf(query.toUpperCase()) > -1) {
                                    list.push(node);
                                }
                            }
                            else if (category !== null) {
                                if (node.categoryCode === category) {
                                    list.push(node);
                                }
                            }
                            else {
                                list.push(node);
                            }
                        }
                        $("#searchResultCount").text(list.length);
                        if (list.length === 0) {
                            var $article = $("<article class='no-list-item'></article>");
                            var $entity = $("<div class='list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix no-result'></div>");
                            var $entity_body = $("<div style='width: 100%;text-align:center;'></div>");
                            $entity_body.append("検索結果がありません。");
                            $(".list-area").html("");
                            $entity.append($entity_body);
                            $article.append($entity);
                            $(".list-area").append($article);
                            _.loading.off();
                            return;
                        }
                        for (var i = 0; i < list.length; i++) {
                            var post = list[i];
                            // console.log(post);
                            var $article = $($(".list-article").html());
                            $article.find(".list-link").prop("href", "./" + post.idx + ".html");
                            $article.find(".ci-link").html(post.title);
                            if (post.tags !== undefined && post.tags !== null) {
                                $article.find(".tag-column").html("");
                                var taglist = post.tags.split(',');
                                for (var j = 0; j < taglist.length; j++) {
                                    if (taglist[j][0] === '#') {
                                        var taglink = $("<a class='p-tag'></a>").prop("href", "./?query=" + encodeURIComponent(taglist[j].substring(1, taglist[j].length)));
                                        taglink = taglink.text(taglist[j]);
                                        $article.find(".tag-column").append(taglink);
                                    }
                                    else {
                                        $article.find(".tag-column").append(taglist[j]);
                                    }
                                    $article.find(".tag-column").append(",");
                                }
                                var tagColumn = $article.find(".tag-column").html();
                                $article.find(".tag-column").html(tagColumn.substring(0, tagColumn.length - 1));
                            }
                            $article.find(".list-summary").text(post.summary);
                            $article.find(".date-column.create-date").text(post.createddate);
                            $article.find(".date-column.update-date").text(post.lastupdateddate);
                            $article.find(".p-category").text(post.categoryName);
                            $article.find(".p-category").prop("href", "/?category=" + post.categoryCode);
                            $(".list-area").append($article);
                        }
                        _.loading.off();
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
            }
        },
        ev: function () {
        }
    };
    $(__.ev);
    $(function () {
        __.fn.getList();
    });
    return {};
})());
