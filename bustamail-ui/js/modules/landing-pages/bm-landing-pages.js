BMApp.LandingPages = angular.module('LandingPageModule', ['SecurityModule', 'ui.codemirror']);
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
	
	/**
	 * Deletes the landing page specified by the id
	 */
	$scope.deleteLandingPage = function(id) {
		BMApp.confirm("Soll die Landing Page wirklich entfernt werden?", function() {
			$http({
				method:		"DELETE",
				url:		"/api/landingpages/" + id
			}).success(function() {
				BMApp.utils.remove('id', id, $scope.pages.content);
				BMApp.alert("Die Landing Page wurde erfolgreich entfernt");
			});
		});
	};
	
	/**
	 * Creates a preview of the landing page
	 */
	$scope.createPreview = function(id) {
		$http.put("/api/landingpages/" + id + "/preview").success(function() {
			window.open("http://previews.localhost/preview_" + id + "/index.html", "_blank");
		});
	};
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

BMApp.LandingPages.controller('LPDetailsController', 
		['$scope', '$http', '$routeParams', 
    function($scope, $http, $routeParams) {

	// the data of the landing page itself
	$scope.lp 		   = undefined;
	
	// reference to the static pages of the landing page
	$scope.staticPages = [];
	
	// reference to the forms of the landing page
	$scope.forms = [];
	
	// the static page to be created / edited
	$scope.staticPage  = {};

	// the form that has the focus on created / edited
	$scope.form = { fields : [] };
	$scope.field = undefined;
	
	// the current view mode for the static pages tab
	$scope.spViewMode  = 'TABLE';

	// the current view mode for the forms tab
	$scope.formViewMode = 'TABLE';

	// initially load the landing page
	$http.get("/api/landingpages/" + $routeParams.id).success(function(data) {
		$scope.lp = data;
		$scope.staticPages = data.staticPages;
		$scope.forms = data.forms;
	});

	// watch expression on the static pages owner to load the appropriated templates.
	// This is used when creating a new static page
	$scope.$watch('staticPage.owner', function(val) {
		if (!val || val == '') return;
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
		}).then(BMApp.hideSpinner);
	});

	/**
	 * Updates the given landing pages data
	 */
	$scope.updateLandingPage = function() {
		$http({
			method:		"PATCH",
			url:		'/api/landingpages/' + $scope.lp.id,
			data:		$scope.lp,
			headers:	{"Content-Type" : "application/json"}
		}).success(function(data) {
			$scope.lp = data;
			$scope.staticPages = data.staticPages;
			BMApp.alert("Die Daten wurden erfolgreich gespeichert");
		});
	};
	
	/**
	 * Shows the form to create a new static page
	 */
	$scope.showStaticCreate = function() {
		$scope.spViewMode = 'FORM';
	};
	
	$scope.showFormCreate = function() {
		$scope.formViewMode = 'FORM';
		$scope.form = { fields : [] };
	};
	
	$scope.focusForm = function(id) {
		$scope.form = BMApp.utils.find('id', id, $scope.forms);
		$scope.formViewMode = 'FORM';
	};

	/**
	 * Cancels the focus on the current static page
	 */
	$scope.cancelStaticCreate = function() {
		$scope.staticPage = {};
		$scope.spViewMode = 'TABLE';
	};
	
	$scope.unfocusForm = function() {
		$scope.form = {};
		$scope.formViewMode = 'TABLE';
	}
	
	$scope.unfocusField = function() {
		$scope.field = undefined;
	};

	/**
	 * Creates a new static page from the given data
	 */
	$scope.createStaticPage = function() {
		$scope.staticPage.owner = undefined;
		$scope.staticPage.template = { id : $scope.staticPage.template };
		BMApp.showSpinner();
		$http.post("/api/landingpages/"+$scope.lp.id+"/staticpages/", $scope.staticPage).success(function(data) {
			$scope.staticPages.push(data);
			$scope.spViewMode = 'TABLE';
			$scope.staticPage = {};
			BMApp.alert("Die statische Seite wurde erfolgreich angelegt");
		}).then(BMApp.hideSpinner);
	};
	
	$scope.createForm = function() {
		$http.post("/api/landingpages/" + $scope.lp.id + "/forms", $scope.form).success(function(data) {
			$scope.forms.push(data);
			$scope.formViewMode = 'TABLE';
			$scope.form = {};
			BMApp.alert("Das Formular wurde erfolgreich angelegt");
		});
	};
	
	$scope.updateForm = function() {
		$http({
			method:		"PATCH",
			url:		"/api/landingpages/" + $scope.lp.id + "/forms/" + $scope.form.id,
			data:		$scope.form,
			headers:	{"Content-Type" : "application/json"}
		}).success(function(data) {
			BMApp.utils.remove('id', data.id, $scope.forms);
			$scope.forms.push(data);
			BMApp.alert("Das Formular wurde erfolgreich gespeichert");
			$scope.unfocusForm();
		});
	};
	
	
	$scope.createField = function() {
		$scope.field = {};
	};
	
	$scope.focusField = function(f) {
		$scope.field = f;
	};
	
	$scope.pushField = function() {
		BMApp.utils.remove('name', $scope.field.name, $scope.form.fields);

		var f = {}; 
		angular.copy($scope.field, f);
		$scope.form.fields.push(f);
		$scope.field = undefined;
	};
	
	$scope.purgeField = function(f) {
		BMApp.utils.remove("name", f.name, $scope.form.fields);
	};

	/**
	 * Deletes the static page by its id
	 */
	$scope.deleteStaticPage = function(id) {
		BMApp.confirm("Soll die statische Seite wirklich entfernt werden?", function() {
			$http({
				method:		"DELETE",
				url:		"/api/landingpages/" + $scope.lp.id + "/staticpages/" + id
			}).success(function() {
				BMApp.utils.remove('id', id, $scope.staticPages);
				BMApp.alert("Die statische Seite wurde erfolgreich entfernt");
			});
		});
	};

	$scope.deleteForm = function(id) {
		BMApp.confirm("Soll das Formular wirklich entfernt werden?", function() {
			$http({
				method:		"DELETE",
				url:		'/api/landingpages/' + $scope.lp.id + '/forms/' + id
			}).success(function() {
				BMApp.utils.remove('id', id, $scope.forms);
				BMApp.alert("Das Formular wurde erfolgreich entfernt");
			});
		});
	};
}]);


