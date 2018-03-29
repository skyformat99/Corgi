/**datatable 中文**/
(function(){
    var oLanguage={
        "oAria": {
            "sSortAscending": ": 升序排列",
            "sSortDescending": ": 降序排列"
        },
        "oPaginate": {
            "sFirst": "首页",
            "sLast": "末页",
            "sNext": "下页",
            "sPrevious": "上页"
        },
        "sEmptyTable": "没有相关记录",
        "sInfo": "第 _START_ 到 _END_ 条记录，共 _TOTAL_ 条",
        "sInfoEmpty": "第 0 到 0 条记录，共 0 条",
        "sInfoFiltered": "(从 _MAX_ 条记录中检索)",
        "sInfoPostFix": "",
        "sDecimal": "",
        "sThousands": ",",
        "sLengthMenu": "每页显示条数: _MENU_",
        "sLoadingRecords": "正在载入...",
        "sProcessing": "正在载入...",
        "sSearch": "搜索:",
        "sSearchPlaceholder": "",
        "sUrl": "",
        "sZeroRecords": "没有相关记录"
    }
    $.fn.dataTable.defaults.oLanguage=oLanguage;
    //$.extend($.fn.dataTable.defaults.oLanguage,oLanguage)
})();

// 分页返回转换成 datatable 通用参数
function pagerToDataTableParams(pager){
    var tdData = {};
    tdData.aaData = pager.object || [];

    tdData.pageCount = pager.totalPage;
    tdData.page = pager.currentPage;
    tdData.pageNO = pager.currentPage;
    tdData.iDisplayStart = ( pager.currentPage -1 ) * pager.pageSize;
    tdData.iDisplayLength = pager.pageSize;
    tdData.iTotalRecords = pager.count;
    tdData.iTotalDisplayRecords = pager.count;
    tdData.prev = pager.currentPage > 1 ? pager.currentPage -1 : 1;
    tdData.next = pager.currentPage < pager.totalPage ? pager.pageNo + 1 : pager.totalPage;
    tdData.top = 1;
    tdData.bottom = pager.totalPage;
    tdData.iSortCol_0 = 0;
    tdData.iSortTitle_0 = 0;
    tdData.sSortDir_0 = "desc";
    tdData.sSearch = ""
    tdData.pageIndex = 1;
    return tdData;
}

//datatable 请求参数转换成后台分页参数
function convToCommDataTableReqParams(aoData) {
    var currentPage;
    var pageSize;
    var iDisplayStart;
    var iDisplayLength;
    var orderField;
    var orderDirection;
    var iSortCol_0;
    var sSortDir_0;

    for(var x in aoData){
        var tmp = aoData[x];
        if(tmp.name == 'iDisplayStart') {
            iDisplayStart = tmp.value;
        }else if(tmp.name == 'iDisplayLength'){
            if(tmp.value < 20){
                aoData.value = 20;
            }
            iDisplayLength = tmp.value;

        }else if(tmp.name == 'iSortCol_0'){
            iSortCol_0 = tmp.value;
        }else if(tmp.name == 'sSortDir_0'){
            sSortDir_0 = tmp.value;
        }

    }

    for(var x in aoData){
        var tmp = aoData[x];
        if(tmp.name == 'mDataProp_' + iSortCol_0) {
            orderField = tmp.value;
            break;
        }
    }

    orderDirection = sSortDir_0;
    currentPage = iDisplayStart/iDisplayLength + 1;
    pageSize = iDisplayLength;
    aoData.push({name : "currentPage", value : currentPage});
    aoData.push({name : "pageSize", value : pageSize});
    aoData.push({name : "orderField", value : orderField});
    aoData.push({name : "orderDirection", value : orderDirection});
    return aoData;
}