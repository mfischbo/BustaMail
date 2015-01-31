BMApp.Subscriber = angular.module("SubscriberModule", []);
BMApp.Subscriber.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when("/subscriber", {
			templateUrl : "./modules/subscriber/tmpl/index.html",
			controller	: "SubscriberIndexController"
		})
		.when('/subscriber/:id/edit', {
			templateUrl : './modules/subscriber/tmpl/edit.html',
			controller	: 'SubscriberEditController'
		});
}]);


BMApp.Subscriber.service("SubscriberService", ['$http', function($http) {
	
	return {
		getAllContacts : function(cb) {
			$http.get("/api/contacts").success(function(data) {
				return cb(true, data);
			});
		},
		
		getSubscriberById : function(id) {
			return $http.get('/api/contacts/' + id).success(function(data) {
				return data;
			});
		},
		
		saveSubscriber : function(subscriber) {
			return $http.patch('/api/contacts/' + subscriber.id, subscriber).success(function(data) {
				return data;
			});
		}
	};
}]);


BMApp.Subscriber.controller("SubscriberIndexController", ['$scope', 'SubscriberService', function($scope, Service) {

	$scope.contacts = {};
	/*
	Service.getAllContacts(function(state, data) {
		console.log(data);
		$scope.contacts = data;
	});
	*/
}]);

BMApp.Subscriber.controller('SubscriberEditController', ['$scope', '$routeParams', 'SubscriberService', function($scope, $routeParams, sService) {

	$scope.subscriber = undefined;
	$scope.focusedAddress = undefined;
	$scope.addressMode = 'view';
	
	sService.getSubscriberById($routeParams.id).success(function(sub) {
		$scope.subscriber = sub;
	});
	
	
	$scope.addAddress = function() {
		$scope.focusedAddress = {};
		$scope.addressMode = 'edit';
	};
	
	$scope.editAddress = function(a) {
		$scope.focusedAddress = a;
		$scope.addressMode    = 'edit';
	};
	
	$scope.cancelEdit = function() {
		$scope.focusedAddress = undefined;
		$scope.addressMode = 'view';
	};
	
	$scope.updateAddress = function() {
		if (!$scope.focusedAddress.id)
			$scope.subscriber.addresses.push($scope.focusedAddress);
		$scope.focusedAddress = undefined;
		$scope.addressMode = 'view';
	};
	
	$scope.saveSubscriber = function() {
		sService.saveSubscriber($scope.subscriber).success(function(subscriber) {
			$scope.subscriber = subscriber;
			BMApp.alert('Der Kontakt wurde erfolgreich bearbeitet');
		}).error(function() {
			BMApp.alert('Beim bearbeiten des Kontakts ist ein Fehler aufgetreten', 'error');
		});
	};
}]);