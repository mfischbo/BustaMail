BMApp.Templates = angular.module("TemplatesModule", 
		['SecurityModule', 'ui.codemirror', 'angularFileUpload']);


BMApp.Templates.controller("TemplatePacksIndexController", ['$http', '$scope', function($http, $scope) {

	$scope.owner = undefined;
	$scope.packs = {};
	
	$scope.$watch('owner', function(owner) {
		if (!owner) return;
		$http.get("/api/templates/" + $scope.owner + "/packs").success(function(data) {
			$scope.packs = data;
		});
	});
	
	$scope.copyPack = function(id) {
		BMApp.showSpinner();
		$http.put("/api/templates/packs/" + id + "/clone").success(function(data) {
			$scope.packs.content.push(data);
			BMApp.hideSpinner();
		}).error(function(){
			BMApp.hideSpinner();
		});
	};
	
	$scope.downloadPack = function() {
		
	};
	
	$scope.deletePack = function(id) {
		BMApp.confirm("Soll der Template Pack und alle seine Inhalte wirklich entfernt werden?", function() {
			$http({
				method 		:	"DELETE",
				url			: 	"/api/templates/packs/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.packs.content);
			});
		});
	};
}]);


BMApp.Templates.controller("TemplatePacksCreateController", ['$http', '$scope', '$location', function($http, $scope, $location) {
	
	$scope.pack = {};
		
	$scope.dismissForm = function() {
		$location.path("/templates/packs");
	};
	
	$scope.savePack = function() {
		$http.post("/api/templates/packs", $scope.pack).success(function(data) {
			$location.path("/templates/packs/" + data.id + "/edit");
		});
	};
}]);


