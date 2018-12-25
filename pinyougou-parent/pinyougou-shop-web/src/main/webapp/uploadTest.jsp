<%--
  Created by IntelliJ IDEA.
  User: crowndint
  Date: 2018/10/16
  Time: 20:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="plugins/jQuery/jquery-2.2.3.min.js"></script>
    <script src="plugins/angularjs/angular.min.js"></script>

    <script>

        var app = angular.module('app', []);
        app.controller('ctr', function ($scope, $http) {
            $scope.upload = function () {

                var formData = new FormData();
                formData.append("file", file.files[0]);
                $http({
                    method: 'POST',
                    url: 'goods/upload.do',
                    data: formData,
                    headers: {'Content-Type': undefined},
                    //transformRequest来转换请求数据的格式
                    transformRequest: function (data) {
                        return data;
                    }

                }).success(function (response) {
                    console.log(response);
                    //alert("上传成功"+response);
                    $scope.image_url = response.message;

                }).error(function () {
                    alert("上传失败");
                });

                /*
                var formData = new FormData();
                formData.append('file', $('#file')[0].files[0]);
                $.ajax({
                    url: 'goods/upload.do',
                    type: 'POST',
                    cache: false,
                    data: formData,
                    processData: false,
                    contentType: false
                }).done(function (res) {
                    alert("上传成功" + res.message);
                    console.log(res);
                    $scope.image_url = res.message;
                }).fail(function (res) {
                    alert("上传失败");
                });
                */
            }
        });

    </script>

</head>
<body ng-app="app" ng-controller="ctr">
<div>
    <div class="modal-body">
        <table class="table table-bordered table-striped">
            <td><input type="file" name="file" id="file"/>
                <button ng-click="upload()">上传</button>
            </td>
            <td>
                <img src="{{image_url}}" width="60px" height="60px">
            </td>
            </tr>
        </table>
    </div>
</div>


</body>
</html>
