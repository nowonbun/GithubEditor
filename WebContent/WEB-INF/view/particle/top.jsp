<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
<meta name="theme-color" content="#29343a">
<title>Nowonbun blog editor</title>
<!-- link rel="stylesheet" type="text/css" href="./css/blog/style.css"-->
<!-- https://getbootstrap.com/docs/3.3/getting-started/ -->
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="//stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/font-awesome/5.8.2/css/all.css">
<link rel="stylesheet" type="text/css" href="//maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css">
<link rel="stylesheet" type="text/css" href="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/css/toastr.min.css">
<link rel="stylesheet" type="text/css" href="http://cdnjs.cloudflare.com/ajax/libs/summernote/0.8.11/summernote-bs4.css">
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/highlight.js/9.13.1/styles/rainbow.min.css">
<link rel="stylesheet" href="./css/highlight.init.css">
<link rel="stylesheet" type="text/css" href="./css/loader.css">
<link rel="stylesheet" type="text/css" href="./css/common.css">

<script src="//code.jquery.com/jquery-3.3.1.min.js" integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=" crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.7/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>

<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/toastr.js/latest/toastr.min.js"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/moment.js/2.24.0/moment.min.js"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/jquery-cookie/1.4.1/jquery.cookie.js"></script>
<script type="text/javascript" src="//cdnjs.cloudflare.com/ajax/libs/summernote/0.8.11/summernote-bs4.js"></script>
<script type="text/javascript" src="./js/common.js"></script>
</head>
<body>
	<div class="loader off"></div>
	<section class="loader-layout off"></section>
	<nav class="navbar navbar-expand-lg nav-costom">
		<button class="menu-toggle" type="button">MENU</button>
		<a class="navbar-brand navbar-brand-flex-left" href="./" style="color: #000;">明月の開発ストーリ</a>
		<div class="collapse navbar-collapse navbar-collapse-flex-100" id="navbarSupportedContent">
			<div class="form-inline my-2 my-lg-0 search-custom">
				<input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search">
				<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
			</div>
		</div>
	</nav>
	<aside class="leftside">
		<h5 class="side-header"><img src="https://t1.daumcdn.net/cfile/tistory/1935C94C505D9F8B13">明月の開発ストーリ</h5>
		<div class="side-menu bs-glyphicons">
			<ul class="bs-glyphicons-list">
				<li onclick="location.href='./';"><span class="fa fa-home" aria-hidden="true" title="Home"></span> <span>Home</span></li>
				<li onclick="location.href='./write.html'"><span class="fa fa-file-text-o" aria-hidden="true" title="Write"></span> <span>Write</span></li>
				<li onclick="location.href='./compile.html'"><span class="fa fa-gears" aria-hidden="true" title="Compile"></span> <span>Compile</span></li>
				<li><span class="fa fa-minus" aria-hidden="true" title="..."></span> <span>...</span></li>
				<li class="menu-close"><span class="fa fa-times" aria-hidden="true" title="Close"></span> <span>Close</span></li>
				<li class="menu-close-off"><span class="fa fa-minus" aria-hidden="true" title="..."></span> <span>...</span></li>
			</ul>
		</div>
		<div class="form-inline my-2 my-lg-0 search-custom side-search" style="padding: 5px;">
			<input class="form-control mr-sm-2" type="search" placeholder="Search" aria-label="Search" style="width:65%">
			<button class="btn btn-outline-success my-2 my-sm-0" type="submit">Search</button>
		</div>
		<div class="side-list">
			<ul class="side-nav">
				<c:forEach items="${menulist}" var="item">
					<c:choose>
					    <c:when test="${empty item.subMenu}">
						    <li class="">
								<a class="link_item link-item-collapse" href="${item.url}">${item.text}</a>
							</li>
						</c:when>
						<c:otherwise>
							<li class="">
								<a class="link_item link-item-collapse" href="javascript:void(0)"> ${item.text}
									<span class="fa fa-chevron-down pull-right"></span>
								</a>
								<ul class="sub_category_list off">
									<c:forEach items="${item.subMenu}" var="sub">
										<li class=""><a class="link_sub_item" href="${sub.url }">${sub.text}</a></li>
									</c:forEach>
								</ul>
							</li>
    					</c:otherwise>
					</c:choose>
				</c:forEach>
			</ul>
		</div>
	</aside>
	<section class="menu-back-layout menu-close off"></section>
	<div class="container-fluid main-container">
		<!-- 메인 -->