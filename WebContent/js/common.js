var _ = (function(obj) {
	$(obj.onLoad);
	return obj;
})((function() {
	return {
		onLoad : function() {
			$(".menu-toggle").on("click", function() {
				$this = $(this);
				$("aside.leftside").toggleClass("on");
				$("section.menu-back-layout").toggleClass("off");
			});
			$(".menu-close").on("click", function() {
				$("aside.leftside").removeClass("on");
				$("section.menu-back-layout").addClass("off");
			});
			$(document).on("click", ".link-item-collapse", function() {
				var $icon = $(this).find("span.fa");
				if ($icon.hasClass("fa-chevron-down")) {
					$icon.removeClass("fa-chevron-down");
					$icon.addClass("fa-chevron-up");
					$icon.closest("li").find(".sub_category_list").removeClass("off");
				} else if ($icon.hasClass("fa-chevron-up")) {
					$icon.removeClass("fa-chevron-up");
					$icon.addClass("fa-chevron-down");
					$icon.closest("li").find(".sub_category_list").addClass("off");
				}
			});
			$(document).on("change", ".search-text", function() {
				$val = $(this).val();
				$(".search-text").val($val);
			});
			$(document).on("click", ".search-btn", function() {
				location.href = "./search.html?query=" + $(".search-text").val();
			});
			var modal = document.getElementById('myModal');
	        $("img").on("click", function () {
	            $("#ImgModal").show();
	            $("#ImgModal .modal-content").attr("src", this.src);
	        });
	        $("#ImgModal").on("click", function () {
	            $("#ImgModal").hide();
	        });
	        $("#ImgModal .close").on("click", function () {
	            $("#ImgModal").hide();
	        });
		},
		loading : {
			on : function() {
				$(".loader").removeClass("off");
				$(".loader-layout").removeClass("off");
			},
			off : function() {
				$(".loader").addClass("off");
				$(".loader-layout").addClass("off");
			}
		}
	}
})());