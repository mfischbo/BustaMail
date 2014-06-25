BMApp.MailingList = angular.module("MailingListModule", ['SecurityModule']);

BMApp.MailingList.controller("MailingListIndexController", 
		['$scope', '$http', function($scope, $http) {

	$scope.owner = undefined;
	$scope.subscriptionLists = {};
	
	$scope.$watch('owner', function(val) {
		if (val) {
			$http.get("/api/subscription-lists?owner=" + $scope.owner).success(function(data) {
				$scope.subscriptionLists = data;
			});
		};
	});
	
	$scope.deleteSubscriptionList = function(id) {
		BMApp.confirm("Soll die Verteilerliste wirklich entfernt werden?", function() {
			$http({
				method:		"DELETE",
				url:		"/api/subscription-lists/" + id
			}).success(function() {
				BMApp.alert("Die Liste wurde erfolgreich entfernt!");
				BMApp.utils.remove("id", id, $scope.subscriptionLists.content);
			}).error(function() {
				BMApp.alert("Beim entfernen der Liste ist ein Fehler aufgetreten", 'error');
			});
		});
	};
}]);


BMApp.MailingList.controller("MailingListCreateController", ['$scope', '$http', '$location', function($scope, $http, $location) {

	$scope.list = {};
	
	$scope.createList = function() {
		$http.post("/api/subscription-lists", $scope.list).success(function(data) {
			$location.path("/subscription-lists/" + data.id)
		}).error(function() {
			
		});
	};
}]);


BMApp.MailingList.controller("MailingListEditController", 
		['$scope', '$http', '$routeParams', '$location', function($scope, $http, $routeParams, $location) {

	$scope.list = undefined;
	$http.get("/api/subscription-lists/" + $routeParams.id).success(function(data) {
		$scope.list = data;
	}).error(function() {
		
	});
	
	$scope.updateList = function() {
		$http({
			method:		"PATCH",
			url:		"/api/subscription-lists/" + $routeParams.id,
			data:		$scope.list
		}).success(function(data) {
			BMApp.alert("Die Liste wurde erfolgreich gespeichert!");
			$location.path("/subscription-lists/" + $routeParams.id);
		});
	};
}]);