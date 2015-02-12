var BMApp = angular.module("BMApp", 
		['ngRoute', 'SecurityModule', 'SubscriberModule', 
		 'BMEditorModule', 'TemplatesModule', 'TemplateServiceModule', 'MailingModule', 'MailingListModule', 'MediaModule', 'LandingPageModule',
		 'StatsModule', 'OptinModule', 'DashBoardModule', 'ui.bootstrap']);

BMApp.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
    
	$httpProvider.interceptors.push(function($q) {
		return {
			'request' : function(config) {
				if (config.method == "PATCH")
					config.headers['Content-Type'] = "application/json";
				
				return config;
			},
			
			'requestError' : function(rejection) {
				return $q.reject(rejection);
			},
			
			'response'		: function(response) {
				return response;
			},
			
			'responseError'	: function(rejection) {
				
				if (rejection.status == 401 || rejection.status == 503)
					window.location.href = "./login";
				
				if (rejection.status == 405 || rejection.status == 415)
					BMApp.alert("Leider ist ein Fehler aufgetreten", 'error');
				
				if (rejection.status == 403) {
					var m = rejection.config.method;
					if (m == "POST" || m == "PATCH" || m == "PUT" || m == "DELETE")
						BMApp.alert("Sie haben nicht die notwendigen Rechte um diese Aktion durchzuf&uuml;hren", 'error');
				}
				
				if (rejection.status == 500) {
					BMApp.alert("Ooops! Da ist leider etwas schief gelaufen", 'error');
				}
				
				// disable spinner
				BMApp.hideSpinner();
				return $q.reject(rejection);
			}
		}
	});
	
	$routeProvider
		.otherwise({
			redirectTo 	: "/dashboard/"
		});
}]);

BMApp.run(function($http) {
	$http.get("/api/configuration").success(function(data) {
		BMApp.uiConfig = data;
	});
});


BMApp.controller("BMAppLogoutController", ['$scope', '$http', function($scope, $http) {
	
	$http({
		url		:	"/api/security/authentication",
		method	:	"DELETE"
	}).success(function() {
		window.location.href = "./login";
	});
}]);


BMApp.uiConfig = {
		
		// timeout in milliseconds before a search triggers
		searchDelay : 600,
		
		// configuration options for code mirror instances
		codeMirrorOpts : {
			lineNumbers : true,
			mode		: "htmlmixed"
		}
};