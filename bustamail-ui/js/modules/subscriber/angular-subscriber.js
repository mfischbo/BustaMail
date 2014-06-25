BMApp.Subscriber = angular.module("SubscriberModule", []);
//BMApp.Subscriber.service = angular.module("SubscriberServiceModule", []);

BMApp.Subscriber.service("Subscriber", ['$http', function($http) {
	
	return {
		getAllContacts : function(cb) {
			$http.get("/api/contacts").success(function(data) {
				return cb(true, data);
			});
		}
	};
}]);


BMApp.Subscriber.controller("SubscriberIndexController", ['$scope', 'Subscriber', function($scope, Service) {

	$scope.contacts = {};
	Service.getAllContacts(function(state, data) {
		console.log(data);
		$scope.contacts = data;
	});
	
}]);