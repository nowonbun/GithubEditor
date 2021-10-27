<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<link rel="stylesheet" href="//cdn.datatables.net/1.10.21/css/dataTables.bootstrap4.min.css" />
<link rel="stylesheet" href="//cdn.datatables.net/rowreorder/1.2.8/css/rowReorder.dataTables.min.css" />
<link rel="stylesheet" href="//cdn.datatables.net/responsive/2.2.9/css/responsive.dataTables.min.css" />
<style>
.analysis-list {
	padding: 0.5rem;
}

.analysis-list #analysisList_filter {
	/*display: none;*/
	
}

.analysis-list #analysisList {
	box-shadow: 1.2px 1.5px 2px;
}

.analysis-list .paginate_button [aria-controls=userList] {
	border: 0px;
}

.analysis-list #analysisList_paginate {
	font-size: 0.85rem;
}

.analysis-list .page-list {
	padding: .3rem .55rem;
}

.analysis-list tbody tr {
	cursor: pointer;
}
.analysis-list thead td {
	padding: 5px;
	font-size: 0.8rem;
}
table.dataTable thead .sorting_asc:before, table.dataTable thead .sorting_asc:after,
table.dataTable thead .sorting:before, table.dataTable thead .sorting:after,
table.dataTable thead .sorting_desc:before, table.dataTable thead .sorting_desc:after 
{
	bottom: 0.5em;
}
.analysis-list tbody td {
	padding: 2px;
	word-break: break-all;
	white-space: normal;
	font-size: 0.7rem;
}

.analysis-list tbody td:nth-child(1) {
	text-align: center;
}

.analysis-list .dataTables_info {
	display: none;
}
</style>
<div class="d-sm-flex align-items-center justify-content-between mb-4">
	<h1 class="h3 mb-0 text-gray-800">Dashboard</h1>
</div>
<div class="row">
	<!-- Earnings (Monthly) Card Example -->
	<div class="col-6 mb-4">
		<div class="card border-left-primary shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-primary text-uppercase mb-1">Yesterday</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${yesderdaycount}</div>
					</div>
					<div class="col-auto">
						<i class="fas fa-calendar fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- Earnings (Monthly) Card Example -->
	<div class="col-6 mb-4">
		<div class="card border-left-primary shadow h-100 py-2">
			<div class="card-body">
				<div class="row no-gutters align-items-center">
					<div class="col mr-2">
						<div class="text-xs font-weight-bold text-primary text-uppercase mb-1">Today</div>
						<div class="h5 mb-0 font-weight-bold text-gray-800">${todaycount}</div>
					</div>
					<div class="col-auto">
						<i class="fas fa-calendar fa-2x text-gray-300"></i>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row">
	<div class="col-md-12">
		<div class="card card-primary">
			<div class="card-body table-responsive analysis-list">
				<table id="analysisList" class="table table-hover text-nowrap" style="width: 100%;">
					<thead style="background-color: #f0f8ff;">
						<tr>
							<td style="width:20px;">Idx</td>
							<td style="width: 150px">Url</td>
							<td>Referrer</td>
							<td style="width: 60px;">Browser</td>
							<td style="width: 150px;">Agent</td>
							<td style="width: 70px;">Createddate</td>
						</tr>
					</thead>
				</table>
			</div>
		</div>
	</div>
</div>
<script src="//cdn.datatables.net/1.10.21/js/jquery.dataTables.min.js"></script>
<script src="//cdn.datatables.net/1.10.21/js/dataTables.bootstrap4.min.js"></script>
<script src="//cdn.datatables.net/rowreorder/1.2.8/js/dataTables.rowReorder.min.js"></script>
<script src="//cdn.datatables.net/responsive/2.2.9/js/dataTables.responsive.min.js"></script>
<script type="text/javascript" src="./js/main.js" charset="utf-8"></script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>