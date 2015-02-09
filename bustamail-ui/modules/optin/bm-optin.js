BMApp.optin = angular.module("OptinModule", []);
BMApp.optin.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when('/optin', {
			controller : 'OptinMailIndexController',
			templateUrl: './modules/optin/views/index.html'
		})
		.when('/optin/create', {
			controller : 'OptinMailCreateController',
			templateUrl: './modules/optin/views/create.html'
		})
		.when('/optin/:id/edit', {
			controller : 'OptinMailEditController',
			templateUrl: './modules/optin/views/edit.html'
		})
		.otherwise({ redirectTo : '/' });
}]);

BMApp.optin.service("OptinService", ['$http', function($http) {
	
	return {
		
		getAllOptinMails : function(owner) {
			return $http.get('/api/optin?owner=' + owner).success(function(page) {
				return page;
			});
		},
		
		getOptinMailById : function(id) {
			return $http.get('/api/optin/' + id).success(function(optin) {
				return optin;
			});
		},
		
		createOptinMail : function(optin) {
			return $http.post('/api/optin', optin).succcess(function(optin) {
				return optin;
			});
		},
		
		updateOptinMail : function(optin) {
			return $http.patch('/api/optin/' + optin.id, optin).success(function(optin) {
				return optin;
			});
		},
		
		deleteOptinMail : function(optin) {
			$http({
				method  : 'DELETE',
				url		: '/api/optin/' + optin.id
			});
		},
		
		getCurrentContent : function(optin) {
			return $http.get('/api/optin/contents/current').success(function(content) {
				return content;
			});
		},
		
		getContentVersions : function(optin) {
			return $http.get('/api/optin/' + optin.id + '/contents').success(function(page) {
				return page;
			});
		},
		
		newInstance : function() {
			return {
				name : '',
				senderAddress : '',
				replyAddress  : '',
				subject		  : '',
				templatePack  : { id : '' },
				templateId    : ''
			};
		}
	};
}]);


BMApp.optin.controller('OptinMailIndexController', ['$scope', 'OptinService', function($scope, service) {
	
}]);

BMApp.optin.controller('OptinMailCreateController', ['$scope', 'OptinService', function($scope, service) {
	
}]);

BMApp.optin.controller('OptinMailEditController', ['$scope', 'OptinService', function($scope, service) {
	
}]);