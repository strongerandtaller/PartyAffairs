$(function() {
	/*var curMenu = null, zTree_Menu = null;*/
	var setting = {
		view : {
			showLine : false,
			showIcon : false,
			selectedMulti : false,
			dblClickExpand : false,
			addDiyDom : addDiyDom
		},
		edit : {
			enable : true,
			showRemoveBtn : false,
			showRenameBtn : false
		},
		data : {
			keep : {
				parent : true,
				leaf : true
			},
			simpleData : {
				enable : true
			}
		},
		callback : {
			beforeClick : beforeClick,
			beforeDrag : beforeDrag,		
			beforeRename : beforeRename,
			onRename : onRename,
			onClick : zTreeOnClick
		}
	};

	var zNodes = [ {
		id : 0,
		pId : 0,
		name : "所有题库",
		open : true
		},
	];

	$.ajax({
		type : "get",
		url : "../../examcategory",
		async : false,
		dataType : 'json',
		success : function(result) {
			if (result.status == 0) {
				data = result.data;
				// alert("查询成功");
				$.each(data, function(index, item) {
					zNodes.push({
						id : item.categoryId, // 本身id
						pId : 0, // 父级id
						name : item.categoryName
					// 显示的名称
					});
				})

			} else {
				alert("获取题库列表失败！");
			}
		}
	});

	function zTreeOnClick(event, treeId, treeNode) {
		var examcategoryId = treeNode.id;
		var name = treeNode.name;
		$.ajax({
			type : "get",
			url : "../../exambank/"+examcategoryId,
			async : false,
			dataType : 'json',
			success : function(result) {
				if (result.status == 0) {
					data = result.data;					
					previewLib(data, name);
				} else {
					alert("题库内容为空！");
				}
			}
		});	
		//alert(treeNode.tId + ", " + treeNode.name);
	}

	function addDiyDom(treeId, treeNode) {
		var spaceWidth = 5;
		var switchObj = $("#" + treeNode.tId + "_switch"), icoObj = $("#"
				+ treeNode.tId + "_ico");
		switchObj.remove();
		icoObj.before(switchObj);

		if (treeNode.level > 1) {
			var spaceStr = "<span style='display: inline-block;width:"
					+ (spaceWidth * treeNode.level) + "px'></span>";
			switchObj.before(spaceStr);
		}
	}

	function beforeClick(treeId, treeNode) {
		if (treeNode.level == 0) {
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			zTree.expandNode(treeNode);
			return false;
		}
		return true;
	}
	
	var log, className = "dark";
	function beforeDrag(treeId, treeNodes) {
		return false;
	}
	
	function onRename(event, treeId, treeNode, isCancel){
		alert(treeNode.id+"   "+treeNode.name);
		$.ajax({
			type : "PUT",
			url : "../../examcategory/"+treeNode.id+"?categoryName="+treeNode.name,
			async : false,
			dataType : 'json',
			success : function(result) {
				if (result.status == 0) {
					var treeObj = $("#treeDemo");
					var zNodes1 = [ {
						id : 0,
						pId : 0,
						name : "所有题库",
						open : true
						},
					];

					$.ajax({
						type : "get",
						url : "../../examcategory",
						async : false,
						dataType : 'json',
						success : function(result) {
							if (result.status == 0) {
								data = result.data;
								// alert("查询成功");
								$.each(data, function(index, item) {
									zNodes1.push({
										id : item.categoryId, // 本身id
										pId : 0, // 父级id
										name : item.categoryName
									// 显示的名称
									});
								})

							} else {
								alert("获取题库列表失败！");
							}
						}
					});
					$.fn.zTree.init(treeObj, setting, zNodes1);

				} else {
					alert("删除题库失败！");
				}
			}
		});
	}
	
	function beforeRename(treeId, treeNode, newName) {
		if (newName.length == 0) {
			alert("节点名称不能为空.");
			var zTree = $.fn.zTree.getZTreeObj("treeDemo");
			setTimeout(function() {
				zTree.editName(treeNode)
			}, 10);
			return false;
		}
		return true;
	}
	
	function showLog(str) {
		if (!log)
			log = $("#log");
		log.append("<li class='" + className + "'>" + str + "</li>");
		if (log.children("li").length > 8) {
			log.get(0).removeChild(log.children("li")[0]);
		}
	}
	
	function getTime() {
		var now = new Date(), h = now.getHours(), m = now.getMinutes(), s = now
				.getSeconds(), ms = now.getMilliseconds();
		return (h + ":" + m + ":" + s + " " + ms);
	}

	//编辑
	function edit() {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo"), nodes = zTree
				.getSelectedNodes(), treeNode = nodes[0];
		if (nodes.length == 0) {
			alert("请先选择一个节点");
			return;
		}
		zTree.editName(treeNode);
		$("input[id*='treeDemo_']").height(25);
		$("input[id*='treeDemo_']").width(135);
		$("input[id*='treeDemo_']").change(function() { //题库分类修改监听
			console.log("$('input[id*='treeDemo_']').val()" + $(this).val());
		});
		/*$.ajax({
			type : "PUT",
			url : "../../examcategory/"+treeNode.id+"?categoryName="+treeNode.name,
			async : false,
			dataType : 'json',
			success : function(result) {
				if (result.status == 0) {
					var treeObj = $("#treeDemo");
					var zNodes1 = [ {
						id : 0,
						pId : 0,
						name : "所有题库",
						open : true
						},
					];

					$.ajax({
						type : "get",
						url : "../../examcategory",
						async : false,
						dataType : 'json',
						success : function(result) {
							if (result.status == 0) {
								data = result.data;
								// alert("查询成功");
								$.each(data, function(index, item) {
									zNodes1.push({
										id : item.categoryId, // 本身id
										pId : 0, // 父级id
										name : item.categoryName
									// 显示的名称
									});
								})

							} else {
								alert("获取题库列表失败！");
							}
						}
					});
					$.fn.zTree.init(treeObj, setting, zNodes1);

				} else {
					alert("删除题库失败！");
				}
			}
		});*/
	}
	
	function remove(e) {
		var zTree = $.fn.zTree.getZTreeObj("treeDemo"), 
			nodes = zTree.getSelectedNodes(), 
			treeNode = nodes[0];
		if (nodes.length == 0) {
			alert("请先选择一个节点");
			return;
		}
		var text="确定要删除题库"+treeNode.name+"吗？";
		document.getElementById('show_msg').innerHTML=text;
		$('.popup_de').addClass('bbox');
		$('.popup_de .btn-danger').one('click',function(){
			//zTree.removeNode(treeNode, false);
			$.ajax({
				type : "DELETE",
				url : "../../examcategory/"+treeNode.id,
				async : false,
				dataType : 'json',
				success : function(result) {
					if (result.status == 0) {
						var treeObj = $("#treeDemo");
						var zNodes1 = [ {
							id : 0,
							pId : 0,
							name : "所有题库",
							open : true
							},
						];

						$.ajax({
							type : "get",
							url : "../../examcategory",
							async : false,
							dataType : 'json',
							success : function(result) {
								if (result.status == 0) {
									data = result.data;
									// alert("查询成功");
									$.each(data, function(index, item) {
										zNodes1.push({
											id : item.categoryId, // 本身id
											pId : 0, // 父级id
											name : item.categoryName
										// 显示的名称
										});
									})

								} else {
									alert("获取题库列表失败！");
								}
							}
						});
						$.fn.zTree.init(treeObj, setting, zNodes1);

					} else {
						alert("删除题库失败！");
					}
				}
			});
			
			$('.popup_de').removeClass('bbox');
		});
	}

	$(document).ready(function() {
		var treeObj = $("#treeDemo");
		$.fn.zTree.init(treeObj, setting, zNodes);
		$("#edit").bind("click", edit);
		$("#remove").bind("click", remove);
	});

	//清除弹窗原数据
	$("#previewModal").on("hidden.bs.modal", function() {
		$(this).removeData("bs.modal");
	});
	$('#previewModal').on('hide.bs.modal', function() {
		$(this).removeData("bs.modal");
	})

	$("tbody tr").on('click', function() {
		var check = $(this).find("input[type='checkbox']");
		if ($(check).is(':checked')) {
			$(check).prop('checked', false);
		} else {
			$(check).prop('checked', true);
		}
	})
	
	//弹出框取消按钮事件
	$('.popup_de .btn_cancel').click(function(){
		$('.popup_de').removeClass('bbox');
	});
	//弹出框关闭按钮事件
	$('.popup_de .popup_close').click(function(){
		$('.popup_de').removeClass('bbox');
	})

	//预览题库
	function previewLib(data, name) {
		var test_id1 = name;
		//console.log("name:"+name+":"+test_id1)
		test1(test_id1, data);
	}

	
	function test1(test_id, data) {
		if (test_id != "") {
			//console.log(data);
			var titleB = test_id;
			var sq = data.singleQuestion;
			var mq = data.multipleQuestion;

			var test_box = '';
			var sqtopic_box = '';
			$.each(sq,function(h, sq) {
				var title = sq.questionContent;
				var options = sq.choice;
				var answer = sq.answer;
				var option_box = '';
				$.each(options,function(j, option) {
					var op = convert(j);
					option_box += '<div class = "jxz-option radio" >'
					+ '<label >'
					+ '<input name = "test'
					+ h
					+ ''
					+ j
					+ '" type = "radio" value = "'
					+ op
					+ '" > '
					+ option
					+ '</label>'
					+ '</div >';
				});
				var answer_op = '';
				if (answer == 1) {
					answer_op += 'A';
				} else if (answer == 2) {
					answer_op += 'B';
				} else if (answer == 3) {
					answer_op += 'C';
				} else if (answer == 4) {
					answer_op += 'D';
				}

				sqtopic_box += '<div class = "testCon"  data-type = "sq" data-answer="'
						+ answer_op
						+ '">'
						+ '<h4 class = "jxz-title" >'
						+ sq.questionContent
						+ '</h4>'
						+ option_box
						+ '<div class="topic-answer"><p>正确答案：'
						+ answer_op + '</p>' + '</div></div>';

			});

			var mqtopic_box = '';
			$.each(mq,function(h, mq) {
				var title = mq.questionContent;
				var options = mq.choice;
				var answer = mq.answer;
				var option_box = '';
				$.each(options,function(j, option) {
					var op = convert(j);
					option_box += '<div class = "jxz-option radio" >'
							+ '<label >'
							+ '<input name = "test'
							+ h
							+ ''
							+ j
							+ '" type = "radio" value = "'
							+ op
							+ '" > '
							+ option
							+ '</label>'
							+ '</div >';
				});
				var answer_op = '';

				$.each(answer, function(i, aw) {
					if (aw == 1) {
						answer_op += (i + 1) ? 'A' : 'A ';
					} else if (aw == 2) {
						answer_op += (i + 1) ? 'B' : 'B ';
					} else if (aw == 3) {
						answer_op += (i + 1) ? 'C' : 'C ';
					} else if (aw == 4) {
						answer_op += (i + 1) ? 'D' : 'D ';
					}
					/*answer_op += answer.length == (i + 1) ? aw : aw + " ";*/
				});

				mqtopic_box += '<div class = "testCon"  data-type = "sq" data-answer="'
						+ answer_op
						+ '">'
						+ '<h4 class = "jxz-title" >'
						+ mq.questionContent
						+ '</h4>'
						+ option_box
						+ '<div class="topic-answer"><p>正确答案：'
						+ answer_op + '</p>' + '</div></div>';

			});
			test_box += '<div class="jxz-box"><h4 class="tesTitle">单项选择题</h4 >'
					+ sqtopic_box + '</div>'
					+ '<div class="jxz-box"><h4 class="tesTitle">多项选择题</h4 >'
					+ mqtopic_box + '</div>';

			var test_html = '<div class="page-header"><h3 class="text-center">'
					+ titleB + '</h3></div>'
					+ '<div class="test-form-box" title="双击进入编辑">' + test_box
					+ '</div>';
			$('#previewArea').html(test_html)
		} else {
			alert("试题获取失败！");
		}
	}

})
var mySelect = new Vue({
	el : '#mySelect',
	data : {
		select : []
	},
	created : function() {
		this.loadExamcategory();
	},
	methods : {
		loadExamcategory : function() {
			var self = this;
			$.ajax({
				type : "get",
				url : "../../examcategory",
				async : false,
				dataType : 'json',
				success : function(result) {
					if (result.status == 0) {
						self.select = result.data;
					} else {
						alert(result.msg);
					}
				}
			})
		},
		setSelect : function() {
			this.$nextTick(function() {
				var length = $('#mySelect').find("option").length;
				$('#mySelect').get(0).selectedIndex = length - 1;
			})
		}
	}
})

