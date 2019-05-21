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