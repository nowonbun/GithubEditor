<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<form method="POST" action="./templateModify.html">
	<div class="row" style="margin-bottom: 20px; margin-left: 0px; margin-right: 0px;">
		<div class="col-md-12" style="text-align: right;">
			<input type="submit" class="btn btn-success" id="modify_template_btn" value="修正">
		</div>
	</div>
	<input type=hidden name="templateName" value="${templateName }">
	<textarea style="height: calc(85vh); width: 100%; font-size: 0.7rem;" name="templateData">${data }</textarea>
</form>
<jsp:include page="./particle/bottom.jsp"></jsp:include>


