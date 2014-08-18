BMApp.LandingPages = angular.module('LandingPageModule', ['SecurityModule']);
BMApp.LandingPages.controller('LPIndexController', ['$scope', '$http', function($scope, $http) {

	$scope.owner = '';
	$scope.pages = {};

	$scope.$watch('owner', function(val) {
		if (!$scope.owner || $scope.owner.length == 0) return;
		BMApp.showSpinner();
		$http.get('/api/landingpages?owner=' + $scope.owner).success(function(data) {
			$scope.pages = data;
		}).then(BMApp.hideSpinner);
	});
}]);


BMApp.LandingPages.controller('LPCreateController', ['$scope', '$http', '$location', function($scope, $http, $location) {
	
	$scope.templates = [];
	$scope.owner = undefined;
	$scope.page  = {};
	
	$scope.$watch('owner', function(val) {
		if (!val) return;
		
		BMApp.showSpinner();
		$http.get('/api/templates/' + val + '/packs').success(function(data) {
			$scope.templates = [];
			for (var i in data.content) {
				var p = data.content[i].name;
				for (var t in data.content[i].templates) {
					data.content[i].templates[t].pack = p;
					$scope.templates.push(data.content[i].templates[t]);
				}
			}
			console.log($scope.templates);
		}).then(BMApp.hideSpinner);
	});
	
	$scope.createLandingPage = function() {
		var id = $scope.page.template;
		$scope.page.template = { id : id };
		$scope.page.owner    = $scope.owner;
		$http.post("/api/landingpages", $scope.page).success(function() {
			BMApp.alert("Die Landing Page wurde erfolgreich angelegt");
			$location.path("/landingpages");
		});
	};
	
	$scope.dismissForm = function() {
		$location.path("/landingpages");
	};
}]);


/**
 * Controller for editing the contents of landing pages
 */
BMApp.LandingPages.controller('LPEditController', ['$scope', '$http', '$routeParams', '$sce', function($scope, $http, $routeParams, $sce) {

	$scope.pageId = './js/modules/landing-pages/tmpl/frameedit.html?id=' + ($routeParams.id);
	$scope.tlink = $sce.trustAsUrl($scope.pageId);
	console.log($scope.tlink);
	
	var dFrame = document.getElementById("documentFrame");
	
	// initialize the mailing and the widgets
	$http.get("/api/landingpages/" + $routeParams.id).success(function(data) {
	
		$scope.mailing = data;
		
		// fetch the full graph for the given template
		$http.get("/api/templates/templates/" + $scope.mailing.template.id).success(function(data) {
			$scope.mailing.template = data;
			$scope.widgets = data.widgets;
			for (var i in $scope.widgets) 
				if ($scope.widgets[i].source.indexOf("bm-on-replace") > 0)
					$scope.widgets[i].nestable = true;
		});
	
		// fetch all content versions for this mailing
		$http.get("/api/landingpages/" + $scope.mailing.id + "/content").success(function(data) {
			$scope.contentVersions = data;
		});
	});
	
	$scope.appendWidget = function(id) {
		var w = BMApp.utils.find('id', id, $scope.widgets);
		var m = { type : 'appendWidget', data : w };
		dFrame.contentWindow.postMessage(m, "http://localhost/bustamail");
	}
}]);