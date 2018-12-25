app.controller('brandController', function ($scope,$controller,brandService) {

    //继承
    $controller('baseController', {$scope: $scope});

    $scope.findAll=function () {

        brandService.findAll().success(function (response) {
            $scope.list=response;
        });
    }

    $scope.findPage=function (pageNum, pageSize) {

        brandService.findPage(pageNum, pageSize).success(function (response) {
            //响应数据回来，数据模型改变了，视图也会改变
            $scope.paginationConf.totalItems=response.total;
            $scope.list= response.rows;
        });
    }

    //必须定义
    $scope.searchEntity = {}

    $scope.search=function (pageNum, pageSize) {

        brandService.searchByPage(pageNum, pageSize, $scope.searchEntity).success(function (response) {

            $scope.paginationConf.totalItems=response.total;
            $scope.list=response.rows;
        });
    }

    $scope.entity = {};
    $scope.update=function () {

        brandService.update($scope.entity).success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }

    $scope.findOne=function (id) {

        brandService.findOne(id).success(function (response) {
            $scope.entity=response;
        });
    }

    $scope.delete=function () {

        brandService.delete($scope.selectIds).success(function (response) {

            if (response.success) {
                $scope.reloadList();
            }else {
                alert(response.message);
            }
        });
    }



});