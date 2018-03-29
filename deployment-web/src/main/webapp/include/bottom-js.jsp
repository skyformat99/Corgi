<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- REQUIRED JS SCRIPTS -->
<!-- Bootstrap 3.3.2 JS -->
<script src="/static/js/bootstrap/bootstrap.min.js" type="text/javascript"></script>
<script src="/static/js/json2.js" type="text/javascript"></script>
<!-- AdminLTE App -->
<script src="/static/tpl/js/app.min.js" type="text/javascript"></script>
<!-- DATA TABES SCRIPT -->
<script src="/static/js/template.js"  type="text/javascript"></script>
<script src="/static/plugins/select2/select2.min.js" type="text/javascript"></script>
<script src="/static/plugins/jquery.form.js" type="text/javascript"></script>
<script src="/static/plugins/bootstrap-dialog/js/bootstrap-dialog.min.js"></script>
<script src="/static/js/paginator.js?v=7" type="text/javascript"></script>
<script src="/static/plugins/daterangepicker/moment.js" type="text/javascript"></script>
<script src="/static/plugins/daterangepicker/daterangepicker.js" type="text/javascript"></script>
<script src="/static/plugins/jquery.cookie.js" type="text/javascript"></script>

<script src="/static/plugins/datatables/jquery.dataTables.min.js" type="text/javascript"></script>
<script src="/static/plugins/datatables/dataTables.bootstrap.js" type="text/javascript"></script>
<script src="/static/js/datatable-custom.js" type="text/javascript"></script>

<script src="/static/js/pnotify.custom.min.js" type="text/javascript"></script>
<script type="text/javascript">
    PNotify.prototype.options.styling = "bootstrap3";

    // 日期格式化
    Date.prototype.format = function (fmt) { //author: meizz
        var o = {
            "M+": this.getMonth() + 1, //月份
            "d+": this.getDate(), //日
            "h+": this.getHours() % 12 == 0 ? 12 : this.getHours() % 12, //小时
            "H+": this.getHours(), //小时
            "m+": this.getMinutes(), //分
            "s+": this.getSeconds(), //秒
            "q+": Math.floor((this.getMonth() + 3) / 3), //季度
            "S": this.getMilliseconds() //毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };

    // 获取昨天的日期
    Date.prototype.getYesterday = function () {
        var time = this.getTime() - 24 * 3600 * 1000;
        return new Date(time);
    };

    //获取明天的日期
    Date.prototype.getNextDay = function () {
        var time = this.getTime() + 24 * 3600 * 1000;
        return new Date(time);
    };

    // 获取上周同一天的日期
    Date.prototype.getLastWeekSameDay = function () {
        var time = this.getTime() - 24 * 3600 * 1000 * 7;
        return new Date(time);
    };

    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    };

    $.datePickerSperator = ' - ';

    /**
     * 设置一个输入框为时间范围选择框
     * @param selector
     * @param setDate 是否自动设置日期
     */
    $.dateTimePicker = function (selector, setDate) {
        var separator = $.datePickerSperator;
        var options = {
            timePicker: true, timePickerIncrement: 1, format: 'YYYY-MM-DD HH:mm', separator: separator,
            timePicker12Hour: false,
            locale: {cancelLabel: '取消', applyLabel: '确定', fromLabel: '开始日期', toLabel: '结束日期'}
        };
        if (setDate) {
            var startDate = (new Date()).getLastWeekSameDay().format('yyyy-MM-dd') + ' 00:00';
            var endDate = (new Date()).format('yyyy-MM-dd') + ' 23:59';
            options['startDate'] = startDate;
            options['endDate'] = endDate;
            $(selector).val(startDate + separator + endDate);
        }
        $(selector).daterangepicker(options);
    };

    /**
     * 设置一个输入框为日期范围选择框
     * @param selector
     * @param setDate 是否自动设置日期
     */
    $.datePicker = function(selector, setDate) {
        var separator = $.datePickerSperator;
        var options = {
            timePicker: true, timePickerIncrement: 1, format: 'YYYY-MM-DD', separator: separator,
            timePicker12Hour: false,
            locale: { cancelLabel: '取消', applyLabel: '确定', fromLabel:'开始日期', toLabel:'结束日期' }
        };
        if (setDate) {
            var startDate = (new Date()).getLastWeekSameDay().format('yyyy-MM-dd');
            var endDate = (new Date()).format('yyyy-MM-dd');
            options['startDate'] = startDate;
            options['endDate'] = endDate;
            $(selector).val(startDate + separator + endDate);
        }
        $(selector).daterangepicker(options);
    };


    // 把string解析为Date
    function parseDate(str) {
        if (typeof str == 'string') {
            var results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) *$/);
            if (results && results.length > 3)
                return new Date(parseInt(results[1]), parseInt(results[2].replace('0', '')) - 1, parseInt(results[3]));
            results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
            if (results && results.length > 6)
                return new Date(parseInt(results[1]), parseInt(results[2].replace('0', '')) - 1, parseInt(results[3]), parseInt(results[4]), parseInt(results[5]), parseInt(results[6]));
            results = str.match(/^ *(\d{4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,9}) *$/);
            if (results && results.length > 7)
                return new Date(parseInt(results[1]), parseInt(results[2].replace('0', '')) - 1, parseInt(results[3]), parseInt(results[4]), parseInt(results[5]), parseInt(results[6]), parseInt(results[7]));
        }
        return null;
    }
    ;

    $(function () {
        // ie8下会缓存ajax数据，此处去掉缓存
        $.ajaxSetup({cache: false});
    });

    // template 增加日期、时间格式化 start
    template.helper('formatDateTime', function (content) {
        var d = new Date(content).format('yyyy-MM-dd HH:mm:ss');
        return d;
    });

    template.helper('formatDate', function (content) {
        var d = new Date(content).format('yyyy-MM-dd');
        return d;
    });

    function cent2yuan(content) {
        var i = parseFloat(content);
        if (isNaN(i)) {
            i = 0.00;
        }
        i = i / 100.0;
        var minus = '';
        if (i < 0) {
            minus = '-';
        }
        i = Math.abs(i);
        i = parseInt((i + .005) * 100);
        i = i / 100;
        s = new String(i);
        if (s.indexOf('.') < 0) {
            s += '.00';
        }
        if (s.indexOf('.') == (s.length - 2)) {
            s += '0';
        }
        s = minus + s;
        return s;
    }
    // 金额格式化：分 -> 元
    template.helper('cent2yuan', cent2yuan);

    // 格式化百分数
    template.helper('percentFormat', function (content, n) {
        n = n || 2;
        var d = (content == 0 ? '0%' : (Math.round(content * 10000) / 100).toFixed(n) + '%');
        return d;
    });
    // end

    $.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
        return null;
    };


    $(function () {
        // 高亮菜单
        var path = window.location.href;
        if (path.indexOf("?") > 0) {
            path = path.substring(0, path.indexOf("?"));
        }
        $(".sidebar-menu a").each(function (key, value) {
            if (path == value) {
                var el = $(this);
                while (true) {
                    var parent = el.parent();
                    if (parent.hasClass('sidebar-menu')) {
                        break;
                    }
                    if (parent.is("li")) {
                        parent.addClass("active");
                    }

                    el = parent;
                }

            }
        });
    });

</script>
