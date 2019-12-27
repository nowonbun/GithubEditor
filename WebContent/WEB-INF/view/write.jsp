<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<link rel="stylesheet" href="./css/post.css">
<style>
    pre {
    	border: 2px solid #e9e9e9;
    	padding: 10px;
    	background: #f8f8f8;
	}
	pre code{
		border: initial;
	}
	blockquote {
		padding: 5px 10px;
	}
</style>
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
		<button class="btn btn-success" id="add_btn">格納</button>
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
		<div style="margin:10px 0px;">
			<div class="custom-control custom-switch" style="display: inline-block;margin-right:20px;">
				<input type="checkbox" class="custom-control-input" id="reservation">
			  	<label class="custom-control-label" for="reservation">予約投稿</label>
			</div>
			<input type="text" class="form-control date-picker" style="display: inline-block;width: 150px;" id="reservationDate" disabled readonly>
		</div>
	</div>
	<div class="article">
		<div class="tt_article_useless_p_margin" id="article_contents"></div>
		<hr />
		<div class="list-meta ie-dotum">
			<span class="timeago ff-h dt-published tag-column" id="article_tag"> 
				<input type='text' class='form-control' id='tag_txt' placeholder='tag'>
			</span>
		</div>
	</div>
</article>
<script type="text/javascript" src="./js/write.js"></script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>