function addlibary() {//题库上传
	$("#myModal").modal('show');
};



$('#tikutitle').bind("change", function() {
	console.log("$('#tikutitle') was clicked");
	checkisnull();
});
$("#chooseExam").change(
		function() {
			var filename = $(this).val().substring(
					$(this).val().lastIndexOf("\\") + 1);
			//检测上传文件的类型 

			var file = $(this).val();
			var strFileName = file.replace(
					/^.+?\\([^\\]+?)(\.[^\.\\]*?)?$/gi, "$1"); //正则表达式获取文件名，不带后缀
			var FileExt = file.replace(/.+\./, "");//后缀名

			if (FileExt != 'xls' && FileExt != 'xlsx') {
				$("#chooseExam").val("");
				$('#tikutitle').val("");
				alert("只能上传.xls  .xlsx 类型的文件!");
				return;

			} else {
				if (!$('#tikutitle').val()) {
					$('#tikutitle').val(strFileName);
				}
				$(".fl").text($(this).val());
				checkisnull();
				return;
			}

		});
$("#mySelect").bind("change", function() {
	console.log("$('#mySelect') was clicked");
	checkisnull();
});
//检查输入框是否为空
function checkisnull() {
	var title = $('#tikutitle').val();
	var select = $("#mySelect").val();
	var file = $("#chooseExam").val();
	//alert("1          "+title + select + file);
	if (title && select != "请选择文件分类" && file) {
		$('#mUploadbtn').attr('disabled', false);
		//alert("2          "+title + select + file);
	} else {
		//alert("3          "+title + select + file);
		$('#mUploadbtn').attr('disabled', true);
	}
}
;

