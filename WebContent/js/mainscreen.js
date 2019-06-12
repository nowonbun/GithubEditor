var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};

	__.property = {

	}

	__.fn = {
		getParameterByName : function(name) {
			name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
			var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);
			return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
		},
		getCategoryName : function(code) {
            var $item = $(".category-item[data-code="+code+"]");
            var name = $item.text();
            //SUDO: this
            return name;
		},
		getList : function() {
			var category = __.fn.getParameterByName('category');
			var query = __.fn.getParameterByName('query');
			if (query === null && category === null) {
				$(".searchList").hide();
			} else if(category !== null){
                var categoryname = __.fn.getCategoryName(category);
                $(".searchList h3 span").text(categoryname);
            }
			_.loading.on();
			$.ajax({
				type : 'GET',
				dataType : 'json',
				url : "./list.json",
				success : function(data) {
					// console.log(data);
					// var ret = $(data).find("item");
					// console.log(data);
					var list = [];
					for (var i = 0; i < data.length; i++) {
                        var node = data[i];
						if (query !== null) {
							if (node.title.toUpperCase().indexOf(query.toUpperCase()) > -1) {
								list.push(node);
							} else if (node.tags.toUpperCase().indexOf(query.toUpperCase()) > -1) {
								list.push(node);
							}
						} else if (category !== null) {
                            if(node.categoryCode === category){
                                list.push(node);
                            }
						} else {
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
							$article.find(".tag-column").text(post.tags);
						}
						$article.find(".list-summary").text(post.summary);
						$article.find(".date-column.create-date").text(post.createddate);
						$article.find(".date-column.update-date").text(post.lastupdateddate);
						$(".list-area").append($article);
					}
					_.loading.off();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("エラーが発生しました。ログを確認してください。");
				},
				complete : function(jqXHR, textStatus) {
					_.loading.off();
				}
			});
		}
	};

	__.ev = function() {

	};
	$(__.ev);

	$(function() {
		__.fn.getList();
	});

	return {

	};
})());