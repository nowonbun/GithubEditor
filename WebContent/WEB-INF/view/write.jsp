<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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