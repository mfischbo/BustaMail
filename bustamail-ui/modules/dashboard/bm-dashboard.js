BMApp.dashboard = angular.module('DashBoardModule', ['StatsModule']);

BMApp.dashboard.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when('/', {
			templateUrl: './modules/dashboard/views/index.html',
			controller : 'DashBoardController'
		})
		.otherwise( { "redirectTo" : '/' });
}]);

BMApp.dashboard.controller('DashBoardController', ['StatsService', function(service) {
	console.log("init controller");
}]);