/**
 * Controller for editing the contents of landing pages
 */
BMApp.LandingPages.controller('LPEditController', ['$scope', '$http', '$routeParams', '$sce', function($scope, $http, $routeParams, $sce) {

	// CodeMirror instance for the resource editor
	var cme = undefined;
	$scope.resource = undefined; 	// the resource being edited
	
	$scope.pageId = './js/modules/landing-pages/tmpl/frameedit.html?lpid=' + ($routeParams.id);
	if ($routeParams.sid)
		$scope.pageId += '&sid=' + $routeParams.sid;
		
	$scope.tlink = $sce.trustAsUrl($scope.pageId);
	
	var dFrame = document.getElementById("documentFrame");
	
	// initialize the document and the widgets
	$http.get("/api/landingpages/" + $routeParams.id).success(function(data) {
	
		$scope.document = data;
		
		// fetch the full graph for the given template
		$http.get("/api/templates/templates/" + $scope.document.template.id).success(function(data) {
			$scope.document.template = data;
			$scope.widgets = data.widgets;
			for (var i in $scope.widgets) 
				if ($scope.widgets[i].source.indexOf("bm-on-replace") > 0)
					$scope.widgets[i].nestable = true;
		});
	
		// fetch all content versions for this document 
		$http.get("/api/landingpages/" + $scope.document.id + "/content").success(function(data) {
			$scope.contentVersions = data;
		});
	});
	
	/**
	 * Initializes an code mirror instance
	 */
	$scope.setupResourceCM = function(_editor) {
		_editor.setOption('lineNumbers', true);
		_editor.setOption('mode', 'css');
		_editor.setSize('100%',300);
		cme = _editor;
	};
	
	$scope.editResource = function(id) {
		$scope.resource = BMApp.utils.find('id', id, $scope.document.resources);
		$http.get('/api/media/' + id + '/content').success(function(data) {
			cme.setValue(data);
		});
	};
	
	$scope.unfocusResource = function() {
		$scope.resource = undefined;
	};
	
	$scope.updateResource = function() {
		$http({
			method:		"PATCH",
			url:		"/api/media/" + $scope.resource.id + "/content",
			data:		cme.getValue(),
			headers:	{"Content-Type" : "application/json"}
		}).success(function(data) {
			// post a message to the iframe that the css resource has changed
			var m = { type : 'resourceChanged', data : $scope.resource.id };
			dFrame.contentWindow.postMessage(m, 'http://localhost/bustamail');
		});
	};

	/**
	 * Posts a message to the iframe containig the widget to be appended
	 */
	$scope.appendWidget = function(id) {
		var w = BMApp.utils.find('id', id, $scope.widgets);
		var m = { type : 'appendWidget', data : w };
		dFrame.contentWindow.postMessage(m, "http://localhost/bustamail");
	};

	/**
	 * Posts a message to the iframe containing the widget to replace the current selected one
	 */
	$scope.replaceElement = function(id) {
		var w = BMApp.utils.find("id", id, $scope.widgets);
		var m = { type : 'replaceWidget', data : w };
		dFrame.contentWindow.postMessage(m , "http://localhost/bustamail");
	};
	
	/**
	 * Posts a message to the iframe in order to save the document
	 */
	$scope.saveContents = function() {
		var m = { type : 'saveDocument'};
		dFrame.contentWindow.postMessage(m, "http://localhost/bustamail");
	};
	
	$scope.rollBackTo = function(id) {
		var m = {type : 'rollBackTo', data : id};
		dFrame.contentWindow.postMessage(m, 'http://localhost/bustamail');
	};

	/**
	 * Creates a preview of the landing page
	 */
	$scope.createPreview = function() {
		$http.put("/api/landingpages/" + $routeParams.id + "/preview").success(function() {
			window.open("http://previews.localhost/preview_" + $routeParams.id + "/index.html", "_blank");
		});
	};
}]);