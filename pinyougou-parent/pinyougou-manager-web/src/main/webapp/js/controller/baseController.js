app.controller('baseController', function ($scope) {

    //分页控件配置
    // <tm-pagination conf="paginationConf"></tm-pagination> 视图改变的时候paginationConf数据模型改变
    // 同时出发onChange方法的调用，这个时机可以向服务器请求分页数据
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    $scope.reloadList=function () {
        //$scope.findPage($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //checkbox选中之后当前id存放到这个集合
    $scope.selectIds = [];
    $scope.updateSelections=function ($event, id) {
        //如果当前点击的checkbox选中状态,把id加入到集合
        if($event.target.checked) {
            $scope.selectIds.push(id);
        } else {
            //如果当前点击的checkbox取消状态，从集合移除，需要找到当前id在结合的位置
            var idIndex = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idIndex, 1);
        }
    }

    $scope.jsonToStr=function (jsonStr, key) {

        jsonStr = JSON.parse(jsonStr);
        var str = "";
        for (var i=0;i<jsonStr.length;i++) {
            if(i>0) {
                str+=", ";
            }
            str+=jsonStr[i][key];
        }
        return str;
    }


});