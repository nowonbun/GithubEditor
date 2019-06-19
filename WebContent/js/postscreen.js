var _this = (function(obj) {
	return obj;
})((function() {
	var __ = {};
	__.property = {}
	__.fn = {}
	__.ev = function() {
		$("pre code.hljs").each(function () {
            $(this).before($("<div class='code-title'></div>").append($("<i class='fa fa-minus-square code-collapse'></i>"))
                .append("&nbsp;[Source view]&nbsp;" + $(this).data("type")));
            $(this).parent().addClass("code-view");
        });
        $(document).on("click", ".code-title", function () {
            $this = $(this);
            $i = $this.find("i.code-collapse");
            if ($i.hasClass("fa-plus-square")) {
                $i.removeClass("fa-plus-square");
                $i.addClass("fa-minus-square");
                $this.parent().removeClass("code-view-disabled");
            } else {
                $i.removeClass("fa-minus-square");
                $i.addClass("fa-plus-square");
                $this.parent().addClass("code-view-disabled");
            }
        });
	}
	$(__.ev);
	return {}
})());