BMApp.Templates.controller("TemplatePacksEditController", 
		['$http', '$scope', '$routeParams', '$location', '$upload', 
		 function($http, $scope, $routeParams, $location, $upload) {
	
	$scope.pack = {};
	$scope.tab  = "PACK";
	
	
	$scope.template = undefined;
	$scope.resource = undefined;
	$scope.widgets  = undefined;
	
	$scope.templateFiles = [];		// contains images marked for upload
	$scope.templateResources = [];	// contains js/css files for the template
	
	var		_tCMEditor;	// instance for template source code
	var 	_tHeadEditor; // instance for the HTML Header editor
	var		_wCMEditor; // instance for widget source code
	var		_rCMEditor;	// editor to edit resource files
	
	$http.get("/api/templates/packs/" + $routeParams.id).success(function(data) {
		$scope.pack = data;
		
		/*
		for (var i in $scope.pack.templates) {
			for (var k in $scope.pack.templates[i].resources)
				if ($scope.pack.templates[i].resources[k].mimetype == 'text/css')
					$scope.pack.templates[i].resources[k].iconClass = 'flaticon-css5';
		}
		*/
	});
	
	$scope.setupCodeMirror = function(_editor) {
		_editor.setOption("lineNumbers", true);
		_editor.setOption("mode", "htmlmixed");
		if ($scope.template && $scope.template.source)
			_editor.setOption("value", $scope.template.source);
		_tCMEditor = _editor;
	};
	
	$scope.setupHTMLHeadCodeMirror = function(_editor) {
		_editor.setOption("lineNumbers", true);
		_editor.setOption("mode", "htmlmixed");
		if ($scope.template && $scope.template.htmlHead)
			_editor.setOption("value", $scope.template.htmlHead);
		else
			_editor.setOption("value", "");
		_tHeadEditor = _editor;
	}
	
	$scope.setupCodeMirrorWidget = function(_editor) {
		_editor.setOption("lineNumbers", true);
		_editor.setOption("mode", "htmlmixed");
		if ($scope.widget && $scope.widget.source)
			_editor.setOption("value", $scope.widget.source);
		_wCMEditor = _editor;
	};
	
	$scope.setupResourceCodeMirror = function(_editor) {
		_editor.setOption("lineNumbers", true);
		_editor.setOption("mode", "htmlmixed");
		_rCMEditor = _editor;
	};
	
	$scope.switchTab = function(tab) {
		if (tab == 'SOURCE')
			window.setTimeout(function() { _tCMEditor.refresh(); }, 200);
		if (tab == 'HEAD')
			window.setTimeout(function() {_tHeadEditor.refresh(); }, 200);
	};
	
	$scope.dismissForm = function() {
		if ($scope.tab == 'TEMPLATES') {
			$scope.template = undefined;
		}
		
		if ($scope.tab == 'PACK')
			$location.path("/templates/packs");
		
		if ($scope.tab == 'WIDGETS') {
			$scope.widget = undefined;
		}
	};
	
	$scope.saveAction = function() {
		if ($scope.tab == "PACK") {
			$scope.updateTemplatePack();
		}
		
		if ($scope.tab == "TEMPLATES") {
			if ($scope.template.id)
				$scope.updateTemplate();
			else
				$scope.createTemplate();
		}
		
		if ($scope.tab == 'WIDGETS') {
			if ($scope.widget.id) 
				$scope.updateWidget();
			else
				$scope.createWidget();
		}
	};
	
	$scope.onThemeFileSelect = function($files) {
		BMApp.showSpinner();
		$scope.upload = $upload.upload({
			url		:	"/api/templates/packs/" + $routeParams.id + "/themeImage",
			method	:	"POST",
			file	:	$files[0]
		}).success(function(data) {
			$scope.pack.themeImage = data;
			BMApp.hideSpinner();
			BMApp.alert("Das Bild wurde erfolgreich gespeichert");
		});
	};
	
	$scope.onFileSelect = function($files) {
		for (var i in $files)
			$scope.templateFiles.push($files[i]);
	};

	$scope.onFileSelectResource = function($files) {
		for (var i in $files)
			$scope.templateResources.push($files[i]);
	};
	
	$scope.uploadTemplateImages = function() {
		for (var i in $scope.templateFiles) {
			BMApp.showSpinner();
			$scope.upload = $upload.upload({
				url		:	"/api/templates/templates/" + $scope.template.id + "/images",
				method	:	"POST",
				file	:	$scope.templateFiles[i]
			}).success(function(data) {
				$scope.template.images.push(data);
				BMApp.utils.remove("name", data.name, $scope.templateFiles);
				if ($scope.templateFiles.length == 0)
					BMApp.hideSpinner();
			});
		}
	};
	
	$scope.uploadTemplateResources = function() {
		for (var i in $scope.templateResources) {
			BMApp.showSpinner();
			$scope.upload = $upload.upload({
				url		:	"/api/templates/templates/" + $scope.template.id + "/resources",
				method	:	"POST",
				file	:	$scope.templateResources[i]
			}).success(function(data) {
				$scope.template.resources.push(data);
				BMApp.utils.remove("name", data.name, $scope.templateResources);
				if ($scope.templateResources.length == 0)
					BMApp.hideSpinner();
			});
		}
	};
	
	
	
	$scope.deleteTemplateImage = function(id) {
		BMApp.confirm("Soll das Bild wirklich aus dem Template entfernt werden?", function() {
			$http({
				method	:	"DELETE",
				url		:	"/api/templates/templates/" + $scope.template.id + "/images/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.template.images);
			});
		});
	};
	
	$scope.deleteTemplateResource = function(id) {
		BMApp.confirm("Soll die Datei wirklich aus dem Template entfernt werden?", function() {
			$http({
				method	:	"DELETE",
				url		:	"/api/templates/templates/" + $scope.template.id + "/resources/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.template.resources);
			});
		});
	};
	
	
	$scope.copyTemplate = function(id) {
		$http.put("/api/templates/packs/" + $routeParams.id + "/templates/" + id + "/clone").success(function(data) {
			$scope.pack.templates.push(data);
			BMApp.hideSpinner();
		}).error(function() {
			BMApp.hideSpinner();
		});
	};
	
	$scope.updateTemplatePack = function() {
		$http({
			method		:	"PATCH",
			url			:	"/api/templates/packs/" + $routeParams.id,
			data		:	$scope.pack
		}).success(function(data) {
			$scope.pack = data;
		});
	};
	
	
	$scope.focusTemplate = function(t) {
		$scope.template = t;
		if (_tCMEditor)
			_tCMEditor.setValue($scope.template.source);
		
		if (_tHeadEditor)
			_tHeadEditor.setValue($scope.template.htmlHead || "");
		$scope.widgets = undefined;
	};
	
	
	$scope.focusResource = function(id) {
		$http.get("/api/media/" + id + "/content").success(function(data) {
			$scope.resource = BMApp.utils.find('id', id, $scope.template.resources);
			if ($scope.resource.mimetype = 'text/css')
				_rCMEditor.setOption('mode', 'css');
			if ($scope.resource.mimetype = 'text/javascript')
				_rCMEditor.setOption('mode', 'javascript');
			_rCMEditor.setValue(data);
			window.setTimeout(function() { _rCMEditor.refresh(); }, 200);
		});
	};
	
	$scope.unfocusResource = function() {
		$scope.resource = undefined;
	};
	
	$scope.saveResource = function() {
		$http({
			url  	: "/api/media/" + $scope.resource.id + "/content",
			method	: "PATCH",
			data	:	_rCMEditor.getValue(),
			headers	:	{"Content-Type" : "text/plain"}
		}).success(function() {
			BMApp.alert("Erfolgreich gespeichert");
		});
	};
	
	
	$scope.createTemplate = function() {
		$scope.template.source = _tCMEditor.getValue();
		$http.post("/api/templates/packs/" + $routeParams.id + "/templates", $scope.template).success(function(data) {
			data.widgets = [];
			$scope.pack.templates.push(data);
			$scope.template = undefined;
		});
	};
	
	$scope.updateTemplate = function() {
		$scope.template.source = _tCMEditor.getValue();
		$scope.template.htmlHead = _tHeadEditor.getValue();
		
		$http({
			method 	:	"PATCH",
			url		:	"/api/templates/packs/" + $routeParams.id + "/templates/" + $scope.template.id,
			data	:	$scope.template,
		}).success(function(data) {
			var t = BMApp.utils.find("id", data.id, $scope.pack.templates);
			if (t) {
				t = data;
				$scope.template = undefined;
			}
		});
	};
	
	$scope.deleteTemplate = function(id) {
		BMApp.confirm("Soll das Template und seine Widgets wirklich entfernt werden?", function() {
			$http({
				method	:	"DELETE",
				url		:	"/api/templates/packs/" + $scope.pack.id + "/templates/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.pack.templates);
			});
		});
	};
	
	
	$scope.showWidgets = function(t) {
		$scope.template = t;
		$scope.widgets = t.widgets;
	};
	
	$scope.focusWidget = function(w) {
		$scope.widget = w;
		if (_wCMEditor)
			_wCMEditor.setValue($scope.widget.source);
	};
	
	$scope.createWidget = function() {
		$scope.widget.source = _wCMEditor.getValue();
		$http.post("/api/templates/templates/" + $scope.template.id + "/widgets", $scope.widget).success(function(data) {
			$scope.template.widgets.push(data);
			$scope.widget = undefined;
			BMApp.alert("Widget erfolgreich angelegt");
		});
	};
	
	$scope.updateWidget = function() {
		$scope.widget.source = _wCMEditor.getValue();
		$http({
			method		:	"PATCH",
			url			:	"/api/templates/templates/" + $scope.template.id + "/widgets/" + $scope.widget.id,
			data		:	$scope.widget
		}).success(function(data) {
			var w = BMApp.utils.find("id", data.id, $scope.template.widgets);
			w = data;
			$scope.widget = undefined;
			BMApp.alert("Widget erfolgreich gespeichert");
		});
	};
	
	$scope.deleteWidget = function(id) {
		BMApp.confirm("Soll das Widget wirklich entfernt werden?", function() {
			$http({
				method	:	"DELETE",
				url		:	"/api/templates/templates/" + $scope.template.id + "/widgets/" + id
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.template.widgets);
			});
		});
	}
	
}]);