var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};
	
	__.property = {
		page: 0,
		count: Number($.trim($("#count").val())),
		pageMax: Number($.trim($("#pageMax").val()))
	}
	
	__.fn = {
		getList: function(){
			_.loading.on();
			if (__.property.count === 0) {
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
			$.ajax({
				type : 'POST',
				dataType : 'json',
				data : {
					page : __.property.page,
					category : $("#category").val()
				},
				url : "./list.ajax",
				success : function(data) {
					for (var i = 0; i < data.length; i++) {
						var post = data[i];
						var $article = $($(".list-article").html());
						$article.find(".list-link").prop("href", "./post.html?idx=" + post.idx);
						$article.find(".ci-link").html(post.title);
						if (post.tags !== undefined && post.tags !== null) {
							$article.find(".tag-column").text(post.tags);
						}
						$article.find(".list-summary").text(post.summary);
						$article.find(".date-column.create-date").text(post.createddate);
						$article.find(".date-column.update-date").text(post.lastupdateddate);
						$(".list-area").append($article);
					}
					__.property.page++;
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
	}
	
	__.ev = function(){
		$(window).scroll(function() {
			if ($(window).scrollTop() >= $(document).height() - $(window).height()) {
				if (__.property.page < __.property.pageMax) {
					__.fn.getList();
				}
			}
		});
	}
	
	$(__.ev)
	$(function(){
		__.fn.getList();
	});
	return {};
})());