var BMApp = angular.module("BMApp", 
		['ngRoute', 'SecurityModule', 'SubscriberModule', 
		 'BMEditorModule', 'TemplatesModule', 'MailingModule', 'MailingListModule', 'MediaModule']);

BMApp.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
	
	$httpProvider.interceptors.push(function($q) {
		return {
			'request' : function(config) {
				if (config.method == "PATCH")
					config.headers['Content-Type'] = "application/json";
				
				return config;
			},
			
			'requestError' : function(rejection) {
				return $q.reject(rejection);
			},
			
			'response'		: function(response) {
				return response;
			},
			
			'responseError'	: function(rejection) {
				if (rejection.status == 401 || rejection.status == 503)
					window.location.href = "./login";
				
				if (rejection.status == 405 || rejection.status == 415)
					BMApp.alert("Leider ist ein Fehler aufgetreten", 'error');
				
				if (rejection.status == 403) {
					var m = rejection.config.method;
					if (m == "POST" || m == "PATCH" || m == "PUT" || m == "DELETE")
						BMApp.alert("Sie haben nicht die notwendigen Rechte um diese Aktion durchzuf&uuml;hren", 'error');
				}
				
				if (rejection.status == 500) {
					BMApp.alert("Ooops! Da ist leider etwas schief gelaufen", 'error');
				}
				
				// disable spinner
				BMApp.hideSpinner();
				return $q.reject(rejection);
			}
		}
	});
	
	$routeProvider
		// security module
		.when("/security/orgUnits", {
			templateUrl :	"./js/modules/security/tmpl/orgUnits/index.html",
			controller	:	"SecurityOrgUnitIndexController"
		})
		.when("/security/orgUnits/create", {
			templateUrl :	"./js/modules/security/tmpl/orgUnits/create.html",
			controller	:	"SecurityOrgUnitCreateController"
		})
		.when("/security/users",   {
			templateUrl : "./js/modules/security/tmpl/users/index.html",
			controller	: "SecurityUserIndexController"
		})
		.when("/security/users/create", {
			templateUrl	:	"./js/modules/security/tmpl/users/create.html",
			controller	:	"SecurityUserCreateController"
		})
		.when("/security/users/:id/edit", {
			templateUrl :	"./js/modules/security/tmpl/users/edit.html",
			controller	:	"SecurityUserEditController"
		})
		.when("/security/settings", {
			templateUrl : "./js/modules/security/tmpl/settings/index.html",
			controller	:	"SecuritySetttingsIndexController"
		})
	
		// subscriber module
		.when("/subscriber", {
			templateUrl : "./js/modules/subscriber/tmpl/index.html",
			controller	: "SubscriberIndexController"
		})
		
		// templates module
		.when("/templates/packs", {
			templateUrl	:	"./js/modules/templates/tmpl/packs/index.html",
			controller	:	"TemplatePacksIndexController"
		})
		.when("/templates/packs/create", {
			templateUrl :	"./js/modules/templates/tmpl/packs/create.html",
			controller  :	"TemplatePacksCreateController"
		})
		.when("/templates/packs/:id/edit", {
			templateUrl	:	"./js/modules/templates/tmpl/packs/edit.html",
			controller	:	"TemplatePacksEditController"
		})
	
		// Mailing Module
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
		
		// Media Module
		.when("/media", {
			templateUrl	:	"./js/modules/media/tmpl/index.html",
			controller	:	"MediaIndexController"
		})
		
		// editor module
		.when("/editor", {
			templateUrl	:	"./js/modules/editor/tmpl/editor.html",
			controller	:	"EditorIndexController"
		})
		
		// Mailing List Module
		.when("/subscription-lists", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/index.html",
			controller	:	"MailingListIndexController"
		})
		.when("/subscription-lists/create", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/create.html",
			controller	:	"MailingListCreateController"
		})
		.when("/subscription-lists/:id", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/details.html",
			controller	:	"MailingListEditController"
		})
		.when("/subscription-lists/:id/edit", {
			templateUrl	:	"./js/modules/mailinglist/tmpl/subscriptionlist/edit.html",
			controller	:	"MailingListEditController"
		})
		
		// Other stuff
		.when("/logout", {
			templateUrl :   "./js/modules/security/tmpl/logout.html",
			controller	:	"BMAppLogoutController"
		})
		.otherwise({
			redirectTo 	: "/subscriber"
		});
}]);


