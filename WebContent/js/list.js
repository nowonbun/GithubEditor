var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};

	__.property = {
		page : 0,
		count : Number($.trim($("#count").val())),
		pageMax : Number($.trim($("#pageMax").val()))
	}

	__.fn = {
		selectList: function(){
			var code = $("#category").val();
			var query =$("#query").val();
			if($.trim(code) !== ""){
				var $item = $(".category-item[data-code="+code+"]");
	            $item.addClass("active");
	            var $parent = $item.closest("ul.sub_category_list");
	            if($parent.length > 0){
	                $parent.prev().trigger("click");
	            }
			} else if($.trim(query) !== ""){
				$(".search-text").val($.trim(query));
			}
		},
		getList : function() {
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
					category : $("#category").val(),
					query : $("#query").val()
				},
				url : "./list.ajax",
				success : function(data) {
					for (var i = 0; i < data.length; i++) {
						var post = data[i];
						var $article = $($(".list-article").html());
						$article.find(".list-link").prop("href", "./post.html?idx=" + post.idx);
						$article.find(".ci-link").html(post.title);
						if (post.tags !== undefined && post.tags !== null) {
                            $article.find(".tag-column").html("");
                            var taglist = post.tags.split(',');
                            for(var j=0;j<taglist.length;j++){
                                if(taglist[j][0] === '#'){
                                	var taglink = $("<a class='p-tag'></a>").prop("href","./search.html?query="+encodeURIComponent(taglist[j].substring(1,taglist[j].length)));
                                    taglink = taglink.text(taglist[j]);
                                    $article.find(".tag-column").append(taglink);
                                } else {
                                    $article.find(".tag-column").append(taglist[j]);
                                }
                                $article.find(".tag-column").append(",");
                            }
                            var tagColumn = $article.find(".tag-column").html();
                            $article.find(".tag-column").html(tagColumn.substring(0,tagColumn.length-1));
						}
						$article.find(".p-category").text(post.categoryName);
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

	__.ev = function() {
		$(window).scroll(function() {
			if ($(window).scrollTop() >= $(document).height() - $(window).height()) {
				if (__.property.page < __.property.pageMax) {
					__.fn.getList();
				}
			}
		});
	}

	$(__.ev);
	$(function() {
		__.fn.getList();
		__.fn.selectList();
	});
	return {};
})());