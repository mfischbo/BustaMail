BMApp.Editor = angular.module("BMEditorModule", ['ui.bootstrap', 'MediaModule']);

BMApp.Editor.controller("EditorIndexController", 
		['$scope', '$http', '$routeParams', '$sce',
          function($scope, $http, $routeParams, $sce) {


	// The Mailing in question + initialization
	$scope.document = undefined;
	
	// HTML content of the document 
	$scope.html    = undefined;
	
	// the tinymce instance running
	var mce = undefined;

	var jE = undefined;
	
	// the iframe element
	var dFrame = $("#documentFrame");
	
	// The actual editor
	var nodeEdit   = undefined;
	
	// initialize the document and the widgets
	var params = BMApp.utils.parseHTTPParams(window.location.href);
	var id = params.id;
	
	var apiPath = '/api/landingpages/' + params.lpid;
	if (params.sid) 
		apiPath += '/staticpages/' + params.sid;
	
	$http.get(apiPath).success(function(data) {
		$scope.initializeDocument(data);
	});
	
	/**
	 * Initializes the editor with the data from a given document 
	 */
	$scope.initializeDocument = function(data) {
		$scope.document = data;
		$scope.html = $sce.trustAsHtml($scope.document.htmlContent.content);

		// fetch the full graph for the given template
		$http.get("/api/templates/templates/" + $scope.document.template.id).success(function(data) {

			$scope.widgets = data.widgets;
			for (var i in $scope.widgets)
				if ($scope.widgets[i].source.indexOf("bm-on-replace") > 0)
					$scope.widgets[i].nestable = true;
			
			// create the editor
			// bind editor after content is inserted into DOM
			nodeEdit = new BMNodeEdit($scope.document, data);
			window.setTimeout(function() {
				nodeEdit.setup();
				$scope.reinitMCE(); 
			}, 1000);

			
			// bind events from the outside world
			window.addEventListener("message", function(e) {
				e = e.data;
				if (e.type == 'appendWidget') {
					nodeEdit.appendWidget(e.data);
					window.setTimeout(function() {
						$scope.reinitMCE();
					}, 200);
				}
				
				if (e.type == 'replaceWidget')
					nodeEdit.replaceWidget(e.data);
				
				if (e.type == 'saveDocument') 
					$scope.saveContents();
				
				if (e.type == 'rollBackTo')
					$scope.rollBackTo(e.data);
				
				if (e.type == 'resourceChanged')
					$scope.handleResourceChanged(e.data);
				
			}, false);
		});
	};
	

	$scope.reinitMCE = function() {
		for (id in tinymce.editors) {
			console.log("Removing editor with id : " + id);
			tinymce.editors[id].remove();
		}
	
		console.log("Setting up new editors");
		// setup tinymce
		tinymce.init({
			//selector : '[contenteditable="true"]',
			selector : ".bm-fragment",
			inline   : true,
			browser_spellcheck : false,
			toolbar	 :	'undo redo | styleselect | bold italic underline',
			plugins  :  ['link'],
			link_list : $scope.getMCELinkList()
		});	
		
		console.log("Created " + tinymce.editors.length + " new instances");
	};


	/**
	 * Saves the editor contents.
	 * Copies the content of the editor node and removes all editor markers before saving it
	 */
	$scope.saveContents = function() {
		for (id in tinymce.editors) {
			tinymce.editors[id].remove();
		}
		
		
		var content = $("#editor-content").html();
		content = content.replace(/bm-fragment-hovered/g, "");
		content = content.replace(/bm-fragment-focused/g, "");
		content = content.replace(/bm-element-hovered/g, "");
		content = content.replace(/bm-element-focused/g, "");
		content = content.replace(/bm-text-focused/g, "");
		var d = {
			type : "HTML",
			content : content 
		};
		
		$http.post(apiPath + "/content", d).success(function(data) {
			$scope.reinitMCE();
		});
	};
	
	/**
	 * Loads the versioned content with the specified id and sets the editor contents to the returned value
	 * This will reinitialize the editor.
	 */
	$scope.rollBackTo = function(contentId) {
		$http.get(apiPath + "/content/" + contentId).success(function(data) {
			
			for (var i in tinymce.editors)
				tinymce.editors[i].remove();
			
			// reinit the editor
			$scope.html = $sce.trustAsHtml(data.content);
			nodeEdit.destroy();
			
			nodeEdit = new BMNodeEdit($scope.document, $scope.document.template);
			window.setTimeout(function() {
				nodeEdit.setup();
				$scope.reinitMCE();
			}, 1000);
		});
	};
	

	$scope.handleResourceChanged = function(data) {
		nodeEdit.reloadCSSFile(data);
	};
	
	
	$(document).on("_bmElementSelected", function(event, element) {
		jE = $(element);
		$scope.element = $scope.metaDataFor(element);
		$scope.$apply();
		console.log("Selected an element");
		console.log($scope.element);
		
		if (element.tagName == "IMG")
			$scope.$broadcast("setCurrentImage", element);
		
		// broadcast that up to the content frame window
		var m = {type : 'elementSelected', data : $scope.element};
		window.parent.postMessage(m, BMApp.uiConfig.uiURL);
	});
	
	$(document).on("_bmElementUnselected", function(e) {
		jE = undefined;
		$scope.element = undefined;
		$scope.$apply();
		
		// broadcast that up to the content frame window
		var m = {type : 'elementUnselected'};
		window.parent.postMessage(m, BMApp.uiConfig.uiURL);
	});
	
	$(document).on("_bmTextSelection", function(e, s) {
		console.log("Text selection occured");
	});
	
	$scope.getMCELinkList = function() {
		var retval = [];
		for (var i in $scope.document.staticPages) {
			var p = $scope.document.staticPages[i];
			retval.push({title : p.name, value : p.id})
		}
		return retval;
	};

	$(document).on("_bmNoTextSelection", function() {
		$scope.textSelection = undefined;
		$scope.link = undefined;
		$scope.$apply();
	});


	
	$scope.applySettings = function() {
		for (var i in $scope.element) {
			jE.attr(i, $scope.element[i]);
		}
	};
	
	$scope.$on("_bmSetImageRequest", function(e, image) {
		jE.attr("src", "./img/media/" + image.id);
	});
	
	$scope.metaDataFor = function(element) {
		var t = {};
		if (element.tagName == "TR") {
			t.bgColor = jE.attr("bgColor");
		}
		
		if (element.tagName == "TD") {
			t.valign = jE.attr("valign") || "center";
			t.align  = jE.attr("align");
			t.width  = jE.attr("width");
		}
		
		if (element.tagName == "IMG") {
			t.width = element.offsetWidth;
			t.height= element.offsetHeight;
		}
		return t;
	};
}]);