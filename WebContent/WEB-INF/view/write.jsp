<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<style>
#title_txt {
	font-size: 25px;
	height: 40px;
}
#tag_txt {
	font-size: 18px;
	height: 34px;
}
div.titleArea {
    margin-bottom: 15px;
}
select#category_sel {
	height: 34px;
}
</style>
<div class="row" style="margin-bottom: 20px;">
	<div class="col-md-12" style="text-align: right;">
		<button class="btn btn-success" id="add_btn">추가</button>
	</div>
</div>
<article class="entry">
	<div class="titleArea">
		<div class="title" id="article_title">
			<input type='text' class='form-control' id='title_txt' placeholder="title">
		</div>
		<hr class="titileHr">
		<div class="categoryArea">
			<select class="form-control" id="category_sel">
				<c:forEach items="${categorylist}" var="item">
				    <option value="${item.value }">${item.text }</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="article">
		<div class="tt_article_useless_p_margin" id="article_contents"></div>
		<hr />
		<div class="list-meta ie-dotum">
			<span class="timeago ff-h dt-published tag-column" id="article_tag"> 
				<input type='text' class='form-control' id='tag_txt'  placeholder="tag">
			</span>
		</div>
	</div>
</article>
<jsp:include page="./particle/bottom.jsp"></jsp:include>
<script>
	var _this = (function(obj) {
		obj.init();
		$(obj.onLoad);
		return obj;
	})({
		init : function() {
			$('#add_btn').on('click', function(){
				if($.trim($('#title_txt').val()) === ""){
					toastr.error("empty title");
					return;
				}
				_.loading.on();
				$.ajax({
					type : 'POST',
					dataType : 'json',
					data : {
						title: $.trim($('#title_txt').val()),
						category: $('#category_sel').val(),
						contents: $('#article_contents').summernote('code'),
						tag: $.trim($('#tag_txt').val())
					},
					url : "./createPost.ajax",
					success : function(data) {
						if (data.ret) {
							toastr.success(data.message);
						}
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
			});
		},
		onLoad : function() {
			_.loading.on();
			$('#article_contents').summernote({
				height : $(window).height() - 400
			});
			_.loading.off();
		}
	})
</script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>