BMApp.MailingList = angular.module("MailingListModule", ['SecurityModule', 'angularFileUpload']);
BMApp.MailingList.config(['$routeProvider', function($routeProvider) {
	
	$routeProvider
		.when("/subscription-lists", {
			templateUrl	:	"./modules/mailinglist/tmpl/subscriptionlist/index.html",
			controller	:	"MailingListIndexController"
		})
		.when("/subscription-lists/create", {
			templateUrl	:	"./modules/mailinglist/tmpl/subscriptionlist/create.html",
			controller	:	"MailingListCreateController"
		})
		.when("/subscription-lists/:id", {
			templateUrl	:	"./modules/mailinglist/tmpl/subscriptionlist/details.html",
			controller	:	"MailingListEditController"
		})
		.when("/subscription-lists/:id/edit", {
			templateUrl	:	"./modules/mailinglist/tmpl/subscriptionlist/edit.html",
			controller	:	"MailingListEditController"
		}).
		when("/subscription-lists/:id/import", {
			templateUrl	:	"./modules/mailinglist/tmpl/subscriptionlist/import.html",
			controller	:	"MailingListImportController"
		}).
		when('/subscription-lists/:id/subscribers', {
			templateUrl :   './modules/mailinglist/tmpl/subscriptionlist/subscribers.html',
			controller	:	'MailingListSubscriberController'
		});
}]);

/**
 * Controller to show the index page of mailing lists
 */
BMApp.MailingList.controller("MailingListIndexController", 
		['$scope', '$http', function($scope, $http) {

	$scope.owner = undefined;
	$scope.subscriptionLists = {};
	
	var tout = undefined;
	
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
	
	$scope.search = function() {
		
		if (tout)
			window.clearTimeout(tout);
		tout = window.setTimeout(function() {
			if ($scope.query.length == 0) {
				$http.get('/api/subscription-lists?owner=' + $scope.owner).success(function(data) {
					$scope.subscriptionLists = data;
				});
			}
			
			if ($scope.query.length > 2) {
				$http.get('/api/subscription-lists?owner=' + $scope.owner + '&q=' + $scope.query).success(function(data) {
					$scope.subscriptionLists = data;
				});
			}
		}, BMApp.uiConfig.searchDelay);
	};
}]);

/**
 * Controller to create a new MailingList
 */
BMApp.MailingList.controller("MailingListCreateController", ['$scope', '$http', '$location', function($scope, $http, $location) {

	$scope.list = {};
	
	$scope.createList = function() {
		$http.post("/api/subscription-lists", $scope.list).success(function(data) {
			$location.path("/subscription-lists/" + data.id)
		}).error(function() {
			
		});
	};
}]);


/**
 * Controller to edit a mailing lists base data
 */
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


/**
 * Controller to display all subscribers for the given subscription list
 */
BMApp.MailingList.controller("MailingListSubscriberController", 
		['$scope', '$http', '$routeParams', function($scope, $http, $routeParams) {

	$scope.query = '';
	$scope.listId = $routeParams.id;
	var tout = undefined;
			
	$http.get('/api/subscription-lists/' + $routeParams.id + '/summary').success(function(data) {
		$scope.summary = data;
	});
	
	$http.get('/api/subscription-lists/' + $routeParams.id + '/subscriptions').success(function(data) {
		$scope.subscriptions = data;
	});
	
	
	/**
	 * Searches for subscriptions, matching the given $scope.query
	 */
	$scope.search = function() {
		if (tout)
			window.clearTimeout(tout);
		
		tout = window.setTimeout(function() {
			if ($scope.query.length == 0) {
				$http.get('/api/subscription-lists/' + $routeParams.id + '/subscriptions').success(function(data) {
					$scope.subscriptions = data;
				});
			}
			if ($scope.query.length > 2) {
				$http.get('/api/subscription-lists/' + $routeParams.id + '/subscriptions/search?q=' + $scope.query)
				.success(function(result) {
					$scope.subscriptions = result;
				});
			}
		}, BMApp.uiConfig.searchDelay);
	};
	
	
	/**
	 * @param subscription - The subscription to be unsubscribed
	 */
	$scope.unsubscribe = function(subscription) {
		BMApp.confirm("Soll der Abonnent wirklich aus der Liste entfernt werden?", function() {
			$http({
				url : '/api/subscription-lists/' + $routeParams.id + '/subscriptions/' + subscription.id,
				method: 'DELETE'
			}).success(function() {
				BMApp.alert("Der Abonnent wurde als abgemeldet markiert");
				subscription.state = 'INACTIVE';
				$scope.summary.subscriptionsActive--;
				$scope.summary.subscriptionInactive++;
			});
		});
	};
}]);