//题库上传按钮
$("#mUploadbtn").click(function() {
	var formData = new FormData();
	var sendFile = $('#chooseExam').get(0).files[0];
	var select = $('#mySelect').val();
	formData.append("file", sendFile);
	$.ajax({
		type : 'post',
		url : '../../exambank/upload/' + select,
		cache : false,
		data : formData,
		processData : false,
		contentType : false,
		dataType : 'json', //请求成功后，后台返回图片访问地址字符串，故此以text格式获取，而不是json格式
		success : function(result) {
			if (result.status == 0) {
				alert("上传文件成功，请预览");
				var test_id = 'get a test id';
				test(test_id, result.data);
				ExamData = result.data;
			} else {
				alert("上传失败，请重新上传");
			}
		},
		error : function() {
			alert("上传失败");
		}
	});
	$("#previewModal").modal('show');
});
//清除弹窗原数据
$("#previewModal").on("hidden.bs.modal", function() {
	$(this).removeData("bs.modal");
});
$('#previewModal').on('hide.bs.modal', function() {
	$(this).removeData("bs.modal");
})

/*$("tbody tr").on('click' , function(){
var check = $(this).find("input[type='checkbox']");
if ($(check).is(':checked')) {
	$(check).prop('checked', false);
} else {
	$(check).prop('checked', true);
}
 })*/

