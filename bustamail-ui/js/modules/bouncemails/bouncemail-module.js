BMApp.BounceMails = angular.module('BounceMailModule', ['SecurityModule']);

BMApp.BounceMails.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when('/bounce-accounts', {
			templateUrl : './js/modules/bouncemails/views/accounts/index.html',
			controller  : 'BounceAccountIndexController'
		})
		.when('/bounce-accounts/create', {
			templateUrl : './js/modules/bouncemails/views/accounts/create.html',
			controller	: 'BounceAccountCreateController'
		})
		.when('/bounce-accounts/:id/edit', {
			templateUrl : './js/modules/bouncemails/views/accounts/edit.html',
			controller	: 'BounceAccountEditController',
		})
		.when('/bounce-accounts/:id/mails', {
			templateUrl : './js/modules/bouncemails/views/mails/index.html',
			controller	: 'BounceMailIndexController'
		})
		.when('/bounce-accounts/:id/mails/:mid', {
			templateUrl : './js/modules/bouncemails/views/mails/details.html',
			controller	: 'BounceMailDetailsController'
		});
}]);