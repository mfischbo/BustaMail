BMApp.BounceMails.controller('BounceAccountIndexController', ['$scope', 'BounceAccountService', function($scope, service) {

	$scope.owner = undefined;
	$scope.accounts = {};
	
	$scope.$watch('owner', function(val) {
		if (!val) return;
		service.getAllByOwner(val).success(function(accounts) {
			$scope.accounts = accounts;
		});
	});
	
	$scope.deleteAccount = function(account) {
		BMApp.confirm("Soll der Account wirklich entfernt werden?", function() {
			service.deleteAccount(account.id).success(function() {
				BMApp.alert("Der Account wurde entfernt");
				BMApp.utils.remove('id', account.id, $scope.accounts.content);
			});
		});
	};
}]);

BMApp.BounceMails.controller('BounceAccountCreateController', ['$scope', '$location', 'BounceAccountService', function($scope, $location, service) {

	$scope.account = {
			port		: 110,
			enabled 	: true,
			accountType : 'POP3',
			pollInterval: 60
	};

	$scope.createAccount = function() {
		service.createAccount($scope.account).success(function(data) {
			BMApp.alert("Der Account wurde erfolgreich angelegt");
			$location.path("/bounce-accounts");
		});
	};
}]);

BMApp.BounceMails.controller('BounceAccountEditController', ['$scope', '$routeParams', 'BounceAccountService', function($scope, $routeParams, service) {
	
	$scope.account = {};
	service.getAccountById($routeParams.id).success(function(account) {
		$scope.account = account;
	});
	
	
	$scope.updateAccount = function() {
		service.updateAccount($scope.account).success(function(account) {
			$scope.account = account;
			BMApp.alert("Der Account wurde erfolgreich gespeichert");
		});
	};
}]);

BMApp.BounceMails.controller('BounceMailIndexController', ['$scope', 'BounceAccountService', function($scope, service) {
	
}]);

BMApp.BounceMails.controller('BounceMailDetailsController', ['$scope', 'BounceAccountService', function($scope, service) {
	
}]);