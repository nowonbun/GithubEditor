var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};

	__.property = {

	}

	__.fn = {

	};

	__.ev = function() {
		$(".compile-btn").on("click", function() {
			$.ajax({
				type : 'POST',
				dataType : 'json',
				url : "./gitsync.ajax",
				success : function(data) {
					console.log(data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("エラーが発生しました。ログを確認してください。");
				},
				complete : function(jqXHR, textStatus) {
				}
			});
		});
	}

	$(__.ev);

	$(function() {
		//setInterval(__.fn.status, 1000);
	});

	return {};
})());