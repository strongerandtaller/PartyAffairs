function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]); return null; 
}
var type=getUrlParam('type');//文章类型
var datatype;
new Vue({
	el : "#msg",
	data: {
		datas: [],
		datatype:[]
	},
	methods:{
		loadNewMessages: function() {
			var url;
			if(type=="news"){
				url = "../../publicityManage/newsList";
				datatype = 1;
			}else if(type=="public"){
				url = "../../publicityManage/publicNoticesList";
				datatype = 2;
			}else if(type=="party"){
				url = "../../publicityManage/partyNoticesList";
				datatype = 3;
			}
			var app = this;
			$.ajax({
				type:"get",
				url: url,
				async : false,
				dataType: 'json',
				success: function(result){
					if (result.status == 0) {
						app.datas = result.data;
						app.datatype = datatype;
					}else{
						alert(result.msg);
					}
				}
			});
		},
		titleFormat: function(msg){
			// 长度超过12，截取12个字符
			if(msg.length<=12){
				return msg;
			}				
			return msg.substr(0,11)+"···";
		}
	},
	created: function () {
		this.loadNewMessages();
	}
});
  $(document).ready(function() {
	var table = $('#msg').DataTable({
        responsive: true,
        "bProcessing" : true, //DataTables载入数据时，是否显示‘进度’提示  
        "bAutoWidth" : true, //是否自适应宽度  
        "aLengthMenu" : [5, 10, 20], //更改显示记录数选项 
        "iDisplayLength" : 5, //默认显示的记录数  
        "aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0,7 ] }],
        "order": [[ 1, 'asc' ]],
        "sDom": '<"row" <"col-sm-8" <"#add">> <"col-sm-4" <"row" <"col-sm-6" l> <"col-sm-6" f>>>>t<"row" <"col-sm-6" i> <"col-sm-6" p>>',
        language: {
            search: "搜索:",
            sInfo: "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
            sLengthMenu: "显示 _MENU_ 项结果",
            sZeroRecords: "没有匹配结果",
            sInfoEmpty: "显示第 0 至 0 项结果，共 0 项",
            sInfoFiltered: "(由 _MAX_ 项结果过滤)",
            sEmptyTable: "表中数据为空",
            oPaginate: {
                sPrevious: "上页",
                sNext: "下页",
            }
        },
        "fnDrawCallback": function() {
        	$(this).find('input[type=checkbox]').removeAttr('checked');
		},
        
       
		
    });
	
	var button = "<button type='button' class='btn btn-success tp' onclick='window.parent.aclick()'><span class='glyphicon glyphicon-plus icon'></span><span class='caption'>添加文章</span></button> &nbsp;&nbsp;&nbsp;&nbsp;"
		+"<button type='button' class='btn btn-primary tp'  id='allcheck' onclick='checkall()'><span class='fa fa-check-square-o icon'></span><span class='caption'>全选</span></button> &nbsp;&nbsp;&nbsp;&nbsp;"
		+"<a class='btn btn-danger td' onclick='deleteall()'><span class='fa fa-trash-o icon'></span><span class='caption'>撤稿</span></a> &nbsp;&nbsp;&nbsp;&nbsp;"
		+"<a class='btn btn-warning tw' onclick='passall()'><span class='fa fa-pencil-square-o'></span><span class='caption' id='allpass'>审核通过</span></a>";
	document.getElementById("add").innerHTML = button;
	$('#msg tbody').on( 'mouseenter', 'td', function () {
	var colIdx = table.cell(this).index().column;
	$( table.cells().nodes() ).removeClass( 'highlight' );
	$( table.column( colIdx ).nodes() ).addClass( 'highlight' );
	} );
	$("#all").click(function(){
		$('[name=all]:checkbox').prop('checked',this.checked);//checked为true时为默认显示的状态
	});

});   
function cancel(){
	$('.popup_de').removeClass('bbox');
}
function checkall(){
	var all = $('[name=all]:checkbox');
	for(var i=0;i<all.length;i++){
		all[i].checked=!all[i].checked;
	}
}
function passall(){
	var all = $('[name=all]:checkbox');
	var str = "";
	for(var i=1;i<all.length;i++){
		if(all[i].checked)
			str = str+"&"+all[i].value;
	}
	var text="是否审核通过所选文章吗?";
	document.getElementById('show_msg').innerHTML=text;
	$('.popup_de').addClass('bbox');
	$('.popup_de .btn-danger').one('click',function(){
		if(str!=""){
			//alert(str);
			doPass(str);
		}
		else{
			alert("请至少选择一个文章");
			$('.popup_de').removeClass('bbox');
		}
	})
}
function pass(obj){
	var tds = $(obj).parent().parent().parent().find('td');
	var label = tds.eq(1).find('label');
	var sid = label.eq(0).text();
	var text="是否审核通过该文章吗?";
	document.getElementById('show_msg').innerHTML=text;
	$('.popup_de').addClass('bbox');
	$('.popup_de .btn-danger').one('click',function(){
		doPass(sid);
	})
}
function deleteall(){
	var all = $('[name=all]:checkbox');
	var str = "";
	for(var i=1;i<all.length;i++){
		if(all[i].checked)
			str = str+"&"+all[i].value;
	}
	var text="确定要撤稿所选文章吗?";
	document.getElementById('show_msg').innerHTML=text;
	$('.popup_de').addClass('bbox');
	$('.popup_de .btn-danger').one('click',function(){
		if(str!="")
			doDelete(str);
		else{
			alert("请至少选择一篇文章");
			$('.popup_de').removeClass('bbox');
		}		
	})
}
function millisecondsToDateTime(ms){
	return new Date(ms).toLocaleString();
};
function deletemsg(obj){
	var tds = $(obj).parent().parent().parent().find('td');
	var label = tds.eq(1).find('label');
	var sid = label.eq(0).text();
	var text="确定要撤稿该文章吗?";
	document.getElementById('show_msg').innerHTML=text;
	$('.popup_de').addClass('bbox');
	$('.popup_de .btn-danger').one('click',function(){
		doDelete(sid);
	})
}
function doDelete(data){
	var url;
	if(type=="news"){
		url = "../../publicityManage/deleteNews/";
	}else if(type=="public"){
		url = "../../publicityManage/deleteNotices/public/";
	}else if(type=="party"){
		url = "../../publicityManage/deleteNotices/party/";
	}
	$.ajax({                            
		type:'post',        
        url:url+data, 
        dataType:'json',
        success: function(result) {  
			alert(result.msg);
			location.reload();	
        },
        error :function(){
        	alert("系统出错，删除失败！");
        }
   });
}
function doPass(data){
	var url;
	if(type=="news"){
		url = "../../publicityManage/checkNews/";
	}else if(type=="public"){
		url = "../../publicityManage/checkNotices/public/";
	}else if(type=="party"){
		url = "../../publicityManage/checkNotices/party/";
	}
	$.ajax({                            
		type:'post',        
        url:url+data,   
        dataType:'json',
        success: function(result) {  
			alert(result.msg);
			location.reload();	
        },
        error :function(){
        	alert("系统出错，审核通过失败！");
        }
   });
}