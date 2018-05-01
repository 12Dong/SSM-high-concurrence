// 存放主要交互逻辑js代码
// js模块化
var seckill = {
    // 封装秒杀相关ajax地址
    url:{
        now : function () {
            return "/seckill/time/now";
        },

        exposer: function (seckillId) {
            return "/seckill/" + seckillId + "/exposer";
        },
        execution:function(seckillId,md5){
            return '/seckill/'+seckillId+'/'+md5+'/execution';
        }
    },
    // 详情页秒杀地址逻辑
    //验证手机号
    validatePhone:function(phone){
        if(phone && phone.length==11 && !isNaN(phone)){
            return true;
        }else return false;
    },

    handlerSecKill :function (seckillId,node) {
        //处理秒杀逻辑
        node.hide().html('<button class="btn btn-primary bt-lg" id="killBtn">开始秒杀</button>');//按钮
        // alert('error');

        $.get(seckill.url.exposer(seckillId), {}, function (result) {
            //再回调函数中 执行交互流程
            // alert('success');
            if(result && result['success']){
                var exposer =  result['data'];
                if(exposer['exposed']){
                    // 开始秒杀
                    //获取秒杀地址
                    var md5 =  exposer['md5'];
                    var killUrl = seckill.url.execution(seckillId,md5);
                    console.log('killUrl = '+killUrl);
                    //光用click的话 会把所有的点击请求发送至服务器 造成服务器压力
                    $("#killBtn").one('click',function () {
                        //执行秒杀 请求操作
                        $(this).addClass('disable');
                        // 发送秒杀请求 执行秒杀
                        $.get(killUrl,{},function (result) {
                            alert("执行成功");

                            if(result && result['success']){
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                alert(stateInfo);
                                // 显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>')
                            }else{
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                alert(stateInfo);
                                // 显示秒杀结果
                                node.html('<span class="label label-success">'+stateInfo+'</span>')                            }
                        });
                    });
                    node.show();
                }else{
                    //如果 用户开始计时面板很长 时间跳转有差异 pc机计时过快
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    //重新计算 计时逻辑
                    seckill.countdown(seckillId,now,start,end);
                }
            }else{
                console.log(result);
            }
        });

    },


    countdown:function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $('#seckillBox');
        if(nowTime>endTime){
            // 秒杀结束
            seckillBox.html('秒杀结束');
        }else if(nowTime<startTime){
            //秒杀未开始
            //计时时间绑定
            var killTime = new Date(startTime+1000);
            // 防止出现时间偏移
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后回调时间
            }).on('finish.countdown',function () {
                //获取秒杀地址 控制显示逻辑
                //执行秒杀
                seckill.handlerSecKill(seckillId,seckillBox);

            });
        }else{
            alert("执行秒杀开始逻辑");
            seckill.handlerSecKill(seckillId,seckillBox);
        }
    },

    detail:{
        init:function (params){
            // 用户手机验证和登录,计时交互
            // 把 因为没有后端 用户信息放到cookie
            var killPhone = $.cookie('killPhone');

            // alert("开始验证")
            // 验证手机号
            if(!seckill.validatePhone(killPhone)){
                //绑定phone
                //控制输出

                // 验证手机控制输出
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({
                    show: true,  // 显示弹出层
                    backdrop: 'static',  // 静止位置关闭
                    keyboard: false    // 关闭键盘事件
                });

                $('#killPhoneBtn').click(function(){
                    var inputPhone = $('#killphoneKey').val();
                    console.log("inputPhone="+inputPhone); //todo
                    if(seckill.validatePhone(inputPhone)){
                        //刷新页面
                        // 需要将电话写入cookie
                        window.location.reload();
                        //将用不到的cookie 传到后端 对服务器压力有影响 传输数据多了
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                    }else{
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机错误</label>').show(300);
                    }
                });
            }
            //计时交互
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            $.get(seckill.url.now(),{},function (result) {
                if(result && result['success']){
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                }else{
                    console.log('result'+result);
                }
            });


    }
    }
}

