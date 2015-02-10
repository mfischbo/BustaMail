BMApp.Editor.service('EditorFactory', ['$http', '$sce', function($http, $sce) {
	
	return {
		prepareScope : function(scope, routeParams, type) {
		
			scope.pageId = './frameedit.html?type=' + type + '&cid=' + routeParams.id;
			scope.tlink  = $sce.trustAsUrl(scope.pageId);
			
			scope.asTemplate = {
					visible : false,
					name	: '',
					editable: false
			};
			
			var dFrame = document.getElementById('documentFrame');
			window.addEventListener('message', function(e) {

		        if (e.data.type == 'elementSelected') {
		            scope.element = e.data.data;
		            scope.$apply();
		        }
		
		        if (e.data.type == 'elementUnselected') {
		            scope.element = undefined;
		            scope.$apply();
		        }
			});
			
			var path = '/api/mailings/';
			if (type == 'optinmail')
				path = '/api/optin/';
			
			$http.get(path + routeParams.id).success(function(data) {
				scope.document = data;
				$http.get('/api/templatepacks/' + scope.document.templatePack.id + '/templates/' + scope.document.templateId).success(function(template) {
					scope.document.template = template;
					
					if (template.editable) {
						scope.widgets = template.widgets;
						for (var i in scope.widgets) {
							if (scope.widgets[i].source.indexOf('bm-on-replace') > 0)
								scope.widgets[i].nestable = true;
						}
					} else {
						scope.widgets = [];
					}
				});
			});
			
			$http.get(path + routeParams.id + '/contents').success(function(data) {
				scope.contentVersions = data;
			});
			
			scope.appendWidget = function(id) {
				var w = BMApp.utils.find('id', id, scope.widgets);
				var m = { type : 'appendWidget', data : w };
				dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
			};

		    /**
		     * Posts a message to the iframe containing the widget to replace the current selected one
		     */
		    scope.replaceElement = function(id) {
		        var w = BMApp.utils.find("id", id, scope.widgets);
		        var m = { type : 'replaceWidget', data : w };
		        dFrame.contentWindow.postMessage(m , BMApp.uiConfig.uiURL);
		    };

		    /**
		     * Posts a message to the iframe in order to save the document
		     */
		    scope.saveContents = function() {
		        var m = { type : 'saveDocument'};
		        dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
		    };
    
		    /**
		     * Posts a message to the iframe in order to save the content as new template
		     */
		    scope.saveAsTemplate = function() {
		    	var m = { 
		    			type : 'saveAsTemplate', 
		    			data : {
		    					editable : scope.asTemplate.editable,
		    					name     : scope.asTemplate.name
		    	}};
		    	dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
		    };

		    /**
		     * Roll back to an older version of the contents
		     */
		    scope.rollBackTo = function(id) {
		        var m = {type : 'rollBackTo', data : id};
		        dFrame.contentWindow.postMessage(m, BMApp.uiConfig.uiURL);
		    };

		    /**
		     * Creates a preview
		     */
		    scope.createPreview = function() {
		        $http.put("/api/mailings/" + routeParams.id + "/preview").success(function() {
		            BMApp.alert('Das Preview wurde erfolgreich versendet');
		        });
		    };
		}
	}
}]);