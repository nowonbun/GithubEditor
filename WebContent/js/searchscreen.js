var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};

	__.property = {
		
	}

	__.fn = {
		getParameterByName: function (name) {
			name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
			var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
				results = regex.exec(location.search);
			return results === null ? null : decodeURIComponent(results[1].replace(/\+/g, " "));
		},
		getList : function() {
			var query = __.fn.getParameterByName('query');
			$("#searchTitle").text(query);
			if(query == null){
				toastr.error("検索条件がありません。");
				return;
			}
			_.loading.on();
			$.ajax({
				type : 'GET',
				dataType : 'xml',
				url : "./rss",
				success : function(data) {
					console.log(data);
					var ret = $(data).find("item");
					var list = [];
					for(var i = 0; i < ret.length; i++){
						if($(ret[i]).find("title").text().toUpperCase().indexOf(query.toUpperCase()) > -1){
							list.push($(ret[i]));
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
						console.log(post);
						var $article = $($(".list-article").html());
						$article.find(".category-column").html(post.find("category").html());
						$article.find(".list-link").prop("href",post.find("link").html());
						$article.find(".ci-link").html(post.find("title").html());
						$article.find(".list-summary").html(post.find("description").html());
						$article.find(".date-column.update-date").html(post.find("pubDate").html());
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