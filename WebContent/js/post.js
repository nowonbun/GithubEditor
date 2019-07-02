var _this = (function(obj) {
	return obj;
})((function() {
	var __ = {};
	__.property = {
		maximumImageFileSize : 1024 * 1024,
		modifyMode : false,
		originalData : JSON.parse($("#originalData").val())
	}
	__.fn = {
		changeModifyMode : function() {
			$("#article_title").html($("<input type='text' class='form-control' id='title_txt' placeholder='title'>").val(__.property.originalData.title));
			$(".categoryArea").html($($("#categoryAreaTemplate").html()).prop("id", "category_sel"));
			var tags = "";
			if (__.property.originalData.tags !== undefined) {
				tags = __.property.originalData.tags;
			}
			$("#article_tag").html($("<input type='text' class='form-control' id='tag_txt' placeholder='tag'>").val(tags));
			var node_height = $(window).height() - 400;
			if (node_height < 250) {
				node_height = 250;
			}
			$('#article_contents').summernote({
				height : node_height,
				maximumImageFileSize : __.property.maximumImageFileSize,
				callbacks : {
					onInit : function() {
						// attachfile
						var button = $('<button type="button" role="button" tabindex="-1" title="" aria-label="Attachfile" data-original-title="Attachfile"></button>');
						button.addClass("note-btn btn btn-light btn-sm attachment-tools");
						button.append($('<i class="fa fa-paperclip"></i>'));
						button.on("click", function() {
							$(".attachment-dialog").modal("show");
						});
						$(".note-btn-group.btn-group.note-insert").append(button);
					}
				}
			});
			__.property.modifyMode = true;
		},
		modifyPost : function() {
			$.ajax({
				type : 'POST',
				dataType : 'json',
				data : {
					idx : __.property.originalData.idx,
					title : $.trim($('#title_txt').val()),
					category : $('#category_sel').val(),
					contents : $('#article_contents').summernote('code'),
					tags : $.trim($('#tag_txt').val())
				},
				url : "./modifyPost.ajax",
				success : function(data) {
					if (data.ret) {
						location.href = data.message;
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("예상치 못한 에러가 발생했습니다. 로그를 확인해 주십시오.");
				},
				complete : function(jqXHR, textStatus) {
					_.loading.off();
				}
			});
		},
		uploadAttachFile : function(filename, type, data, cb, er) {
			$.ajax({
				type : 'POST',
				dataType : 'json',
				data : {
					filename : filename,
					type : type,
					data : data
				},
				url : "./addAttachFile.ajax",
				success : function(data) {
					cb.call(this, data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("エラーが発生しました。ログを確認してください。");
					er.call(this, data);
				},
				complete : function(jqXHR, textStatus) {

				}
			});
		},
		getBase64Data : function(data) {
			var item = data.split(",");
			if (item.length != 2) {
				return null;
			}
			var type = item[0].split(";");
			if (type.length != 2) {
				return null;
			}
			if (type[1] !== 'base64') {
				return null;
			}
			return {
				type : type[0],
				item : item[1]
			}
		},
		updatePost : function() {
			if ($.trim($('#title_txt').val()) === "") {
				toastr.error("empty title");
				return;
			}
			_.loading.on();
			var state = 0;
			var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
			function checkNmodifyPost() {
				state++;
				if (state === count) {
					__.fn.modifyPost();
				}
			}
			if (count === 0) {
				__.fn.modifyPost();
			}
			$("img[data-filename]").each(function() {
				var $this = $(this);
				var data = __.fn.getBase64Data($(this).prop("src"));
				if (data === null) {
					checkNmodifyPost();
					return;
				}
				__.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
					$this.prop("src", data.message);
					checkNmodifyPost();
				}, function() {
					$this.prop("src", "");
					checkNmodifyPost();
				});
			});
			$("a.attachfile[data-filename]").each(function() {
				var $this = $(this);
				var data = __.fn.getBase64Data($(this).prop("href"));
				if (data === null) {
					checkNmodifyPost();
					return;
				}
				__.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
					$this.prop("href", data.message);
					checkNmodifyPost();
				}, function() {
					$this.prop("href", "");
					checkNmodifyPost();
				});
			});
		}
	}

	__.ev = function() {
		$("#modify_btn").on("click", function() {
			if (!__.property.modifyMode) {
				__.fn.changeModifyMode();
			} else {
				__.fn.updatePost();
			}
		});
		$("#delete_btn").on("click", function(){
			_.loading.on();
			$.ajax({
				type : 'POST',
				dataType : 'json',
				data : {
					idx : __.property.originalData.idx
				},
				url : "./deletePost.ajax",
				success : function(data) {
					if (data.ret) {
						location.href = data.message;
					}
				},
				error : function(jqXHR, textStatus, errorThrown) {
					console.log(jqXHR);
					console.log(errorThrown);
					toastr.error("エラーが発生しました。ログを確認してください。");
				},
				complete : function(jqXHR, textStatus) {
					_.loading.off();
				}
			});
		});
		$("input[type=file].note-image-input").on("change", function() {
			if ($(this)[0].files[0].size > __.property.maximumImageFileSize) {
				toastr.error("big file.");
			}
		});
		$("input[type=file].note-attach-input").on("change", function() {
			if ($(this)[0].files[0].size > __.property.maximumImageFileSize) {
				toastr.error("big file.");
				$("input[type=file].note-attach-input").val("");
				$(".attachment-dialog").modal("hide");
				return;
			}
			// https://summernote.org/deep-dive/#insertimage
			var file = $(this)[0].files[0];
			var filename = file.name;
			var reader = new FileReader();
			reader.onload = function(e) {
				var node = document.createElement('p');
				$(node).append($("<a class='attachfile'><img src='./img/zip.gif'> " + filename + "</a>").attr("href", reader.result).attr("data-filename", filename));
				$('#article_contents').summernote('insertNode', node);
				$("input[type=file].note-attach-input").val("");
				$(".attachment-dialog").modal("hide");
			}
			reader.readAsDataURL(file);
		});
		/*$("pre code.hljs").each(function () {
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
        });*/
	}
	$(__.ev);
	return {}
})());