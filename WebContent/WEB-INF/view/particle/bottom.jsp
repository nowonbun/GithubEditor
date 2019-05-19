<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	</div>
</body>
<script>
	var _ = (function(obj) {
		obj.init();
		$(obj.onLoad);
		return obj;
	})({
		init : function() {

		},
		onLoad : function() {

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
	})
</script>
</html>