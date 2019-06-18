<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 list-area-panel">
	<div class="searchList">
		<h1>
			<span>${title}</span> 検索結果
		</h1>
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
					<h5 class="list-head ie-nanum ci-link"></h5>
					<p class="list-summary"></p>
				</a>
				<div class="list-meta ie-dotum">
					<p><a href="" class="p-category ci-color"></a></p>
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
<script type="text/javascript" src="./js/list.js"></script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>