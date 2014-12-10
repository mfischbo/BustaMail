var BMApp = angular.module("BMApp", 
		['ngRoute', 'SecurityModule', 'SubscriberModule', 
		 'BMEditorModule', 'TemplatesModule', 'MailingModule', 'MailingListModule', 'MediaModule', 'LandingPageModule',
		 'ui.bootstrap']);

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
		// security module
		.when("/security/orgUnits", {
			templateUrl :	"./js/modules/security/tmpl/orgUnits/index.html",
			controller	:	"SecurityOrgUnitIndexController"
		})
		.when("/security/orgUnits/create", {
			templateUrl :	"./js/modules/security/tmpl/orgUnits/create.html",
			controller	:	"SecurityOrgUnitCreateController"
		})
		.when("/security/users",   {
			templateUrl : "./js/modules/security/tmpl/users/index.html",
			controller	: "SecurityUserIndexController"
		})
		.when("/security/users/create", {
			templateUrl	:	"./js/modules/security/tmpl/users/create.html",
			controller	:	"SecurityUserCreateController"
		})
		.when("/security/users/:id/edit", {
			templateUrl :	"./js/modules/security/tmpl/users/edit.html",
			controller	:	"SecurityUserEditController"
		})
		.when("/security/settings", {
			templateUrl : "./js/modules/security/tmpl/settings/index.html",
			controller	:	"SecuritySetttingsIndexController"
		})
	
		// subscriber module
		.when("/subscriber", {
			templateUrl : "./js/modules/subscriber/tmpl/index.html",
			controller	: "SubscriberIndexController"
		})
		
		// templates module
		.when("/templates/packs", {
			templateUrl	:	"./js/modules/templates/tmpl/packs/index.html",
			controller	:	"TemplatePacksIndexController"
		})
		.when("/templates/packs/create", {
			templateUrl :	"./js/modules/templates/tmpl/packs/create.html",
			controller  :	"TemplatePacksCreateController"
		})
		.when("/templates/packs/:id/edit", {
			templateUrl	:	"./js/modules/templates/tmpl/packs/edit.html",
			controller	:	"TemplatePacksEditController"
		})
	
		// Mailing Module
		.when("/mailings", {
			templateUrl	:	"./js/modules/mailing/tmpl/mailing/index.html",
			controller	:	"MailingIndexController"
		})
		.when("/mailings/create", {
			templateUrl :	"./js/modules/mailing/tmpl/mailing/create.html",
			controller	:	"MailingCreateController"
		})
		.when("/mailings/:id/edit", {
			templateUrl : 	"./js/modules/editor/tmpl/editor.html",
			controller	:	"EditorIndexController"
		})
		.when("/mailings/:id/envelope", {
			templateUrl	:	"./js/modules/mailing/tmpl/mailing/envelope.html",
			controller	:	"MailingEnvelopeController"
		})
		
		// Media Module
		.when("/media", {
			templateUrl	:	"./js/modules/media/tmpl/index.html",
			controller	:	"MediaIndexController"
		})
		
		// editor module
		.when("/editor", {
			templateUrl	:	"./js/modules/editor/tmpl/editor.html",
			controller	:	"EditorIndexController"
		})
		
		// Mailing List Module
		.when("/subscription-lists", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/index.html",
			controller	:	"MailingListIndexController"
		})
		.when("/subscription-lists/create", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/create.html",
			controller	:	"MailingListCreateController"
		})
		.when("/subscription-lists/:id", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/details.html",
			controller	:	"MailingListEditController"
		})
		.when("/subscription-lists/:id/edit", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/edit.html",
			controller	:	"MailingListEditController"
		}).
		when("/subscription-lists/:id/import", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/import.html",
			controller	:	"MailingListImportController"
		}).
		when('/subscription-lists/:id/subscribers', {
			templateUrl :   './js/modules/mailinglist/tmpl/subscriptionlist/subscribers.html',
			controller	:	'MailingListSubscriberController'
		})
		
		// Landing Pages Module
		.when("/landingpages", {
			templateUrl	:	'./js/modules/landing-pages/tmpl/landing-pages/index.html',
			controller  :	'LPIndexController'
		})
		.when("/landingpages/create", {
			templateUrl :	'./js/modules/landing-pages/tmpl/landing-pages/create.html',
			controller	:	'LPCreateController'
		})
		.when("/landingpages/:id/edit", {
			templateUrl :	'./js/modules/landing-pages/tmpl/landing-pages/edit.html',
			controller	:	'LPEditController'
		})
		.when("/landingpages/:id/staticpages/:sid/edit", {
			templateUrl : 	"./js/modules/landing-pages/tmpl/landing-pages/edit.html",
			controller:		"LPEditController"
		})
		.when("/landingpages/:id/details", {
			templateUrl	:	'./js/modules/landing-pages/tmpl/landing-pages/details.html',
			controller	:	'LPDetailsController'
		})
		
		
		// Other stuff
		.when("/logout", {
			templateUrl :   "./js/modules/security/tmpl/logout.html",
			controller	:	"BMAppLogoutController"
		})
		.otherwise({
			redirectTo 	: "/subscriber"
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