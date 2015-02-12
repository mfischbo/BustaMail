BMApp.Security = angular.module("SecurityModule", ['angularTreeview', 'ngRoute']);
BMApp.Security.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when("/security/orgUnits", {
			templateUrl :	"./modules/security/tmpl/orgUnits/index.html",
			controller	:	"SecurityOrgUnitIndexController"
		})
		.when("/security/orgUnits/create", {
			templateUrl :	"./modules/security/tmpl/orgUnits/create.html",
			controller	:	"SecurityOrgUnitCreateController"
		})
		.when("/security/users",   {
			templateUrl : "./modules/security/tmpl/users/index.html",
			controller	: "SecurityUserIndexController"
		})
		.when("/security/users/create", {
			templateUrl	:	"./modules/security/tmpl/users/create.html",
			controller	:	"SecurityUserCreateController"
		})
		.when("/security/users/:id/edit", {
			templateUrl :	"./modules/security/tmpl/users/edit.html",
			controller	:	"SecurityUserEditController"
		})
		.when("/security/settings", {
			templateUrl : "./modules/security/tmpl/settings/index.html",
			controller	:	"SecuritySetttingsIndexController"
		})
		.when("/logout", {
			templateUrl :   "./modules/security/tmpl/logout.html",
			controller	:	"BMAppLogoutController"
		});
}]);

BMApp.Security.controller("SecurityOrgUnitIndexController", ['$http', '$scope', function($http, $scope) {

	$scope.orgUnit = undefined;
	$scope.users   = {};
	$scope.tab     = 'BASE';
	
	$http.get("/api/security/orgUnits/roots").success(function(data) {
		$scope.rootUnits = data;
		if ($scope.rootUnits.length > 0)
			$scope.tree.currentNode = $scope.rootUnits[0];
	});
	
	
	$scope.$watch('tree.currentNode', function(val) {
		if (val) {
			$scope.orgUnit = val;
			$http.get("/api/security/orgUnits/" + $scope.orgUnit.id + "/users").success(function(data) {
				$scope.users = data;
			});
		}
	});
	
	
	$scope.saveOrgUnit = function() {
		$scope.orgUnit.selected = undefined;
		$scope.orgUnit.collapsed = undefined;
		$http({
			url : "/api/security/orgUnits/" + $scope.orgUnit.id,
			method:	"PATCH",
			data: 	$scope.orgUnit,
			headers : {"Content-Type" : "application/json"}
		}).success(function(data) {
			$scope.orgUnit = data;
		}).error(function() {
			console.log("Something");
		});
	};
	

}]);


BMApp.Security.controller("SecurityOrgUnitCreateController", ['$http', '$scope', '$location', function($http, $scope, $location) {
	
	$scope.orgUnit = {};
	$scope.rootUnits = [];
	
	$http.get("/api/security/orgUnits/roots").success(function(data) {
		$scope.rootUnits = data;
	});
	
	$scope.$watch('tree.currentNode', function(val) {
		$scope.parentUnit = val;
	});
	
	$scope.removeSelection = function() {
		$scope.parentUnit = undefined;
	};

	$scope.dismissForm = function() {
		$location.path("/security/orgUnits");
	};
	
	$scope.saveOrgUnit = function() {
		var url = "/api/security/orgUnits";
		if ($scope.parentUnit)
			url += "/" + $scope.parentUnit.id;
		
		$http.post(url, $scope.orgUnit).success(function(data) {
			$location.path("/security/orgUnits");
		});
	};
}]);


BMApp.Security.controller("SecurityUserIndexController", ['$http', '$scope', function($http, $scope) {
	
	$scope.users = {};
	$scope.searchTerm = "";

	var searchInt = undefined;
	
	$http.get("/api/security/users").success(function(data) {
		$scope.users = data;
	});

	$scope.searchUser = function() {
		if (searchInt)
			window.clearTimeout(searchInt);
		searchInt = window.setTimeout(function() {
			$http.get("/api/security/users?q=" + $scope.searchTerm).success(function(data) {
				$scope.users = data;
				window.clearTimeout(searchInt);
			});
		}, BMApp.uiConfig.searchDelay);
	};
	
	$scope.lockUser = function(id) {
		$http.put("/api/security/users/" + id + "/lock").success(function() {
			var user = BMApp.utils.find("id", id, $scope.users.content);
			user.locked = true;
		});
	};
	
	$scope.unlockUser = function(id) {
		$http({
			url		:	"/api/security/users/" + id + "/lock",
			method	:	"DELETE"
		}).success(function(data) {
			var user = BMApp.utils.find("id", id, $scope.users.content);
			user.locked = false;
		});
	};
	
	$scope.deleteUser = function(id) {
		BMApp.confirm("Soll der Benutzer wirklich entfernt werden?", function() {
			$http({
				url		:	"/api/security/users/" + id,
				method	:	"DELETE"
			}).success(function() {
				BMApp.utils.remove("id", id, $scope.users.content);
				BMApp.alert("Der Benutzer wurde erfolgreich entfernt")
			});
		});
	};
}]);