/**
 * Controller to import data from xlsx / csv into a mailing list as subscribers
 */
BMApp.MailingList.controller("MailingListImportController", 
		['$scope', '$http', '$routeParams', '$upload', function($scope, $http, $routeParams, $upload) {

	$scope.list = undefined;
	$scope.settings = {fieldNames : []};		// settings for the import
	
	$scope.page = undefined;
	$scope.data = undefined;
	
	$scope.psize = 15;
	$scope.step  = 0;
	
	$http.get("/api/subscription-lists/"+$routeParams.id).success(function(data) {
		$scope.list = data;
	});
	
	/**
	 * Uploads the selected file and returns the mediaDTO stored in $scope.file
	 */
	$scope.onImportFileSelect = function($files) {
		BMApp.showSpinner();
		$scope.upload = $upload.upload({
			url		:	"/api/subscription-lists/" + $routeParams.id + "/subscriptions/upload",
			method	:	"POST",
			file	:	$files[0]
		}).success(function(data) {
			$scope.settings = data;
			$scope.settings.fieldNames = [];
			$scope.settings.encoding = "UTF8";
			BMApp.hideSpinner();
			BMApp.alert("Datei wurde erfolgreich hochgeladen");
			$scope.parse();
		});
	};
	
	$scope.parse = function() {
		$http.post("/api/subscription-lists/" + $routeParams.id + "/subscriptions/parse", $scope.settings).success(function(data) {
			$scope.data = data.data;
			$scope.page = data.data.slice(0, $scope.psize);
			
			// calculate page counts
			$scope.data.totalElements = $scope.data.length;
			$scope.data.numPages      = Math.ceil($scope.data.totalElements / $scope.psize);
			$scope.data.currentPage   = 0;
			
			// initialize all fieldNames with an empty string
			for (var i in $scope.data[0])
				$scope.settings.fieldNames.push("");
		});
	};
	
	$scope.checkForErrors = function() {
		$http.post("/api/subscription-lists/" + $routeParams.id + "/subscriptions/status", $scope.settings).success(function(data) {
			$scope.status = data;
			if ($scope.status.errorLines.length > 0)
				$scope.currentIssueIndex = -1;
		});
	};
	
	$scope.import = function() {
		$http.post("/api/subscription-lists/" + $routeParams.id + "/subscriptions/import", $scope.settings).success(function(data) {
			
		});
	};
	
	
	/**
	 * Jumps to certain positions in the data set
	 */
	$scope.slice = function(num) {
		if (num == 'first') {
			$scope.page = $scope.data.slice(0, $scope.psize);
			$scope.data.currentPage = 0;
		}
		
		if (num == 'next') {
			$scope.data.currentPage++;
			var s = $scope.data.currentPage * $scope.psize;
			$scope.page = $scope.data.slice(s, s + $scope.psize);
		}
		
		if (num == 'prev') {
			$scope.data.currentPage--;
			var s = $scope.data.currentPage * $scope.psize;
			$scope.page = $scope.data.slice(s, s + $scope.psize);
		}
		
		if (num == 'last') {
			$scope.data.currentPage = ($scope.data.numPages -1);
			$scope.page = $scope.data.slice($scope.data.currentPage * $scope.psize);
		}
	};
	
	$scope.nextIssue = function() {
		var item = $scope.status.errorLines[$scope.currentIssueIndex + 1];
		$scope.currentIssueIndex++;
		$scope.jumpToPage(item);
	};
	
	$scope.previousIssue = function() {
		var item = $scope.status.errorLines[$scope.currentIssueIndex -1];
		$scope.currentIssueIndex--;
		$scope.jumpToPage(item);
	};
	
	$scope.jumpToPage = function(num) {
		$scope.data.currentPage = Math.floor(num / $scope.psize);
		var offset = num % $scope.psize;
		var s = num - offset;
		$scope.page = $scope.data.slice(s, s + $scope.psize);
	};
	
	$scope.editLine = function() {
		$scope.line = $scope.data[$scope.status.errorLines[$scope.currentIssueIndex]];
	};
	
}]);
