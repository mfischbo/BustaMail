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
	
	var apiPath = '/api/landingpages/' + params.cid;
	
	if (params.type == 'static')
		apiPath += '/staticpages/' + params.cid;
	
	if (params.type == 'mailing')
        apiPath = '/api/mailings/' + params.cid;

	if (params.type == 'optinmail')
		apiPath = '/api/optin/' + params.cid;
	
	$http.get(apiPath).success(function(data) {
		$scope.initializeDocument(data);
	});
	
	/**
	 * Initializes the editor with the data from a given document 
	 */
	$scope.initializeDocument = function(data) {
		$scope.document = data;

		// fetch the full graph for the given template
		$http.get("/api/templatepacks/"+ $scope.document.templatePack.id +"/templates/" + $scope.document.templateId).success(function(template) {

			// fetch the current content
			$http.get(apiPath + '/contents/current').success(function(data) {
				$scope.html = $sce.trustAsHtml(data.content);
			
				$scope.widgets = template.widgets;
				for (var i in $scope.widgets)
					if ($scope.widgets[i].source.indexOf("bm-on-replace") > 0)
						$scope.widgets[i].nestable = true;
				
				// create the editor
				// bind editor after content is inserted into DOM
				nodeEdit = new BMNodeEdit($scope.document, data);
				window.setTimeout(function() {
					nodeEdit.setup();
					$scope.initMCE();
				}, 1000);
	
				
				// bind events from the outside world
				window.addEventListener("message", function(e) {
					e = e.data;
					if (e.type == 'appendWidget') {
						nodeEdit.appendWidget(e.data);
						window.setTimeout(function() {
							$scope.destroyMCE();
							$scope.initMCE();
						}, 1000);
					}
					
					if (e.type == 'replaceWidget')
						nodeEdit.replaceWidget(e.data);
					
					if (e.type == 'saveDocument') 
						$scope.saveContents();
					
					if (e.type == 'rollBackTo')
						$scope.rollBackTo(e.data);
					
					if (e.type == 'resourceChanged')
						$scope.handleResourceChanged(e.data);
					
					if (e.type == 'saveAsTemplate')
						$scope.saveAsTemplate(e.data);
					
				}, false);
			});
		});
	};
	

	$scope.initMCE = function() {
		console.log('initializing MCE...');
		tinyMCE.init({
			selector 			: '[contenteditable="true"]',
			inline				: true,
			menubar				: false,
			browser_spellcheck	: false,
			valid_styles		: { '*' : ''},
			forced_root_block	: 'div',
			toolbar				: 'undo redo | bold italic underline | link',
			plugins				: ['link'],
			link_list			: $scope.getMCELinkList()
		});
		console.log('Created instances : ' + tinyMCE.editors.length);
	};
	
	$scope.reinitMCE = function() {
		$scope.destroyMCE();
		$scope.initMCE();
	};

	
	$scope.destroyMCE = function() {
		for (id in tinyMCE.editors) {
			console.log("Removing editor with id : " + id);
			var e = tinyMCE.EditorManager.get(id);
			var elm = e.getElement();
			e.destroy();
			
			// note: For some reason the attribute is removed when calling destroy(). Add it again.
			elm.setAttribute("contenteditable", "true");
		}
	};

	/**
	 * Saves the editor contents.
	 * Copies the content of the editor node and removes all editor markers before saving it
	 */
	$scope.saveContents = function() {
		for (var id in tinyMCE.editors) {
			console.log("Removing editor for id " + id);
			tinyMCE.EditorManager.execCommand('mceRemoveControl', true, id);
		}
		
		var content = $("#editor-content").html();
		content = $scope.sanitizeContent(content);
		var d = {
			type : "HTML",
			content : content 
		};
		
		$http.post(apiPath + "/contents", d).success(function(data) {
			$scope.initMCE();
		});
	};
	
	/**
	 * Saves the current editor contents as new template
	 */
	$scope.saveAsTemplate = function(config) {
		
		// fetch the template pack
		$http.get("/api/templatepacks/" + $scope.document.templatePack.id).success(function(pack) {
			
			var origin = BMApp.utils.find('id', $scope.document.templateId, pack.templates);
			var template = {};
			angular.copy(origin, template);
			
			$scope.destroyMCE();
			var content = $('#editor-content').html();
			content = $scope.sanitizeContent(content);
			template.id = undefined;
			template.source = content;
			template.name   = config.name;
			template.editable = config.editable;
			pack.templates.push(template);
			$http.patch('/api/templatepacks/' + $scope.document.templatePack.id, pack).success(function() {
				BMApp.alert('Content saved as new template');
				$scope.initMCE();
			}).error(function() {
				BMApp.alert('Failed saving content as new template')
				$scope.initMCE();
			});
		});
	};

	
	$scope.sanitizeContent = function(html) {
		html = html.replace(/bm-fragment-hovered/g, "");
		html = html.replace(/bm-fragment-focused/g, "");
		html = html.replace(/bm-element-hovered/g, "");
		html = html.replace(/bm-element-focused/g, "");
		html = html.replace(/bm-text-focused/g, "");
		return html;
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
	
	$(document).on("_bmFragmentRemoving", function(event, element) {
		$scope.destroyMCE();
	});
	
	$(document).on("_bmFragmentRemoved", function(event, elmement) {
		$scope.initMCE();
	});
	
	
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
		jE.attr("src", "/api/files/" + image.id);
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