BMApp.controller("BMAppLogoutController", ['$scope', '$http', function($scope, $http) {
	
	$http({
		url		:	"/api/security/authentication",
		method	:	"DELETE"
	}).success(function() {
		window.location.href = "./login";
	});
}]);


BMApp.uiConfig = {
		searchDelay : 600,
		codeMirrorOpts : {
			lineNumbers : true,
			mode		: "htmlmixed"
		}
};


BMApp.showSpinner = function() {
	$("#spinner").show();
}

BMApp.hideSpinner = function() {
	$("#spinner").hide();
}


BMApp.confirm = function(txt, callback) {
	var retval = window.confirm(txt);
	if (retval)
		callback();
};

BMApp.alert = function(message, status) {
	if (!status)
		status = 'success';
	
	var p = $("#alert-panel");
	var t = $("#alert-panel .alert");
	if (status == 'success')
		t = t.addClass("alert-success")
	if (status == 'error')
		t = t.addClass("alert-danger");
	t.html(message);
	p.show();
	window.setTimeout(function() {
		p.fadeOut(800, function() {
			t.removeClass("alert-success");
			t.removeClass("alert-danger");
		});
	}, 3000);
};

BMApp.cssUtils = {
		
		rgb2Hex : function(rgb) {
			if (rgb.indexOf("rgb") > -1) {
				rgb = rgb.match(/^rgb\((\d+),\s*(\d+),\s*(\d+)\)$/);
			    function hex(x) {
			        return ("0" + parseInt(x).toString(16)).slice(-2);
			    }
			    return "#" + hex(rgb[1]) + hex(rgb[2]) + hex(rgb[3]);
			}
		}
}

BMApp.utils = { 
		indexOf : function(property, value, array) {
			if (!array) {
				array = value;
				value = property;
				property = "id";
			}
			for (var i in array)
				if (array[i][property] == value)
					return i;
			return -1;
		},
	
		/**
		 * Sets each property in the given array to the specified value
		 * @param property The property to be set
		 * @param value The value to set the property to
		 * @param array The array
		 */
		set    : function(property, value, array) {
			if (array) {
				for (var i in array)
					array[i][property] = value;
			}
		},
		
		remove : function(property, value, array) {
			for (var i in array) {
				if (array[i][property] == value)
					array.splice(i,1);
			}
		},
		
		find:	function(property, value, array) {
			for (var i in array)
				if (array[i][property] == value)
					return array[i];
			return undefined;
		},
		
		extract: function(property, value, array) {
			var retval = new Array();
			for (var i in array)
				if (array[i][property] == value)
					retval.push(array[i]);
			return retval;
		},
		
		filter: function(property, cb, array) {
			var retval = new Array();
			for (var i in array)
				if (array[i][property] && cb(array[i][property]) == true)
					retval.push(array[i]);
			return retval;
		},
		
		unique: function(property, array) {
			var retval = new Array();
			for (var i in array) {
				if (retval.indexOf(array[i][property]) < 0)
					retval.push(array[i][property]);
			}
			return retval;
		},
		
		flattenTree: function(byProperty, root, list) {
			if (!list)
				list = new Array();
			
			if (root[byProperty].length == 0)
				return list;
			else {
				for (var i in root[byProperty]) {
					list.push(root[byProperty][i]);
					return BMApp.utils.flattenTree(byProperty, root[byProperty][i], list);
				}
			}
			return list;
		},
		
		UUID : function() {
		    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		        var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
		        return v.toString(16);
		    });
		},
		
		/**
		 * Converts a string value into a more native value like boolean or number
		 * @param value The value to convert
		 */
		convertString : function(value) {
			if (value == 'true') return true;
			if (value == 'false') return false;
			
			var q = new Number(value);
			if (q != Number.NaN) return q;
		}
};