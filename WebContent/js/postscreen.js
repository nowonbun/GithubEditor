"use strict";
(function (obj) {
    $(obj.onLoad);
})((function () {
    var __ = {
        property: {},
        fn: {},
        ev: function () {
            $("pre code.hljs").each(function () {
                $(this).before($("<a href='javascript:void(0);' class='code-copy'><i class='fa fa-copy'></i>Copy!</a>"));
                $(this).before($("<div class='code-title'></div>").append($("<i class='fa fa-minus-square code-collapse'></i>"))
                    .append("&nbsp;[Source view]&nbsp;" + $(this).data("type")));
                $(this).parent().addClass("code-view");
            });
            $(document).on("click", ".code-title", function () {
                var $this = $(this);
                var $i = $this.find("i.code-collapse");
                if ($i.hasClass("fa-plus-square")) {
                    $i.removeClass("fa-plus-square");
                    $i.addClass("fa-minus-square");
                    $this.parent().removeClass("code-view-disabled");
                }
                else {
                    $i.removeClass("fa-minus-square");
                    $i.addClass("fa-plus-square");
                    $this.parent().addClass("code-view-disabled");
                }
            });
            $(document).on("click", '.code-copy', function () {
                var $parent = $(this).closest(".code-view");
                if ($parent.hasClass("code-view-disabled")) {
                    var $i = $parent.find("i.code-collapse");
                    $i.removeClass("fa-plus-square");
                    $i.addClass("fa-minus-square");
                    $parent.removeClass("code-view-disabled");
                }
                toastr.success("", "コピーされました。", { timeOut: 700 });
                var code_element = $(this).closest("pre").find("code")[0];
                var value = code_element.innerText.replace(/\n\n\n/ig, '').replace('    \n', '').replace(/\t\n/ig, '\n');
                var selection = window.getSelection();
                var body_element = document.getElementsByTagName('body')[0];
                var newdiv = document.createElement('div');
                newdiv.style.position = 'absolute';
                newdiv.style.left = '-10000px';
                newdiv.style.top = '-10000px';
                body_element.appendChild(newdiv);
                newdiv.innerHTML = "<pre>" + escapeHTML(value) + "</pre>";
                selection === null || selection === void 0 ? void 0 : selection.selectAllChildren(newdiv);
                setTimeout(function () {
                    newdiv.remove();
                }, 10000);
                document.execCommand('copy');
            });
        }
    };
    $(__.ev);
    return {
        onLoad: function () {
        }
    };
})());
