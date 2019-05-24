var _this = (function(obj) {
		return obj;
	})((function() {
		var  __ = {};
		__.property = {
			maximumImageFileSize: 1024 * 1024	
		};
		
		__.fn = {
			writePost:function () {
				$.ajax({
					type : 'POST',
					dataType : 'json',
					data : {
						title : $.trim($('#title_txt').val()),
						category : $('#category_sel').val(),
						contents : $('#article_contents').summernote('code'),
						tags : $.trim($('#tag_txt').val())
					},
					url : "./createPost.ajax",
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
			uploadAttachFile: function (filename, type, data, cb, er) {
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
						toastr.error("예상치 못한 에러가 발생했습니다. 로그를 확인해 주십시오.");
						er.call(this, data);
					},
					complete : function(jqXHR, textStatus) {

					}
				});
			},
			getBase64Data: function (data) {
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
			} 
		}
		
		__.ev = function() {
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
				//https://summernote.org/deep-dive/#insertimage
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

			$('#add_btn').on('click', function() {
				if ($.trim($('#title_txt').val()) === "") {
					toastr.error("empty title");
					return;
				}
				_.loading.on();
				var state = 0;
				var count = $("img[data-filename]").length + $("a.attachfile[data-filename]").length;
				function checkNwritePost() {
					state++;
					if (state === count) {
						__.fn.writePost();
					}
				}
				if(count === 0){
					__.fn.writePost();
				}
				$("img[data-filename]").each(function() {
					var _this = $(this);
					var data = __.fn.getBase64Data($(this).prop("src"));
					if (data === null) {
						checkNwritePost();
						return;
					}
					__.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
						_this.prop("src", data.message);
						checkNwritePost();
					}, function() {
						_this.prop("src", "");
						checkNwritePost();
					});
				});
				$("a.attachfile[data-filename]").each(function() {
					var _this = $(this);
					var data = __.fn.getBase64Data($(this).prop("href"));
					if (data === null) {
						checkNwritePost();
						return;
					}
					__.fn.uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
						_this.prop("href", data.message);
						checkNwritePost();
					}, function() {
						_this.prop("href", "");
						checkNwritePost();
					});
				});

			});
		}
		$(__.ev);
		$(function(){
			_.loading.on();
			var node_height = $(window).height() - 400;
			if(node_height < 250){
				node_height = 250;
			}
			$('#article_contents').summernote({
				height : node_height,
				maximumImageFileSize : __.property.maximumImageFileSize,
				callbacks:{
					onInit: function(){
						//attachfile
						var button = $('<button type="button" role="button" tabindex="-1" title="" aria-label="Attachfile" data-original-title="Attachfile"></button>');
						button.addClass("note-btn btn btn-light btn-sm attachment-tools");
						button.append($('<i class="fa fa-paperclip"></i>'));
						button.on("click", function(){
							$(".attachment-dialog").modal("show");
						});
						$(".note-btn-group.btn-group.note-insert").append(button);			
					}
				}
			});
			_.loading.off();
		});
		
		return {
		}
	})());