BMApp.Security.controller("SecurityUserCreateController", ['$http', '$scope', '$location', function($http, $scope, $location) {
	
	$scope.user = {};
	$scope.owner = undefined;
	
	$scope.saveUser = function() {
		$http.post("/api/security/orgUnits/"+ $scope.owner +"/users", $scope.user).success(function(data) {
			BMApp.alert("Der Benutzer wurde angelegt");
			$location.path("/security/users");
		});
	};
	
	$scope.dismissForm = function() {
		$scope.user = {};
		$location.path("/security/users");
	};
}]);


/**
 * Controller for edit the users details and assignment to orgUnits
 */
BMApp.Security.controller("SecurityUserEditController", 
		['$http', '$scope', '$routeParams', '$location', 
        function($http, $scope, $routeParams, $location) {

	$scope.user = {};
	$scope.orgUnit = undefined;
	$scope.childList = [];
	
	$scope.permissions = undefined;
	
	// load the current user
	$http.get("/api/security/users/" + $routeParams.id).success(function(data) {
		$scope.user = data;
	});

	// load the orgUnit tree
	$http.get("/api/security/orgUnits/roots").success(function(data) {
		$scope.rootUnits = data;
	});
	
	// load the system permissions
	$http.get("/api/security/permissions").success(function(data) {
		$scope.permissions = data;
		$scope.permissionGroups = BMApp.utils.unique("group", $scope.permissions);
	});
	
	$scope.$watch('tree.currentNode', function(val) {
		if (!val) return;
		$scope.orgUnit = val;
		$scope.childList = BMApp.utils.flattenTree("children", $scope.orgUnit);
	});
	
	$scope.dismissForm = function() {
		$scope.user = {};
		$location.path("/security/users");
	};
	
	/**
	 * Adds a user to the given org unit
	 */
	$scope.toggleOrgUnit = function() {
		var idx = BMApp.utils.indexOf("userId", $scope.user.id, $scope.orgUnit.actors);
		if (idx == -1)
			$scope.orgUnit.actors.push({userId : $scope.user.id, permissions : [], addToChildren : false, addToFutureChildren : false});
		else
			BMApp.utils.remove("userId", $scope.user.id, $scope.orgUnit.actors);
		
		console.log($scope.orgUnit);
	};

	/**
	 * Returns wheter the user is in the given org unit or not
	 */
	$scope.isInOrgUnit = function() {
		if (!$scope.orgUnit) return;
		return (BMApp.utils.indexOf("userId", $scope.user.id, $scope.orgUnit.actors) > -1);
	};
	
	/**
	 * Toggles the user to be in all children and sub children of the given org unit
	 */
	$scope.toggleAddToChildren = function() {
		if (!$scope.orgUnit) return;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor) {
			if (!actor.addToChildren) {
				actor.addToChildren = true;
				for (var i in $scope.childList)
					$scope.childList[i].actors.push({userId : $scope.user.id, permissions: [], addToChildren : true, addToFutureChildren : false});
			} else {
				actor.addToChildren = false;
				for (var i in $scope.childList)
					BMApp.utils.remove("userId", $scope.user.id, $scope.childList[i].actors);
			}
		}
		console.log($scope.orgUnit);
	};
	
	/**
	 * Returns wheter the user is in the all children of the given org unit or not
	 */
	$scope.isAddedToChildren = function() {
		if (!$scope.orgUnit) return false;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor)
			return actor.addToChildren;
		else return false;
	};
	
	
	/**
	 * Sets the flag that the user will be added to all future org units
	 */
	$scope.toggleAddToFutureChildren = function() {
		if (!$scope.orgUnit) return false;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor) {
			if (!actor.addToFutureChildren) {
				actor.addToFutureChildren = true;
				for (var i in $scope.childList) {
					var idx = BMApp.utils.indexOf("userId", $scope.user.id, $scope.childList[i].actors);
					if (idx > -1)
						$scope.childList[i].actors[idx].addToFutureChildren = true;
				}
			} else {
				actor.addToFutureChildren = false;
				for (var i in $scope.childList) {
					var idx = BMApp.utils.indexOf("userId", $scope.user.id, $scope.childList[i].actors);
					if (idx > -1)
						$scope.childList[i].actors[idx].addToFutureChildren = false;
				}
			}
		}
	};
	
	/**
	 * Returns if the user will be added to all future child org units
	 */
	$scope.isAddedToFutureChildren = function() {
		if (!$scope.orgUnit) return false;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor)
			return actor.addToFutureChildren;
		else return false;
	};

	$scope.getPermissionsByGroup = function(group) {
		return BMApp.utils.extract("group", group, $scope.permissions);
	};

	
	$scope.togglePermission = function(p) {
		if (!$scope.orgUnit) return;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor) {
			if (actor.permissions.indexOf(p) < 0) {
				actor.permissions.push(p);
				for (var i in $scope.childList) {
					var t = BMApp.utils.find("userId", $scope.user.id, $scope.childList[i].actors);
					if (t && t.permissions.indexOf(p) < 0)
						t.permissions.push(p);
				}
			} else {
				actor.permissions.splice(actor.permissions.indexOf(p), 1);
				for (var i in $scope.childList) {
					var t = BMApp.utils.find("userId", $scope.user.id, $scope.childList[i].actors);
					if (t && t.permissions.indexOf(p) > -1)
						t.permissions.splice(t.permissions.indexOf(p), 1);
				}
			}
		}
		console.log($scope.orgUnit);
	};
	
	$scope.hasPermission = function(p) {
		if (!$scope.orgUnit) return false;
		var actor = BMApp.utils.find("userId", $scope.user.id, $scope.orgUnit.actors);
		if (actor) 
			return (actor.permissions.indexOf(p) > -1);
	};
	
	
	$scope.save = function() {
		if ($scope.tab == 'USER') return $scope.saveUser();
		if ($scope.tab == 'GROUPS') return $scope.saveActors();
	};
	
	
	/**
	 * Saves the user
	 */
	$scope.saveUser = function() {
		$http({
			url		:	"/api/security/users/" + $routeParams.id,
			method	:	"PATCH",
			data	:	$scope.user,
		}).success(function(data) {
			$scope.user = data;
			BMApp.alert("Der Benutzer wurde erfolgreich bearbeitet");
		});
	};
	
	
	$scope.saveActors = function() {
		// collect all actors for the given user
		var actors = new Array();
		for (var i in $scope.rootUnits) {
			
			// check if user is actor in this root unit
			var rootActor = BMApp.utils.extract("userId", $scope.user.id, $scope.rootUnits[i].actors);
			if (rootActor.length > 0) {
				rootActor[0].orgUnitId = $scope.rootUnits[i].id;
				actors.push(rootActor[0]);
			}
		
			// add all actors from children of this root unit
			var children = BMApp.utils.flattenTree("children", $scope.rootUnits[i]);
			for (var k in children) {
				var inner = BMApp.utils.extract("userId", $scope.user.id, children[k].actors);
				for (var t in inner) {
					inner[t].orgUnitId = children[k].id;
					actors.push(inner[t]);
				}
			}
		}
	
		$http.post("/api/security/users/" + $routeParams.id + "/actors", actors).success(function(data) {
			BMApp.alert("Die Rechte wurden erfolgreich gespeichert");
			console.log(data);
		});
	};
}]);


BMApp.Security.directive('ouSelector', function($http) {
	
	return {
		restrict:	"A",
		scope : {
			'selectAs': '=selectAs',
		},
		template: '<div class="input-group">'
			+ '<select data-ng-model="selectAs" data-ng-options="u.id as u.name for u in units" class="form-control input-sm">'
			+ '<option value="" disabled>Organisationseinheiten</option>'
			+ '</select>'
			+ '</div>',
		link:	function(scope, element, attrs) {
			$http.get("/api/security/users/orgUnits").success(function(data) {
				scope.units = data;
				// check local storage for pre selection
				if (localStorage.getItem("__BM_SelectedOrgUnit") != 'undefined') {
					var selection = localStorage.getItem("__BM_SelectedOrgUnit");
					if (BMApp.utils.find("id", selection, scope.units)) {
						scope.selectAs = selection;
					}
				}
			});
			
			scope.$watch('selectAs', function(val) {
				if (!val) return;
				localStorage.setItem("__BM_SelectedOrgUnit", val);
			});
		}	
	};
});


