BMApp.Editor = angular.module("BMEditorModule", ['ui.bootstrap', 'MediaModule']);

BMApp.Editor.service("HyperlinkService", [function() {

	return {
		newHyperlink 	: function(defaults) {
			return {
				id				:	BMApp.utils.UUID(),
				href			:	'',
				textContent		:	'',
				enableTracking	:	BMApp.utils.convertString(defaults.linkTrackingEnabled) || false,
				textUnderline	:	BMApp.utils.convertString(defaults.linksUnderlined) || false,
				color			:	defaults.defaultLinkColor || "#ff0000",
				isWorking		:	false
			}
		},
	
		toString		: function(link) {
			var a = $.parseHTML("<a>" + link.textContent + "</a>");
			a = $(a[0]);
			a.attr("data-link-id", link.id);
			a.attr("href", link.href);
			if (!link.enableTracking)
				a.addClass("bm-disable-tracking");
			if (link.textUnderline)
				a.css("text-decoration", "underline");
			else
				a.css("text-decoration", "none");
			if (link.color)
				a.css("color", link.color);
			
			// flag as managed link
			a.addClass("bm-hyperlink");
			
			var retval = $("<div>").append(a).clone().html();
			return retval;
		},
		
		parseNode		: function(node) {
			var retval = {};
			
			// uuid of the link. if none found create a new one
			if (node.attr("data-link-id"))
				retval.id = node.attr("data-link-id");
			else {
				node.attr("data-link-id", BMApp.utils.UUID());
				retval.id = node.attr("data-link-id");
			}
			
			// href
			if (node.attr("href").length > 0)
				retval.href = node.attr("href");
			
			if (node.text().length > 0)
				retval.textContent = node.text();
			
			if (!node.hasClass("bm-disable-tracking"))
				retval.enableTracking = true;
			
			// check if the current link color
			if (node.css("color")) {
				if (node.css("color").indexOf("rgb") > -1)
					retval.color = BMApp.cssUtils.rgb2Hex(node.css("color"));
				else
					retval.color = "#" + node.css("color");
			}
			if (node.css("text-decoration")) {
				if (node.css("text-decoration").indexOf("underline") > -1)
					retval.textUnderline = true;
			}
			return retval;
		}
	};
}]);

BMApp.Editor.controller("EditorIndexController", 
		['$scope', '$http', '$routeParams', '$sce', 'HyperlinkService',
          function($scope, $http, $routeParams, $sce, HyperlinkService) {


	// The Mailing in question + initialization
	$scope.mailing = undefined;
	
	// HTML content of the mailing
	$scope.html    = undefined;
	
	// the tinymce instance running
	var mce = undefined;
	
	$scope.element = undefined;
	$scope.textSelection = undefined;

	$scope.link = {};
	$scope.hyperlinks = [];
	
	// the underlaying jQuery element when an element is marked as selected
	var jE		 = undefined;
	
	// the iframe element
	var dFrame = $("#documentFrame");
	
	// The actual editor
	var nodeEdit   = undefined;
	
	// initialize the mailing and the widgets
	var id = window.location.href.split('=')[1];
	
	$http.get("/api/landingpages/" + id).success(function(data) {
		$scope.initializeDocument(data);
	});
	
	/**
	 * Initializes the editor with the data from a given mailing
	 */
	$scope.initializeDocument = function(data) {
		$scope.mailing = data;
		$scope.html = $sce.trustAsHtml($scope.mailing.htmlContent.content);

		// fetch the full graph for the given template
		$http.get("/api/templates/templates/" + $scope.mailing.template.id).success(function(data) {

			// create the editor
			// bind editor after content is inserted into DOM
			nodeEdit = new BMNodeEdit(data);
			window.setTimeout(function() {
				nodeEdit.setup();
				// find all managed hyperlinks in the document
				$("a.bm-hyperlink").each(function() {
					$scope.hyperlinks.push(HyperlinkService.parseNode($(this)));
				});
			}, 1000);
	
			// bind events from the outside world
			window.addEventListener("message", function(e) {
				e = e.data;
				if (e.type == 'appendWidget')
					nodeEdit.appendWidget(e.data);
				
				if (e.type == 'replaceWidget')
					nodeEdit.replaceWidget(e.data);
				
				if (e.type == 'saveDocument') 
					$scope.saveContents();
				
				if (e.type == 'rollbackTo')
					$scope.rollbackTo(e.data);
				
			}, false);
		});
		

	};


	/**
	 * Saves the editor contents.
	 * Copies the content of the editor node and removes all editor markers before saving it
	 */
	$scope.saveContents = function() {
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
		
		$http.post("/api/landingpages/" + $scope.mailing.id + "/content", d).success(function(data) {
			BMApp.alert("Inhalt erfolgreich gespeichert", 'success');
		});
	};
	
	/**
	 * Loads the versioned content with the specified id and sets the editor contents to the returned value
	 * This will reinitialize the editor.
	 */
	$scope.rollBackTo = function(contentId) {
		$http.get("/api/mailings/" + $scope.mailing.id + "/content/" + contentId).success(function(data) {
			// reinit the editor
			$scope.html = $sce.trustAsHtml(data.content);
			nodeEdit.destroy();
			nodeEdit = new BMNodeEdit($scope.mailing.template);
			BMApp.showSpinner();
			window.setTimeout(function() {
				nodeEdit.setup();
			
				// find all managed hyperlinks in the document
				$("a.bm-hyperlink").each(function() {
					$scope.hyperlinks.push(HyperlinkService.parseNode($(this)));
				});
	
				BMApp.hideSpinner();
				BMApp.alert("Version wiederhergestellt");
			}, 1000);
		});
	};
	
	/**
	 * Sends a preview of this mailing
	 */
	$scope.sendPreview = function() {
		/*
		$http.put("/api/mailings/" + $scope.mailing.id + "/preview").success(function(data) {
			BMApp.alert("Preview versendet", 'success');
		});
		*/
		// TODO: Requires reimplementation for landing pages
	};
	

	$(document).on("_bmElementSelected", function(event, element) {
		jE = $(element);
		$scope.element = $scope.metaDataFor(element);
		$scope.$apply();
		if (element.tagName == "IMG")
			$scope.$broadcast("setCurrentImage", element);
	});
	
	$(document).on("_bmElementUnselected", function(e) {
		jE = undefined;
		$scope.element = undefined;
		$scope.$apply();
	});
	
	$(document).on("_bmTextSelection", function(e, s) {
		if (mce) 
			mce.remove();
		// setup tinymce
		mce = tinymce.init({
			selector : '.bm-fragment',
			inline   : true,
			toolbar	 :	'fullscreen | undo redo | styleselect | bold italic underlined',
			plugins  :  'fullscreen'
		});
	});

	$(document).on("_bmNoTextSelection", function() {
		$scope.textSelection = undefined;
		$scope.link = undefined;
		$scope.$apply();
	});

	$scope.replaceVar = function() {
		var txt = '${' + $scope.textVar + '}';
		$scope.textSelection.text(txt);
	};

	
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