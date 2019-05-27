var _this = (function(obj) {
	return obj
})((function() {
	var __ = {};

	__.property = {

	}

	__.fn = {
		status : function() {
			$.ajax({
				type : 'POST',
				dataType : 'json',
				url : "./status.ajax",
				success : function(data) {
					//console.log(data);
					if (data.status != 0) {
						$(".complie-card").addClass("disabled");
					} else {
						$(".complie-card").removeClass("disabled");
					}
					$("#status").val(data.message);
					$("#timestamp").val(data.time);
					$(".progress .progress-bar").attr("aria-valuenow", data.progress);
					$(".progress .progress-bar").css("width", data.progress + "%");
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("エラーが発生しました。ログを確認してください。");
				},
				complete : function(jqXHR, textStatus) {
				}
			});
		}
	};

	__.ev = function() {
		$(".compile-btn").on("click", function() {
			$.ajax({
				type : 'POST',
				dataType : 'json',
				url : "./compile.ajax",
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
		setInterval(__.fn.status, 1000);
	});

	return {};
})());