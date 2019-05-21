<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="./particle/top.jsp"></jsp:include>
<style>
#title_txt {
	font-size: 25px;
	height: 40px;
}

#tag_txt {
	font-size: 18px;
	height: 34px;
}

div.titleArea {
	margin-bottom: 15px;
}

select#category_sel {
	height: 34px;
}
</style>
<div class="modal attachment-dialog" tabindex="-1" role="dialog" aria-label="Insert Image" aria-modal="true" style="display: none;">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h4 class="modal-title">Insert Attachment</h4>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close" aria-hidden="true">×</button>
			</div>
			<div class="modal-body">
				<div class="form-group note-form-group note-group-select-from-files">
					<label class="note-form-label">Select from files</label> <input class="note-attach-input note-form-control note-input" type="file" name="files" accept="*" multiple="multiple"> <small>Maximum
						file size : 512 KB</small>
				</div>
				<div class="form-group note-group-attach-url" style="overflow: auto;">
					<label class="note-form-label">Attachment URL</label> <input class="note-attach-url form-control note-form-control note-input  col-md-12" type="text">
				</div>
			</div>
			<div class="modal-footer">
				<input type="button" href="#" class="btn btn-primary note-btn note-btn-primary note-attach-btn" value="Insert Image" disabled="">
			</div>
		</div>
	</div>
</div>
<div class="row" style="margin-bottom: 20px;">
	<div class="col-md-12" style="text-align: right;">
		<button class="btn btn-success" id="add_btn">格納</button>
	</div>
</div>
<article class="entry">
	<div class="titleArea">
		<div class="title" id="article_title">
			<input type='text' class='form-control' id='title_txt' placeholder="title">
		</div>
		<hr class="titileHr">
		<div class="categoryArea">
			<select class="form-control" id="category_sel">
				<c:forEach items="${categorylist}" var="item">
					<option value="${item.value }">${item.text }</option>
				</c:forEach>
			</select>
		</div>
	</div>
	<div class="article">
		<div class="tt_article_useless_p_margin" id="article_contents"></div>
		<hr />
		<div class="list-meta ie-dotum">
			<span class="timeago ff-h dt-published tag-column" id="article_tag"> 
				<input type='text' class='form-control' id='tag_txt' placeholder="tag">
			</span>
		</div>
	</div>
</article>
<jsp:include page="./particle/bottom.jsp"></jsp:include>
<script>
	var _this = (function(obj) {
		obj.init();
		$(obj.onLoad);
		return obj;
	})((function() {
		var maximumImageFileSize = 1024 * 1024;
		function writePost() {
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
		}
		function uploadAttachFile(filename, type, data, cb, er) {
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
		}
		function getBase64Data(data) {
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
		return {
			init : function() {
				$(document).on("change", "input[type=file].note-image-input", function() {
					if ($(this)[0].files[0].size > maximumImageFileSize) {
						toastr.error("big file.");
					}
				});
				$(document).on("click", ".attachment-tools", function() {
					$(".attachment-dialog").modal("show");
				});
				$(document).on("change", "input[type=file].note-attach-input", function() {
					if ($(this)[0].files[0].size > maximumImageFileSize) {
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
							writePost();
						}
					}
					if(count === 0){
						writePost();
					}
					$("img[data-filename]").each(function() {
						var _this = $(this);
						var data = getBase64Data($(this).prop("src"));
						if (data === null) {
							checkNwritePost();
							return;
						}
						uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
							_this.prop("src", data.message);
							checkNwritePost();
						}, function() {
							_this.prop("src", "");
							checkNwritePost();
						});
					});
					$("a.attachfile[data-filename]").each(function() {
						var _this = $(this);
						var data = getBase64Data($(this).prop("href"));
						if (data === null) {
							checkNwritePost();
							return;
						}
						uploadAttachFile($(this).data("filename"), data.type, data.item, function(data) {
							_this.prop("href", data.message);
							checkNwritePost();
						}, function() {
							_this.prop("href", "");
							checkNwritePost();
						});
					});

				});
			},
			onLoad : function() {
				_.loading.on();
				$('#article_contents').summernote({
					height : $(window).height() - 400,
					maximumImageFileSize : maximumImageFileSize
				});
				_.loading.off();
				//attachfile
				var button = $('<button type="button" role="button" tabindex="-1" title="" aria-label="Attachfile" data-original-title="Attachfile"></button>');
				button.addClass("note-btn btn btn-light btn-sm attachment-tools");
				button.append($('<i class="fa fa-paperclip"></i>'));
				$(".note-btn-group.btn-group.note-insert").append(button);
			}
		}
	})());
</script>
<jsp:include page="./particle/bottom.jsp"></jsp:include>