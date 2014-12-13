BMApp.TemplateService = angular.module("TemplateServiceModule", []);
BMApp.TemplateService.service("TemplateService", ['$http', '$q', function($http, $q) {
	return {
		getTemplatePacksByOwner : function(owner) {
			return $http.get('/api/templates/' + owner + '/packs').success(function(data) {
				return data;
			});
		},
		
		getTemplatePackById : function(id) {
			return $http.get('/api/templates/packs/' + id).success(function(data) {
				return data;
			});
		},
		
		createTemplatePack : function(pack) {
			return $http.post('/api/templates/packs/', pack).success(function(data) {
				return data;
			});
		},
		
		updateTemplatePack : function(pack) {
			return $http.patch('/api/templates/packs/' + pack.id, pack).success(function(data) {
				return data;
			})
		},
		
		deleteTemplatePack : function(id) {
			return $http({
				method : 'DELETE',
				url    : '/api/templates/packs/' + id
			});
		}
	}
}]);