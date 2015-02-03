BMApp.Media = angular.module("MediaModule", ['angularTreeview', 'angularFileUpload']);
BMApp.Media.config(['$routeProvider', function($routeProvider) {

	$routeProvider
		.when("/media", {
			templateUrl	:	"./modules/media/tmpl/index.html",
			controller	:	"MediaIndexController"
		})
		
		// editor module
		.when("/editor", {
			templateUrl	:	"./modules/editor/tmpl/editor.html",
			controller	:	"EditorIndexController"
		});
}]);

BMApp.Media.controller("MediaIndexController", ['$scope', '$http', '$upload', function($scope, $http, $upload) {

	$scope.roots = undefined;
	$scope.cwd   = undefined;
	$scope.directory = {};
	$scope.uFiles = [];
	
	$scope.viewMode = "TABLE";
	
	$http.get("/api/media/directory").success(function(data) {
		$scope.roots = data;
		if (data.length > 0)
			$scope.cwd = $scope.roots[0];
	});
	
	$scope.$watch('tree.currentNode', function(val) {
		if (!val) return;
		$scope.cwd = val;
		$http.get("/api/media/directory/" + val.id).success(function(data) {
			$scope.cwd.files = data;
		});
		
		$http.get("/api/media/directory/" + val.id + "/children").success(function(data) {
			$scope.tree.currentNode.children = data;
		});
	});
	
	
	$scope.onFilesSelected = function($files) {
		for (var i in $files)
			$scope.uFiles.push($files[i]);
	};
	
	$scope.uploadFiles = function() {
		BMApp.showSpinner();
		for (var i in $scope.uFiles) {
			$scope.upload = $upload.upload({
				method 	: 	"POST",
				url		:	"/api/media/directory/" + $scope.cwd.id,
				file	:	$scope.uFiles[i]
			}).success(function(data) {
				if (!$scope.cwd.files)
					$scope.cwd.files = [];
				$scope.cwd.files.push(data);
				BMApp.utils.remove("name", data.name, $scope.uFiles);
				if ($scope.uFiles.length == 0)
					BMApp.hideSpinner();
			});
		}
	};
	
	$scope.createDirectory = function() {
		$http.put("/api/media/directory/" + $scope.cwd.id, $scope.directory).success(function(data) {
			$scope.cwd.children.push(data);
		});
	};
	
	$scope.deleteFile = function(file) {
		$http({
			method 	: 'DELETE',
			url		: '/api/media/directory/' + $scope.cwd.id + '/' + file.id
		}).success(function() {
			BMApp.utils.remove('id', file.id, $scope.cwd.files);
		});
	};
}]);


BMApp.Media.controller("ImagePickerModalController", 
		['$scope', '$http', function($scope, $http) {
	$scope.roots = undefined;
	$scope.cwd	 = undefined;
	$scope.images = undefined;
	
	$http.get("/api/media/directory").success(function(data) {
		$scope.roots = data;
		if (data.length > 0)
			$scope.cwd = $scope.roots[0];
	});

	$scope.$on("treeviewNodeSelected", function(e, node) {
		if (!node) return;
		$scope.cwd = node;
		$http.get("/api/media/directory/" + node.id).success(function(data) {
			$scope.cwd.files = data;
			$scope.images = BMApp.utils.filter('mimetype', function(t) {
				return (t.indexOf("image/") > -1);
			}, $scope.cwd.files);
			
			angular.forEach($scope.images, function(img) {
				// set property if size is adequate for current selection
				img.isAdequateSize = (img.width >= $scope.current.width);
				
				for (var i in img.variants)
					if (img.variants[i].width == 64) {
						img.thumbnail = "./img/media/" + img.variants[i].id;
						return;
					}
			});
		});
		
		$http.get('/api/media/directory/' + node.id + '/children').success(function(data) {
			node.children = data;
		});
	});
	
	$scope.$on("setCurrentImage", function(e, image) {
		$scope.current = image;
	});
	
	$scope.pickImage = function(i) {
		// select the best fitting variant
		var target = parseInt($scope.$parent.element.width);
		var m = i.width;
		for (var t in i.variants) {
			if (i.variants[t].width < m && i.variants[t].width >= target) {
				$scope.$emit("_bmSetImageRequest", i.variants[t]);
				return;
			}
		};
		$scope.$emit("_bmSetImageRequest", i);
	};
}]);
