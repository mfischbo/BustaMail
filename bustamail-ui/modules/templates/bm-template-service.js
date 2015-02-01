BMApp.TemplateService = angular.module("TemplateServiceModule", []);
BMApp.TemplateService.service("TemplateService", ['$http', '$q', function($http, $q) {
	return {
		getTemplatePacksByOwner : function(owner) {
			return $http.get('/api/templatepacks?owner=' + owner).success(function(data) {
				return data;
			});
		},
		
		getTemplatePackById : function(id) {
			return $http.get('/api/templatepacks/' + id).success(function(data) {
				return data;
			});
		},
		
		createTemplatePack : function(pack) {
			return $http.post('/api/templatepacks/', pack).success(function(data) {
				return data;
			});
		},
		
		copyTemplatePack   : function(id) {
			return $http.put('/api/templatepacks/' + id).success(function(data) {
				return data;
			});
		},
		
		updateTemplatePack : function(pack) {
			return $http.patch('/api/templatepacks/' + pack.id, pack).success(function(data) {
				return data;
			})
		},
		
		deleteTemplatePack : function(id) {
			return $http({
				method : 'DELETE',
				url    : '/api/templatepacks/' + id
			});
		}
	}
}]);