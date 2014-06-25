BMApp.Mailing = angular.module("MailingModule", ['SecurityModule']);

BMApp.Mailing.controller("MailingIndexController", ['$scope', '$http', function($scope, $http) {
	
	$scope.mailings = {};
	$scope.owner    = undefined;

	$scope.$watch('owner', function(val) {
		if (!val) return;
		$http.get("/api/mailings/unit/" + val).success(function(data) {
			$scope.mailings = data;
		});
	});
	
	$scope.deleteMailing = function(id) {
		BMApp.confirm("Soll das Mailing wirklich entfernt werden?", function() {
			$http({
				method	:	"DELETE",
				url		:	"/api/mailings/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.mailings.content);
			});
		});
	};
}]);


BMApp.Mailing.controller("MailingCreateController", ['$scope', '$http', '$location', function($scope, $http, $location) {
	
	$scope.templates = [];
	$scope.owner = undefined;
	$scope.mailing = {};
	
	$scope.$watch('owner', function(val) {
		if (!val) return;
		$http.get("/api/templates/" + val + "/packs").success(function(data) {
			$scope.templates = [];
			for (var i in data.content) {
				var p = data.content[i].name;
				for (var t in data.content[i].templates) {
					data.content[i].templates[t].pack = p;
					$scope.templates.push(data.content[i].templates[t]);
				}
			}
			console.log($scope.templates);
		});
	});

	$scope.createMailing = function() {
		var id = $scope.mailing.template;
		$scope.mailing.template = { id : id };
		$http.post("/api/mailings/unit/" + $scope.owner, $scope.mailing).success(function(data) {
			$location.path("/mailings");
		});
	};
	
	$scope.dismissForm = function() {
		$location.path("/mailings");
	};
}]);