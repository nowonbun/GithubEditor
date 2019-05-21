<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
	<div class="searchList">
		<h3>
			<span>${title}</span> 検索結果
		</h3>
		<p>検索件数 : ${count}　件</p>
	</div>
	<div class="list-area"></div>
	<input type="hidden" id="category" value="${category}"> 
	<input type="hidden" id="pageMax" value="${pageMax}">
	<input type="hidden" id="count" value="${count}"> 
</div>
<template class="list-article">
<article class="list-item">
	<div class="list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix searchListEntity">
		<div class="list-body" style="width: 100%;">
			<div class="flexbox">
				<a class="list-link" href="">
					<h3 class="list-head ie-nanum ci-link"></h3>
					<p class="list-summary"></p>
				</a>
				<div class="list-meta ie-dotum">
					<p><span class="timeago ff-h dt-published tag-column"></span></p> 
					<p>
						<span class="data-column-label">作成日付 :</span>
						<span class="timeago ff-h dt-published date-column create-date"></span>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						<span class="data-column-label">修正日付 :</span>
						<span class="timeago ff-h dt-published date-column update-date"></span>
					</p>
				</div>
			</div>
		</div>
	</div>
</article>
</template>
<script>
	var _this = (function(obj) {
		$(obj.onLoad);
		return obj
	})((function() {
		var page = 0;
		var count = Number($.trim($("#count").val()));
		var pageMax = Number($.trim($("#pageMax").val()));
		function getList() {
			_.loading.on();
			if (count === 0) {
				var $article = $("<article class='no-list-item'></article>");
				var $entity = $("<div class='list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix searchListEntity'></div>");
				var $entity_body = $("<div style='width: 100%;text-align:center;'></div>");
				$entity_body.append("검색된 결과가 없습니다.");
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
					page : page,
					category : $("#category").val()
				},
				url : "./list.ajax",
				success : function(data) {
					for (var i = 0; i < data.length; i++) {
						var post = data[i];
						var $article = $($(".list-article").html());
						$article.find(".list-link").prop("href", "./post.html?idx=" + post.idx);
						$article.find(".ci-link").html(post.title);
						if(post.tags !== undefined && post.tags !== null){
							$article.find(".tag-column").text(post.tags);	
						}
						$article.find(".date-column.create-date").text(post.createddate);
						$article.find(".date-column.update-date").text(post.lastupdateddate);
						$(".list-area").append($article);
					}
					page++;
					_.loading.off();
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("예상치 못한 에러가 발생했습니다. 로그를 확인해 주십시오.");
				},
				complete : function(jqXHR, textStatus) {
					_.loading.off();
				}
			});
		}
		return {
			onLoad : function() {
				$(window).scroll(function() {
					if ($(window).scrollTop() >= $(document).height() - $(window).height()) {
						if (page < pageMax) {
							getList();
						}
					}
				});
				getList();
			}
		};
	})());
</script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>