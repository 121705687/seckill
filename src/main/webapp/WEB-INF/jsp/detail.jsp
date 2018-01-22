<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <title>秒杀详情页</title>
    <%--正常情况下，不会用页面做登录，一般要后台的，这里暂不用--%>
    <%@include file="common/head.jsp"%>
</head>
<body>
    <div class="container">
        <div class="panel panel-default text-center">
            <div class="pannel-heading">${seckill.name}</div>
            <div class="panel-body">
                <h2 class="text-danger">
                    <%--显示taime图标--%>
                    <span class="glyphicon glyphicon-time"></span>
                    <%--展示倒计时--%>
                    <span class="glyphicon" id="seckill-box"></span>
                </h2>
            </div>
        </div>
    </div>
    <%--弹出层，输入电话--%>
    <div id="killPhoneModal" class="modal fade">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h3 class="modal-title text-center">
                        <span class="glyphicon glyphicon-phone"></span>秒杀电话：
                    </h3>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-xs-8 col-xs-offset-2">
                            <input type="text" name="killPhone" id="killPhoneKey"
                                placeholder="填手机号" class="form-control">
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <span id="killPhoneMessage" class="glyphicon"></span>
                    <button type="button" id="killPhoneBtn" class="btn btn-success">
                        <span class="glyphicon glyphicon-phone"></span>
                        Submit
                    </button>
                </div>
            </div>
        </div>
    </div>
</body>
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script src="https://cdn.bootcss.com/jquery/2.1.1/jquery.min.js"></script>
<!-- 最新的 Bootstrap 核心 JavaScript 文件 -->
<script src="https://cdn.bootcss.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

<script src="https://cdn.bootcss.com/jquery-cookie/1.4.1/jquery.cookie.min.js"></script>
<script src="https://cdn.bootcss.com/jquery.countdown/2.1.0/jquery.countdown.min.js"></script>
<%--<script src="./seckill.js" type="text/javascript"></script> 路径有问题--%>
<script type="text/javascript">
    //读取cookie
    function getCookie(name)
    {
        var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
        if(arr=document.cookie.match(reg))
            return unescape(arr[2]);
        else
            return null;
    }


    <%----%>
    //存放主要交互逻辑js代码
    //容易写乱 所以要模块化分包
    var seckill={
        //封装秒杀相关ajax的url
        URL:{
            now:function () {
                return '/seckill/time/now';
            },
            expose:function (seckillId) {
                return '/seckill/'+seckillId+'/exposer';
            },
            execution:function (seckillId,md5) {
                return '/seckill/'+seckillId+'/'+md5+'/execution';
            }
        },
        seckilhandleSeckillkill:function (seckillId,node) {
            //处理秒杀逻辑
            node.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
            $.post(seckill.URL.expose(seckillId),{},function (result) {
                //回调函数
                if(result && result['success']){
                    var exposer = result['data'];
                    if(exposer['exposed']){
                        //开启秒杀
                        var md5 = exposer['md5'];
                        var killUrl = seckill.URL.execution(seckillId,md5);
                        console.log("killUrl:"+killUrl);
                        //只绑定一次点击事件///////////////////////////
                        $('#killBtn').one('click',function () {
                            $(this).addClass('disabled');
                            $.post(killUrl,{},function (result) {
                                if(result && result['success']){
                                    var killResult = result['data'];
                                    var state = killResult['state'];
                                    var stateInfo = killResult['stateInfo'];
                                    node.html('<span class="label label-success">'+stateInfo+'</span>');
                                }
                            });
                        });
                        node.show();
                    }else{
                        //未开启
                        var now = exposer['now'];
                        var start = exposer['start'];
                        var end = exposer['end'];
                        seckill.countdown(seckillId,now,start,end);
                    }

                }else{
                    console.log('result:'+result);
                }
            });
        },
        //验证手机号
        validatePhone:function (phone) {
            if(phone&& phone.length==11 && !isNaN(phone)){
                return true;
            }else{
                return false;
            }
        },
        countdown:function (seckillId,nowTime,startTime,endTime) {
            var seckillBox = $('#seckill-box');
            if(nowTime>endTime){
                //秒杀结束
                seckillBox.html('秒杀结束');
            }else if(nowTime<startTime){
                //秒杀未开始，计时
                var killTime = new Date(startTime+1000);
                seckillBox.countdown(killTime,function (event) {
                    var format = event.strftime('秒杀倒计时：%D天 %H时 %M分 %S秒');
                    seckillBox.html(format);
                }).on('finish.countdown',function () {
                    //获取秒杀地址，控制逻辑，执行秒杀
                    seckill.seckilhandleSeckillkill(seckillId,seckillBox);
                });
            }else{
                //秒杀开始
                seckill.seckilhandleSeckillkill(seckillId,seckillBox);
            }
        },
        //详情页秒杀逻辑
        detail:{
            //详情页初始化
            init:function (params) {
                //用户手机验证和登录，计时交互
                //规划我们的交互流程
                //在cookie中查找手机号
//                var killPhone = $.cookie('killPhone');
                var killPhone = getCookie('killPhone');
                //验证手机号
                if(!seckill.validatePhone(killPhone)){
                    //绑定手机号 控制输出
                    var killPhoneModal = $("#killPhoneModal");
                    //显示弹出层
                    killPhoneModal.modal({
                        show: true,//显示弹出层
                        backdrop: 'static',//禁止位置关闭
                        keyboard: false  //关闭键盘事件
                    });
                    $("#killPhoneBtn").click(function () {
                        var inputPhone = $("#killPhoneKey").val();
                        if (seckill.validatePhone(inputPhone)) {
                            //电话写入cookie
                            $.cookie('killPhone', inputPhone, {expires: 7, path: '/seckill'});
                            //刷新页面
                            window.location.reload();
                        } else {
                            $("#killPhoneMessage").hide().html('<label class="label label-danger">手机号错误!</label></label>').show(300);
                        }
                    });
                }
                //已经登录
                //几时交互
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var seckillId = params['seckillId'];
                $.get(seckill.URL.now(),{},function (result) {
                    console.log(result);
                    if(result && result['success']){
                        var nowTime = result['data'];
                        //事件判断
                        seckill.countdown(seckillId,nowTime,startTime,endTime);
                     }else{
                        console.log('result:'+result);
                    }
                });
            }
        }
    }
    <%----%>
    $(function () {
        //使用el表达式传入参数
        seckill.detail.init({
            seckillId:${seckill.seckillId},
            startTime:${seckill.startStr},//毫秒
            endTime:${seckill.endStr}
//            startTime:"2018-01-23",
//            endTime:"2018-01-24"
        });
    });
</script>
</html>
