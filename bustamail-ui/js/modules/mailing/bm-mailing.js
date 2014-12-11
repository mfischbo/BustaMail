BMApp.Mailing = angular.module("MailingModule", ['SecurityModule']);

BMApp.Mailing.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when("/mailings", {
			templateUrl	:	"./js/modules/mailing/tmpl/mailing/index.html",
			controller	:	"MailingIndexController"
		})
		.when("/mailings/create", {
			templateUrl :	"./js/modules/mailing/tmpl/mailing/create.html",
			controller	:	"MailingCreateController"
		})
		.when("/mailings/:id/edit", {
			templateUrl : 	"./js/modules/editor/tmpl/editor.html",
			controller	:	"EditorIndexController"
		})
		.when("/mailings/:id/envelope", {
			templateUrl	:	"./js/modules/mailing/tmpl/mailing/envelope.html",
			controller	:	"MailingEnvelopeController"
		});
}]);

BMApp.Mailing.controller("MailingIndexController", ['$scope', '$http', function($scope, $http) {
	
	$scope.mailings = {};
	$scope.owner    = undefined;

	$scope.$watch('owner', function(val) {
		if (!val) return;
		$http.get("/api/mailings/unit/" + val).success(function(data) {
			$scope.mailings = data;
		});
	});

	/**
	 * Requests the approval for a given mailing
	 */
	$scope.requestApproval = function(id) {
		BMApp.confirm("Soll die Freigabe für das Mailing eingeholt werden?", function() {
			$http.get("/api/mailings/" + id + "/requestApproval").success(function() {
				BMApp.alert("Die Freigabe wurde angefordert!");
				(BMApp.utils.find('id', id, $scope.mailings.content)).approvalRequested = true;
			});
		});
	};
	
	$scope.approveMailing = function(id) {
		BMApp.confirm("Soll die Freigabe für das Mailing erteilt werden?", function() {
			$http.put("/api/mailings/" + id + "/approve").success(function() {
				BMApp.alert("Die Freigabe wurde erteilt.");
				(BMApp.utils.find('id', id, $scope.mailings.content)).approved = true;
			});
		});
	};
	
	$scope.publishMailing = function(id) {
		BMApp.confirm("Das Mailing wird nun veroeffentlicht. Fortsetzen?", function() {
			$http.put("/api/mailings/"+id+"/publish").success(function() {
				
			});
		});
	};

	/**
	 * Sends a preview of the mailing to the current users email address
	 */
	$scope.sendPreview = function(id) {
		$http.put("/api/mailings/"+id+"/preview").success(function() {
			BMApp.alert("Die Vorschau wurde erfolgreich versendet");
		});
	};
	

	/**
	 * Removes the mailing with the given id
	 */
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


BMApp.Mailing.controller("MailingEnvelopeController", ['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {

	$scope.mailing = undefined;
	$scope.listOwner = undefined;
	$scope.sLists = {};
	
	$scope.$watch('listOwner', function(val) {
		if (!val) return;
		$http.get("/api/subscription-lists?owner=" + $scope.listOwner).success(function(data) {
			$scope.sLists = data;
		});
	});
	
	$http.get("/api/mailings/" + $routeParams.id).success(function(data) {
		$scope.mailing = data;
	});
	
	/**
	 * Returns whether the given subscription list is attached to the current mailing
	 */
	$scope.isAttached = function(listId) {
		if (!$scope.mailing || !$scope.mailing.subscriptionLists) return false;
		return (BMApp.utils.indexOf('id', listId, $scope.mailing.subscriptionLists) > -1);
	};

	/**
	 * Adds or removes the subscription list with the given id from the mailing
	 */
	$scope.toggleSubscriptionList = function(listId) {
		if (!$scope.isAttached(listId)) {
			$http.put("/api/mailings/" + $routeParams.id+ "/subscription-lists/" + listId).success(function() {
				$scope.mailing.subscriptionLists.push(BMApp.utils.find('id', listId, $scope.sLists.content));
			});
		} else {
			$http({
				method : "DELETE",
				url	:	"/api/mailings/" + $routeParams.id + "/subscription-lists/" + listId
			}).success(function() {
				BMApp.utils.remove("id", listId, $scope.mailing.subscriptionLists);
			});
		}
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