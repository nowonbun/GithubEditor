<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<style>
.card {
	position: relative;
	display: -webkit-box;
	display: -ms-flexbox;
	display: flex;
	-webkit-box-orient: vertical;
	-webkit-box-direction: normal;
	-ms-flex-direction: column;
	flex-direction: column;
	min-width: 0;
	word-wrap: break-word;
	background-color: #fff;
	background-clip: border-box;
	border: 1px solid rgba(0, 0, 0, 0.125);
	border-radius: 0.25rem;
}

.card-body {
	-webkit-box-flex: 1;
	-ms-flex: 1 1 auto;
	flex: 1 1 auto;
	padding: 1.25rem;
}

.card-body-icon {
	position: absolute;
	z-index: 0;
	top: -1.25rem;
	right: -1rem;
	opacity: 0.4;
	font-size: 5rem;
	-webkit-transform: rotate(15deg);
	transform: rotate(15deg);
}
.card-footer{
	z-index:1;
}
.card.disabled{
    background-color: gray!important;
    cursor: wait;	
}
</style>
<div class="row">
	<div class="col-xl-4 col-sm-8 mb-3">
		<div class="card text-white bg-primary o-hidden h-100 complie-card">
			<div class="card-body">
				<div class="card-body-icon">
					<i class="fas fa-fw fa-file-export"></i>
				</div>
				<div class="mr-5">ポストの総数 : ${postCount}</div>
			</div>
			<a class="card-footer text-white clearfix small z-1 compile-btn" href="javascript:void(0);"> 
				<span class="float-left">Compile</span> 
				<span class="float-right"> 
				<i class="fas fa-angle-right"></i>
			</span>
			</a>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-xl-6 col-sm-8 mb-3">
		<input type="text" class="form-control" readonly="readonly" id="status">
	</div>
</div>
<div class="row">
	<div class="col-xl-6 col-sm-8 mb-3">
		<div class="input-group">
			<div class="input-group-prepend">
				<div class="input-group-text">Last updated time</div>
			</div> 
			<input id="timestamp" class="form-control" readonly="readonly">
		</div>
	</div>
</div>
<div class="row">
	<div class="col-xl-6 col-sm-8 mb-3">
		<div class="progress">
			<div class="progress-bar syc-progress" role="progressbar" style="width: 0%" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>
		</div>
	</div>
</div>
<script type="text/javascript" src="./js/compile.js"></script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>