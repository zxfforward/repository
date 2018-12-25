app.controller('payController', function ($scope,$location, payService) {
    //生成支付
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.total_fee = (response.total_fee / 100).toFixed(2); //金额
                $scope.out_trade_no = response.out_trade_no;//订单号
                //二维码生成
                var qr = new QRious({
                    element: document.getElementById('qrious'),
                    size: 250,
                    level: 'H',
                    value: response.code_url
                });
                queryPayStatus(response.out_trade_no);//查询支付状态
            }
        );
    }
    //检查支付
   queryPayStatus = function (out_trade_no) {
        payService.queryStatus(out_trade_no).success(
            function (response) {
                if(response.success){
                    location.href="paysuccess.html#?total_fee="+ $scope.total_fee;
                }else {
                    if(response.message=='二维码请求超时'){
                        $scope.createNative();//重新生成二维码
                    }
                    location.href="payfail.html";
                }
            }
        );
    }
    //获取金额
    $scope.getTotalFee = function () {
       return $location.search()['total_fee'];
    }

});