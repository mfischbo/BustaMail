BMApp.dashboard = angular.module('DashBoardModule', ['MailingModule', 'StatsModule']);

BMApp.dashboard.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when('/', {
			templateUrl: './modules/dashboard/views/index.html',
			controller : 'DashBoardController'
		})
		.otherwise( { "redirectTo" : '/' });
}]);

BMApp.dashboard.controller('DashBoardController', ['StatsService', function(service) {


}]);


BMApp.dashboard.controller('RecentMailingsController', ['$scope', 'MailingService', 'StatsService', function($scope, mService, sService) {

	$scope.stats = {}
	$scope.currentStats = {};
	
	mService.getAllVisibleMailings('datePublished', 'desc', 8).success(function(data) {
		$scope.mailings = data.content;
		for (var i in $scope.mailings)
			$scope.mailings[i].isOpen = false;
	});
	

	$scope.toggleState = function(m) {
		m.isOpen = !m.isOpen;
		
		if (m.isOpen && !$scope.stats[m.id]) {
			sService.getSendingStatsByMailing(m).success(function(data) {
				if (!data.openingRate) data.openingRate = 0;
				if (!data.sendingSuccessRate) data.sendingSuccessRate = 0;
				$scope.stats[m.id] = data;
				$scope.currentStats = $scope.stats[m.id];
			});
		} else if (m.isOpen) {
			$scope.currentStats = $scope.stats[m.id];
		}
	};
}]);