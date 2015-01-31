BMLogin = angular.module("BMLogin", ['ngRoute']);

BMLogin.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when("/login", {
			templateUrl : "./modules/core/tmpl/login.html",
			controller	: "BMLoginController"
		})
		.when("/recover", {
			templateUrl : "./modules/core/tmpl/password-recover.html",
			controller	: "BMRecoverPasswordController"
		})
		.otherwise({redirectTo : "/login"});
}]);


BMLogin.controller("BMLoginController", ['$scope', '$http', '$location', function($scope, $http, $location) {
	
	$scope.credentials = {
	};
	
	$scope.loginFailed = false;
	
	$scope.login = function() {
		$http.post("/api/security/authentication", $scope.credentials).success(function(data) {
			window.location.href = "./";
		}).error(function() {
			$scope.loginFailed = true;
		});
	};
}]);

BMLogin.controller("BMRecoverPasswordController", ['$scope', '$http', function($scope, $http) {

	$scope.credentials = {
			
	};
	$scope.showMessage = false;
	
	
	$scope.recover = function() {
		$http.post("/api/security/users/recover", $scope.credentials).success(function() {
			$scope.showMessage = true;
		});
	};
}]);