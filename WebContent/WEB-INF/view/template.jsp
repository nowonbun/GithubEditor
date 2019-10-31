<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 list-area-panel">
	<div class="list-area">
		<c:forEach var="template" items="${templateList}">
			<article class="list-item">
				<div class="list-row pos-right ratio-fixed ratio-4by3 crop-center lts-narrow fouc clearfix searchListEntity">
					<div class="list-body" style="width: 100%;">
						<div class="flexbox">
							<a class="list-link" href="./templateDetail.html?temp=${template}">
								<h5 class="list-head ie-nanum ci-link">${template}</h5>
							</a>
						</div>
					</div>
				</div>
			</article>
		</c:forEach>
	</div>
</div>
<jsp:include page="./particle/bottom.jsp"></jsp:include>