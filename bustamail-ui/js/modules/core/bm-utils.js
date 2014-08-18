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

BMApp.showSpinner = function() {
	$("#spinner").show();
}

BMApp.hideSpinner = function() {
	$("#spinner").hide();
}


BMApp.confirm = function(txt, callback) {
	
	//var $modal = angular.injector().get("$modal");
	
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
};

