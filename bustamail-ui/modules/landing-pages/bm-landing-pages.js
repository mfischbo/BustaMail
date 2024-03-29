BMApp.LandingPages = angular.module('LandingPageModule', ['SecurityModule', 'ui.codemirror']);
BMApp.LandingPages.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when("/landingpages", {
			templateUrl	:	'./modules/landing-pages/tmpl/landing-pages/index.html',
			controller  :	'LPIndexController'
		})
		.when("/landingpages/create", {
			templateUrl :	'./modules/landing-pages/tmpl/landing-pages/create.html',
			controller	:	'LPCreateController'
		})
		.when("/landingpages/:id/edit", {
			templateUrl :	'./modules/landing-pages/tmpl/landing-pages/edit.html',
			controller	:	'LPEditController'
		})
		.when("/landingpages/:id/staticpages/:sid/edit", {
			templateUrl : 	"./modules/landing-pages/tmpl/landing-pages/edit.html",
			controller:		"LPEditController"
		})
		.when("/landingpages/:id/details", {
			templateUrl	:	'./modules/landing-pages/tmpl/landing-pages/details.html',
			controller	:	'LPDetailsController'
		});
}]);

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
		var p = BMApp.utils.find('id', id, $scope.pages.content);
		if (p && p.published) {
			BMApp.alert("Die Seite kann nicht entfernt werden, da sie noch online ist!", 'warning');
			return;
		}
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
			window.open(BMApp.uiConfig.previewURL + "/preview_" + id + "/index.html", "_blank");
		});
	};
	
	$scope.publishLive = function(id) {
		BMApp.confirm("Soll die Seite in Betrieb genommen werden?", function() {
			$http.put("/api/landingpages/" + id + "/publish").success(function(data) {
				BMApp.alert("Die Seite wurde erfolgreich in Betrieb genommen");
				for (var i in $scope.pages.content) {
					if ($scope.pages.content[i].id == id) 
						$scope.pages.content[i] = data;
				}
			});
		});
	};
	
	$scope.unpublishSite = function(id) {
		BMApp.confirm("Achtung! Die Seite wird aus dem Live Betrieb genommen!", function() {
			$http({
				method:		"DELETE",
				url:		"/api/landingpages/" + id + "/publish"
			}).success(function(data) {
				BMApp.alert("Die Seite wurde erfolgreich aus dem Live Betrieb genommen");
				for (var i in $scope.pages.content) {
					if ($scope.pages.content[i].id == id)
						$scope.pages.content[i] = data;
				}
			});
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
		$http.get('/api/templatepacks?owner=' + val).success(function(data) {
			$scope.templates = [];
			for (var i in data.content) {
				var p = data.content[i];
				for (var t in data.content[i].templates) {
					data.content[i].templates[t].pack = p;
					$scope.templates.push(data.content[i].templates[t]);
				}
			}
			console.log($scope.templates);
		}).then(BMApp.hideSpinner);
	});
	
	$scope.createLandingPage = function() {
		
		$scope.page.templatePack = $scope.page.template.pack;
		$scope.page.template.pack = undefined;
		$scope.page.templateId = $scope.page.template.id;
		$scope.page.template = undefined;
		
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

	// the owner of the mailing template to be selected when editing a form
	$scope.owner = '';
	
	// code mirror instance to edit the HTML Head
	var _headCM = undefined;
	
	// initially load the landing page
	$http.get("/api/landingpages/" + $routeParams.id).success(function(data) {
		$scope.lp = data;
		$scope.staticPages = data.staticPages;
		$scope.forms = data.forms;
		$scope.forms.recipients;
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
	
	$scope.$watch('form.mailTemplateOwner', function(val) {
		if (!val || val == '') return;
		$http.get("/api/templates/" + val + "/packs").success(function(data) {
			$scope.mailingTemplates = [];
			for (var i in data.content) {
				var p = data.content[i].name;
				for (var t in data.content[i].templates) {
					data.content[i].templates[t].pack = p;
					$scope.mailingTemplates.push(data.content[i].templates[t]);
				}
			}
		});
	});
	
	
	$scope.createHeadCM = function(_editor) {
		_editor.setOption("lineNumbers", true);
		_editor.setOption("mode", "htmlmixed");
		_headCM = _editor;
	};
	

	/**
	 * Updates the given landing pages data
	 */
	$scope.updateLandingPage = function() {
		$scope.lp.htmlHeader = _headCM.getValue();
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
		if (!$scope.form.recipients || $scope.form.recipients.length == 0)
			$scope.form.recipients.push('');
		$scope.form.mailTemplate = $scope.form.mailTemplate.id;
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
		$scope.form.mailTemplateOwner = undefined;
		$scope.form.mailTemplate = { id : $scope.form.mailTemplate };
		$http.post("/api/landingpages/" + $scope.lp.id + "/forms", $scope.form).success(function(data) {
			$scope.forms.push(data);
			$scope.formViewMode = 'TABLE';
			$scope.form = {};
			BMApp.alert("Das Formular wurde erfolgreich angelegt");
		});
	};
	
	$scope.updateForm = function() {
		$scope.form.mailTemplateOwner = undefined;
		$scope.form.mailTemplate = { id : $scope.form.mailTemplate };
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
	
	$scope.pageId = './frameedit.html?type=landingpage&cid=' + ($routeParams.id);
	if ($routeParams.sid)
		$scope.pageId = './frameedit.html?type=static&cid=' + $routeParams.sid;
		
	$scope.tlink = $sce.trustAsUrl($scope.pageId);
	
	var dFrame = document.getElementById("documentFrame");
	window.addEventListener("message", function(e) {
	
		if (e.data.type == 'elementSelected') {
			$scope.element = e.data.data;
			$scope.$apply();
		}
		
		if (e.data.type == 'elementUnselected') {
			$scope.element = undefined;
			$scope.$apply();
		}
	});
	
	// initialize the document and the widgets
	$http.get("/api/landingpages/" + $routeParams.id).success(function(data) {
	
		$scope.document = data;
		
		// fetch the full graph for the given template
		$http.get("/api/templatepacks/" + $scope.document.templatePack.id + "/templates/" + $scope.document.templateId).success(function(data) {
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
			dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
		});
	};

	/**
	 * Posts a message to the iframe containig the widget to be appended
	 */
	$scope.appendWidget = function(id) {
		var w = BMApp.utils.find('id', id, $scope.widgets);
		var m = { type : 'appendWidget', data : w };
		dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
	};

	/**
	 * Posts a message to the iframe containing the widget to replace the current selected one
	 */
	$scope.replaceElement = function(id) {
		var w = BMApp.utils.find("id", id, $scope.widgets);
		var m = { type : 'replaceWidget', data : w };
		dFrame.contentWindow.postMessage(m , BMApp.uiConfig.uiURL);
	};
	
	/**
	 * Posts a message to the iframe in order to save the document
	 */
	$scope.saveContents = function() {
		var m = { type : 'saveDocument'};
		dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
	};
	
	$scope.rollBackTo = function(id) {
		var m = {type : 'rollBackTo', data : id};
		dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
	};

	/**
	 * Creates a preview of the landing page
	 */
	$scope.createPreview = function() {
		$http.put("/api/landingpages/" + $routeParams.id + "/preview").success(function() {
			window.open(BMApp.uiConfig.previewURL + "/preview_" + $routeParams.id + "/index.html", "_blank");
		});
	};
}]);