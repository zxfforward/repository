 //控制层 
app.controller('itemCatController' ,function($scope,$controller,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承

	$scope.parentId=0;

	$scope.entity1={};
	$scope.entity2={};
	$scope.findByParent=function(grade, entity) {

		//记录当前parentId
		$scope.parentId=entity.id;

		$scope.grade=grade;

		//当点击面包屑0级的时候1级2级应该不显示
		if($scope.grade==0) {
			$scope.entity1=null;
			$scope.entity2=null;
		}

		if($scope.grade==1) {
			$scope.entity1=entity;
			$scope.entity2=null;
		}

		if($scope.grade==2) {
			$scope.entity2=entity;
		}

        itemCatService.findByParent(entity.id).success(function (response) {

            $scope.list=response;
        });
    }
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    

	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}

	//将[{id:1, name:'aaa'},{id:2, name: 'bbb'}}] 转换为[{id:1, text:'aaa'},{id:2, text: 'bbb'}}]
	$scope.replaceKeyToText=function (jsonStr, key) {

		var data = [];
		for(var i=0;i<jsonStr.length;i++) {
			//数组的每一个元素添加一个text属性并给属性赋值
            jsonStr[i]['text']=jsonStr[i][key];
            //删除原来的属性
            delete jsonStr[i][key];
		}
		return jsonStr;
    }

    $scope.replaceKeyToText1=function (jsonStr, key) {

		var item = {id:'',text:''};
        var data = [];
        for(var i=0;i<jsonStr.length;i++) {
            //数组的每一个元素添加一个text属性并给属性赋值
			var newItem = JSON.parse(JSON.stringify(item));
            newItem['text']=jsonStr[i][key];
            newItem['id']=jsonStr[i]['id'];
            data.push(newItem);
        }
        return data;
    }


	$scope.findAllTypeTemplate=function () {

        typeTemplateService.findAll().success(function (response) {

        	$scope.typeTemplateConfig={data:$scope.replaceKeyToText1(response, 'name')};
        	console.log($scope.typeTemplateConfig);
        });
    }

    // //定义一个typeId 给select2 用于ng-model绑定typeId  <input select2 ng-model="typeId"  config="typeTemplateConfig">
    // $scope.typeId=0;
	//保存 
	$scope.save=function(){
		//把保存的分类对象挂载到parentId下面
		$scope.entity.parentId=$scope.parentId;

		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	//$scope.reloadList();//重新加载
					//$scope.findByParent($scope.grade, {id:$scope.parentId});
                    itemCatService.findByParent($scope.parentId).success(function (response) {

                        $scope.list=response;
                    });

				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					//$scope.reloadList();//刷新列表
                    $scope.findByParent($scope.grade, {id:$scope.parentId});
					$scope.selectIds=[];
				}						
			}		
		);				
	}

    
});	
