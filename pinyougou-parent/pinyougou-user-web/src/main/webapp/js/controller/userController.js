 //控制层 
app.controller('userController' ,function($scope,userService){

   $scope.register = function () {
       //比较两次输入密码是否一致
       if($scope.entity.password!=$scope.checkPassword){
           alert("两次输入密码不一致，请重新输入")
           $scope.entity.password = "";
           $scope.checkPassword ="";
           return;
       }
	  userService.add($scope.entity,$scope.smsCode).success(
	  	function (response) {
            alert(response.message)
      }) ;
   }
    $scope .sendCode = function () {
       //判断手机是否填写
        if(($scope.entity.phone==null||$scope.entity.phone=="")){
            alert("请输入您的手机号！");
            return;
        }
        userService.sendCode($scope.entity.phone).success(
            function (response) {
                alert(response.message);
            }
        );
    }

});	