//文件上传限制
function fileChange(target) {
	//检测上传文件的类型 
	var fileName = document.all.up_file.value;
	var ext, idx;
	if (fileName == '') {
		document.all.submit_upload.disabled = true;
		alert("请选择需要上传的文件!");
		return;
	} else {
		idx = fileName.lastIndexOf(".");
		if (idx != -1) {
			ext = imgName.substr(idx + 1).toUpperCase();
			ext = ext.toLowerCase();
			// alert("ext="+ext);
			if (ext != 'xls' && ext != 'xlsx') {
				target.value = "";
				alert("只能上传.xls  .xlsx 类型的文件!");
				return;
			}
		} else {
			console.log("target.value=" + target.value);
			return;
		}
	}

	//检测上传文件的大小        
	var isIE = /msie/i.test(navigator.userAgent) && !window.opera;
	var fileSize = 0;
	if (isIE && !target.files) {
		var filePath = target.value;
		var fileSystem = new ActiveXObject("Scripting.FileSystemObject");
		var file = fileSystem.GetFile(filePath);
		fileSize = file.Size;
	} else {
		fileSize = target.files[0].size;
	}

	var size = fileSize / 1024 * 1024;

	if (size > (1024 * 200)) {
		document.all.submit_upload.disabled = true;
		alert("文件大小不能超过200KB");
	} else {
		document.all.submit_upload.disabled = false;
	}
}

//自定义文件分类和文件分类选择切换
var customGroup = document.getElementById("custom-group");
var selectGroup = document.getElementById("select-group");
function custom() {

	customGroup.style.display = "flex";
	selectGroup.style.display = "none";
};

function addsort() {//添加自定义分类
	customGroup.style.display = "none";
	selectGroup.style.display = "flex";
	var sort = $.trim($('#sort').val());
	if (sort == "") {
		alert("不能为空");
		return;
	}
	$.ajax({
		type : 'post',
		url : '../../examcategory/' + sort,
		dataType : 'json',
		async : false,
		success : function(result) {
			checkisnull();
		},
		error : function() {
			checkisnull();
			alert("系统出错，添加失败！");
		}
	});
	mySelect.loadExamcategory();
	mySelect.setSelect();
	checkisnull();
};
var ExamData;
function submitExam() {
	console.log(ExamData);
	$.ajax({
		type : 'post',
		url : '../../exambank/save',
		data : JSON.stringify(ExamData),
		contentType : "application/json",
		dataType : 'json', //请求成功后，后台返回图片访问地址字符串，故此以text格式获取，而不是json格式
		success : function(result) {
			if (result.status == 0) {
				alert("上传试题成功");
				location.reload();
			} else {
				alert("上传试题成功失败，请联系管理员");
				location.reload();
			}
		},
		error : function() {
			alert("上传试题成功失败，请联系管理员");
		}
	});
}


