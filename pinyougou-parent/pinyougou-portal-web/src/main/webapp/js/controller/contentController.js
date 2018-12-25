app.controller('contentController',function($scope,contentService){
	
	$scope.contentList=[];//广告列表
	
	$scope.findByCategoryId=function(categoryId){
		contentService.findByCategoryId(categoryId).success(
			function(response){
				$scope.contentList[categoryId]=response;
				console.log(JSON.stringify($scope.contentList));
			}
		);		
	}
	//首页对接
	//传递搜索关键字
	$scope.search = function () {
		location.href ="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
	
});