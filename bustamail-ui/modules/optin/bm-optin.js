BMApp.optin = angular.module("OptinModule", ['SecurityModule', 'TemplateServiceModule']);
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
			templateUrl: './modules/editor/tmpl/environment.html'
		})
		.when('/optin/:id/envelope', {
			controller : 'OptinMailEnvelopeController',
			templateUrl: './modules/optin/views/envelope.html'
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
			return $http.post('/api/optin', optin).success(function(optin) {
				return optin;
			});
		},
		
		updateOptinMail : function(optin) {
			return $http.patch('/api/optin/' + optin.id, optin).success(function(optin) {
				return optin;
			});
		},
		
		deleteOptinMail : function(optin) {
			return $http({
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
	
	$scope.owner = '';
	$scope.optinmails = [];
	
	$scope.$watch('owner', function(nval) {
		if (!nval) return;
		
		service.getAllOptinMails($scope.owner).success(function(page) {
			$scope.optinmails = page.content;
		});
	});
	
	$scope.deleteOptinMail = function(optin) {
		BMApp.confirm('Soll die Optin Mail wirklich entfernt werden?', function() {
			service.deleteOptinMail(optin).success(function() {
				BMApp.utils.remove('id', optin.id, $scope.optinmails);
			});
		});
	};
	
	$scope.activate = function(optin) {
		optin.activated = true;
		service.updateOptinMail(optin).success(function() {
			BMApp.alert('Die Optin Mail wurde erfolgreich aktiviert');
			for (var q in $scope.optinmails) {
				if ($scope.optinmails[q].id != optin.id && $scope.optinmails[q].activated)
					$scope.optinmails[q].activated = false;
			}
		});
	};
}]);

BMApp.optin.controller('OptinMailCreateController', ['$scope', '$location', 'OptinService', 'TemplateService', 
                                                     function($scope, $location, service, tService) {
	
	$scope.owner = '';
	$scope.templates = [];
	$scope.packs 	 = [];
	
	$scope.mail = service.newInstance();
	
	$scope.$watch('owner', function(nval) {
		if (!nval) return;
		tService.getTemplatePacksByOwner($scope.owner).success(function(page) {
			$scope.templates = tService.prepareForSelection(page);
			$scope.packs     = page.content;
		});
	});
	
	$scope.submitAction = function() {
		var pack = tService.getTemplatePackByTemplateId($scope.mail.templateId, $scope.packs);
		$scope.mail.templatePack = { id : pack.id };
		$scope.mail.owner = $scope.owner;
		service.createOptinMail($scope.mail).success(function() {
			BMApp.alert("Die Optin Mail wurde erfolgreich angelegt");
			$location.path('/optin');
		});
	};
}]);

BMApp.optin.controller('OptinMailEnvelopeController', ['$scope', '$routeParams', '$location', 'OptinService', 'TemplateService',
                                                       function($scope, $routeParams, $location, service, tService) {
	$scope.owner = '';
	$scope.packs = [];

	service.getOptinMailById($routeParams.id).success(function(mail) {
		$scope.mail = mail;
	});
	
	$scope.submitAction = function() {
		service.updateOptinMail($scope.mail).success(function() {
			BMApp.alert('Die Optin Mail wurde erfolgreich bearbeitet');
			$location.path('/optin');
		});
	};
}]);

BMApp.optin.controller('OptinMailEditController', ['$scope', '$routeParams', 'EditorFactory', 'OptinService', 'TemplateService',
                                                   function($scope, $routeParams, EditorFactory, service, tService) {
	
	EditorFactory.prepareScope($scope, $routeParams, 'optinmail');
}]);