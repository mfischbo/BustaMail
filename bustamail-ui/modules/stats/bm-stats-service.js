BMApp.stats = angular.module('StatsModule', []);
BMApp.stats.service('StatsService', ['$http', function($http) {

	return {
		
		getSendingStatsByMailing : function(mailing) {
			return $http.get('/api/stats/' + mailing.id).success(function(data) {
				return data;
			});
		}
	};
	
}]);