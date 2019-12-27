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
<!-- Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-hidden="true">
	<div class="modal-dialog" role="document">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="exampleModalLabel">削除メッセージ</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">このポストを削除しますか？</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
				<button type="button" class="btn btn-danger" id="delete_btn">削除</button>
			</div>
		</div>
	</div>
</div>
<div class="row" style="margin-bottom: 20px; margin-left:0px; margin-right:0px;">
	<div class="col-md-12" style="text-align: right;">
		<button class="btn btn-success" id="modify_btn">修正</button>
		<button class="btn btn-danger" data-toggle="modal" data-target="#deleteModal">削除</button>
	</div>
</div>
<article class="entry">
	<div class="titleArea">
		<div class="title" id="article_title">
			<input type='text' class='form-control' id='title_txt' placeholder="title" value="${post.title }">
		</div>
		<hr class="titileHr">
		<div class="categoryArea">
			<select class="form-control" id="category_sel">
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
		<c:if test="${post.isReservation}">
			<div style="margin:10px 0px;">
				<div class="custom-control custom-switch" style="display: inline-block;margin-right:20px;">
					<input type="checkbox" class="custom-control-input" id="reservation" checked>
				  	<label class="custom-control-label" for="reservation">予約投稿</label>
				</div>
				<input type="text" class="form-control date-picker" style="display: inline-block;width: 150px;" id="reservationDate" readonly value="${post.reservationDate}">
			</div>
		</c:if>
	</div>
	<div class="article">
		<div class="tt_article_useless_p_margin" id="article_contents">${post.contents }</div>
		<hr />
		<div class="list-meta ie-dotum">
			<span class="timeago ff-h dt-published tag-column" id="article_tag"> 
				<input type='text' class='form-control' id='tag_txt' placeholder='tag' value=' ${post.tags }'>
			</span>
		</div>
	</div>
</article>
<input type="hidden" id="idx" value='${post.idx }'>
<script type="text/javascript" src="./js/write.js"></script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>