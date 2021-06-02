function escapeHTML(unsafe) {
    return unsafe.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;").replace(/'/g, "&#039;");
};
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
				$("body").toggleClass("mobile-fixed");
			});
			$(".menu-close").on("click", function() {
				$("aside.leftside").removeClass("on");
				$("section.menu-back-layout").addClass("off");
				$("body").removeClass("mobile-fixed");
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
				location.href = "./search.html?query=" + encodeURIComponent($(".search-text").val());
			});
			$(document).on("click", ".search-btn2", function() {
				location.href = "./search.html?query=" + encodeURIComponent($(".search-text").val());
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
	        $("body").bind('copy', function (e) {
	            var url = document.location.href,
	                      decodedUrl = decodeURI(url),
	                      selection = window.getSelection();
	            if (typeof window.getSelection == "undefined") {
	                event.preventDefault();
	                if (window.clipboardData) {
	                    window.clipboardData.setData('Text', selection + '\n\n Reference: [明月の開発ストーリ]' + decodedUrl);
	                }
	                return;
	            }
	            var body_element = document.getElementsByTagName('body')[0];
	            var newdiv = document.createElement('div');
	            newdiv.style.position = 'absolute';
	            newdiv.style.left = '-99999px';
	            body_element.appendChild(newdiv);
	            newdiv.appendChild(selection.getRangeAt(0).cloneContents());
	            newdiv.innerHTML += '<br /><br />Reference: <a href="' + url + '">' + decodedUrl + '</a> [明月の開発ストーリ]';
	            selection.selectAllChildren(newdiv);
	            window.setTimeout(function () {
	                body_element.removeChild(newdiv);
	            }, 1);
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