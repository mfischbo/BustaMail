BMApp.Subscriber = angular.module("SubscriberModule", []);
BMApp.Subscriber.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when("/subscriber", {
			templateUrl : "./js/modules/subscriber/tmpl/index.html",
			controller	: "SubscriberIndexController"
		});
}]);


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
	/*
	Service.getAllContacts(function(state, data) {
		console.log(data);
		$scope.contacts = data;
	});
	*/
}]);