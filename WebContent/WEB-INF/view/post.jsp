<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<div class="modal attachment-dialog" tabindex="-1" role="dialog" aria-label="Insert Image" aria-modal="true" style="display: none;">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Insert Attachment</h4>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" aria-hidden="true">×</button>
			</div>
			<div class="modal-body">
				<div class="form-group note-form-group note-group-select-from-files">
					<label class="note-form-label">Select from files</label> 
					<input class="note-attach-input note-form-control note-input" type="file" name="files" accept="*" multiple="multiple"> 
					<small>Maximum file size : 1 MB</small>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row" style="margin-bottom: 20px;">
	<div class="col-md-12" style="text-align: right;">
		<button class="btn btn-success" id="modify_btn">修正</button>
		<button class="btn btn-success" id="delete_btn">削除</button>
	</div>
</div>
<article class="entry post">
	<div class="titleArea">
		<div class="title" id="article_title">
			<h3 id="titleTxt">${post.title }</h3>
		</div>
		<hr class="titileHr">
		<div class="categoryArea">
			<a href="${post.categoryUrl }">${post.categoryName }</a> &nbsp;&nbsp; ${post.createDate}
		</div>
	</div>
	<div class="article">
		<div class="tt_article_useless_p_margin" id="article_contents">
			${post.contents }
		</div>
		<hr />
		<div class="list-meta ie-dotum">
			<span class="timeago ff-h dt-published tag-column" id="article_tag"> 
				${post.tags }
			</span>
		</div>
	</div>
</article>
<div id="template" style="display:none;">
	<div id="categoryAreaTemplate">
		<select class="form-control">
			<c:forEach items="${categorylist}" var="item">
				<c:choose>
					<c:when test="${item.value eq post.categoryCode }">
						<option value="${item.value }" selected>${item.text }</option>
					</c:when>
					<c:otherwise>
						<option value="${item.value }">${item.text }</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</select>
	</div>
</div>
<jsp:include page="./particle/bottom.jsp"></jsp:include>
<script>
	var _this = (function(obj) {
		obj.init();
		$(obj.onLoad);
		return obj;
	})((function() {
		var modifyMode = false;
		function changeModifyMode(){
			$("#article_title").html($("<input type='text' class='form-control' id='title_txt' placeholder='title'>").val($("#titleTxt").text()));
			$(".categoryArea").html($($("#categoryAreaTemplate").html()).prop("id","category_sel"));
			modifyMode = true;
		}
		function updatePost(){
			console.log("update");
		}
		return {
			init : function() {
				$("#modify_btn").on("click", function(){
					if(!modifyMode){
						changeModifyMode();
					} else {
						updatePost();
					}
				});
			},
			onLoad : function() {
				
			}
		}
	})());
</script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>