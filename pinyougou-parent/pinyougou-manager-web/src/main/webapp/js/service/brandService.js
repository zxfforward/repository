app.service('brandService', function ($http) {

    this.findAll=function () {
        return $http.get('../brand/findAll.do');
    }

    this.findPage=function (pageNum, pageSize) {

        return $http.post('../brand/findByPage.do?pageNum='+pageNum+'&pageSize='+pageSize);
    }

    this.searchByPage=function (pageNum, pageSize, searchEn) {

        return $http.post('../brand/searchByPage.do?pageNum='+pageNum+'&pageSize='+pageSize, searchEn);
    }

    this.update=function (poj) {

        return $http.post('../brand/update.do', poj);
    }

    this.findOne=function (id) {

        return $http.get('../brand/findOne.do?id='+id);
    }

    this.delete=function (ids) {

        return $http.post('../brand/delete.do?ids='+ids);
    }

    this.selectOptionsList=function () {

        return $http.get('../brand/selectOptionsList.do');
    }


});