var BMElementControl = function(editor) {
	this.selectedElement = undefined;
	this.editor = editor;
};


BMElementControl.prototype.setup = function() {
	
	var that = this;
	this.editor.click(function(e) {
		e = $(e.target);
		if (!e.hasClass("bm-element"))
			e = e.parents("bm-element").first();
		
		if (e) {
			if (!that.selectedElement)
				that.select(e);
			else
				that.deselect(e);
		} else {
			if (that.selectedElement)
				that.deselect(e);
		}
	});
	
	this.editor.mouseover(function(e) {
		e = $(e.target);
		if (e.hasClass("bm-element")) 
			that.hover(e);
	});
	
	this.editor.mouseout(function(e) {
		e = $(e.target);
		if (e.hasClass("bm-element-hovered"))
			that.unhover(e);
	});
};

BMElementControl.prototype.hover = function(element) {
	if (element.hasClass("bm-element-hovered")) return;
	element.addClass("bm-element-hovered");
}

BMElementControl.prototype.unhover = function(element) {
	if (!element.hasClass("bm-element-hovered")) return;
	element.removeClass("bm-element-hovered");
}

BMElementControl.prototype.select = function(element) {
	if (this.selectedElement)
		this.deselect(element);
	
	if (element.hasClass("bm-element-hovered")) {
		element.removeClass("bm-element-hovered");
		element.addClass("bm-element-focused");
		this.selectedElement = element;
		$(document).trigger("_bmElementSelected", element);
	}
};

BMElementControl.prototype.deselect = function(element) {
	if (this.selectedElement) {
		this.selectedElement.removeClass("bm-element-focused");
		this.selectedElement = undefined;
		$(document).trigger("_bmElementUnselected", element);
	}
};

BMElementControl.prototype.replaceElement = function(widget) {
	var src = $(widget.source);
	if (this.selectedElement) {
		var el = src.find(".bm-on-replace");
		this.selectedElement.html(el.parent().html());
	}
};

BMElementControl.prototype.hideControls = function() {
}

BMElementControl.prototype.destroy = function() {
};


var BMImageControl = function(editor) {
	this.editor			= editor;
	
	this.selectedImage	= undefined;
	
	this.imageControl 	= undefined;
	this.slider			= undefined;
	this.sliderVisible	= false;
};

BMImageControl.prototype.setup = function() {
	// create image control
	this.editor.append('<div id="bm-image-control">'
			+ '<button id="bm-image-control-pick" class="btn btn-xs btn-info"><span class="fa fa-refresh"></span></button>'
			+ '</div>');
	this.imageControl = $("#bm-image-control");
	this.imageControl.hide();
	this.imageControl.css("position", "absolute");
	this.imageControlPick = $("#bm-image-control-pick");
	this.slider = $("#slider");
	
	var that = this;
	
	this.editor.click(function(e) {
	
		var e = $(e.target);
		if (e.hasClass("bm-changeable") && e[0].tagName == "IMG") {
			that.select(e);
		} else
			that.deselect(e);
		
		if (that.sliderIsVisible) {
			if (e.parents("#slider").length == 0) {
				that.slider.animate({'margin-left' : '-915'}, 400, 'swing', function(){
					that.slider.css("visibility",'hidden');
				});
				that.sliderIsVisible = false;
			}
		}
	});
	
	$("#slider").click(function(e) {
		e.stopPropagation();
	});
	
	that.imageControlPick.click(function(e) {
		$("#slider").css("visibility",'visible');
		that.slider.animate({'margin-left' : '-15'}, 400, 'swing', function() {
			$(document).trigger("_bmImagePickerRequested");
			that.sliderIsVisible = true;
		});
		that.imageControl.hide();
		e.stopPropagation();
	});
	
	that.editor.scroll(function(e) {
		that.renderControls();
	});
};

BMImageControl.prototype.select = function(element) {
	this.selectedImage = element;
	$(document).trigger("_bmElementSelected", element);
	this.renderControls();
};

BMImageControl.prototype.deselect = function(element) {
	this.selectedImage = undefined;
	//$(document).trigger("_bmElementUnselected", element);
	this.renderControls();
}

BMImageControl.prototype.renderControls = function() {
	if (this.selectedImage) {
		var p = this.selectedImage.position();
		var t = p.top + 6;
		var w = p.left + 6;
		this.imageControl.css("top", t);
		this.imageControl.css("left", w);
		this.imageControl.show();
	} else 
		this.imageControl.hide();
};

BMImageControl.prototype.hideControls = function() {
	this.imageControl.hide();
};


/**
 * Fragment controller
 */

var BMFragmentControl = function(editor) {
	this.editor = editor;
	this.selectedFragment = undefined;
	
	this.control = undefined;
	this.remove  = undefined;
	this.controlUp = undefined;
	this.controlDown = undefined;
}

BMFragmentControl.prototype.setup = function() {
	this.editor.append('<div id="bm-control">'
			+ '<button id="bm-control-up" class="btn btn-xs btn-warning"><span class="fa fa-chevron-up"></span></button>&nbsp;'
			+ '<button id="bm-control-down" class="btn btn-xs btn-warning"><span class="fa fa-chevron-down"></span></button>&nbsp;'
			+ '<button id="bm-control-delete" class="btn btn-xs btn-danger"><span class="fa fa-trash-o"></span></button>'
			+'</div>');
	this.control	  = $("#bm-control");
	this.control.hide();
	this.control.css("position", "absolute");
	this.remove       = $("#bm-control-delete");
	this.controlUp	  = $("#bm-control-up");
	this.controlDown  = $("#bm-control-down");
	
	var that = this;

	$("#editor").click(function(e) {
		var p = $(e.target).parents(".bm-fragment");
		if (p.length > 0) {
			var fragment = p.first();
			if (fragment == that.selectedFragment)
				that.unselect();
			else {
				that.select(p)
			}
		} else {
			// click was outside of a fragment
			that.deselect();
		}
	});
	
	$("#editor").mouseover(function(e) {
		var c = $(e.target);
		var p = c.parents(".bm-fragment");
		if (p.length > 0)
			that.hover(p);
	});

	$("#editor").mouseout(function(e) {
		var c = $(e.target);
		var p = c.parents(".bm-fragment");
		if (p.length > 0)	
			that.unhover(p);
	});
	
	that.editor.scroll(function(e) {
		that.renderControls();
	});
	
	// bind control for remove button
	that.remove.click(function(e) {
		that.removeFragment();
		e.stopPropagation();
	});
	that.controlUp.click(function(e) {
		that.moveFragmentUp();
		e.stopPropagation();
	});
	that.controlDown.click(function(e) {
		that.moveFragmentDown();
		e.stopPropagation();
	});
};


BMFragmentControl.prototype.renderControls = function() {
	if (this.selectedFragment) {
		var p = this.selectedFragment.position();
		var t = p.top  - this.control.height();
		var w = p.left+ this.selectedFragment.width() - this.control.width();
		this.control.css("top", t);
		this.control.css("left", w);
		this.control.show();
	} else {
		this.control.hide();
	}
};

BMFragmentControl.prototype.hover = function(element) {
	if (element.hasClass("bm-fragment-hovered")) return;
	element.addClass("bm-fragment-hovered");
};

BMFragmentControl.prototype.unhover = function(element) {
	if (!element.hasClass("bm-fragment-hovered")) return;
	element.removeClass("bm-fragment-hovered");
};

BMFragmentControl.prototype.select = function(fragment) {
	// check if another fragment is selected. if so deselect
	if (this.selectedFragment)
		this.deselect();

	// select the incoming fragment
	if (fragment.hasClass("bm-fragment-hovered")) {
		fragment.removeClass("bm-fragment-hovered");
		fragment.addClass("bm-fragment-focused");
		this.selectedFragment = fragment;
		this.renderControls();
	}
};

BMFragmentControl.prototype.deselect = function() {
	if (this.selectedFragment) {
		this.selectedFragment.removeClass("bm-fragment-focused");
		this.selectedFragment = undefined;
		this.renderControls();
	}
};


BMFragmentControl.prototype.removeFragment = function() {
	if (this.selectedFragment) {
		var that = this;
		this.selectedFragment.animate({"opacity" : 0}, 400, 'swing', function() {
			that.selectedFragment.remove();
			that.selectedFragment = undefined;
			that.renderControls();
		});
	};
};

BMFragmentControl.prototype.appendFragment = function(w) {
	if (this.selectedFragment) {
		this.selectedFragment.after(w.source);
	} else {
		// find the last bm-fragment in the document
		if ($(".bm-fragment").last()) {
			$(".bm-fragment").last().after(w.source);
			return;
		}
		// if non matches append it to the html wrapping body
		$("#editor-content").append(w.source);
		//$("tr.bm-fragment").parents("tbody").append(w.source);
	}
};

BMFragmentControl.prototype.hideControls = function() {
	this.control.hide();
};

BMFragmentControl.prototype.moveFragmentUp = function() {
	if (!this.selectedFragment) return;
	// select the element that is this elements predecessor
	var p = this.selectedFragment.prev(".bm-fragment");
	this.switchNodes(this.selectedFragment[0], p[0]);
	this.renderControls();
};

BMFragmentControl.prototype.moveFragmentDown = function() {
	if (!this.selectedFragment) return;
	var p = this.selectedFragment.next(".bm-fragment");
	this.switchNodes(this.selectedFragment[0], p[0]);
	this.renderControls();
};


BMFragmentControl.prototype.switchNodes = function(elm1, elm2) {
	var parent1, next1,
    parent2, next2;

	parent1 = elm1.parentNode;
	next1   = elm1.nextSibling;
	parent2 = elm2.parentNode;
	next2   = elm2.nextSibling;

	parent1.insertBefore(elm2, next1);
	parent2.insertBefore(elm1, next2);
};

BMFragmentControl.prototype.destroy = function() {
	this.remove.unbind('click');
	this.controlDown.unbind('click');
	this.controlUp.unbind('click');
	this.control.remove();
};

BMTextSelectionControl = function(editor) {
	this.editor = editor;
	rangy.init();
	this.selectionApplier = rangy.createCssClassApplier("bm-text-focused", {
		elementTagName : "span",
		normalize	   : "true"
	}, ["a", "b", "i"]);
	this.textSelection = undefined;
};

BMTextSelectionControl.prototype.setup = function() {

	var that = this;
	this.editor.mouseup(function(e) {

		that.removeSelection();
		var rs = rangy.getSelection(window);
		that.createSelection(rs);
	});
};

BMTextSelectionControl.prototype.removeSelection = function() {
	if (this.textSelection) {
		this.textSelection.each(function() {
			$(this).replaceWith(this.childNodes);
		})
		this.textSelection = undefined;
	}
	$(document).trigger("_bmNoTextSelection");
	$(".bm-text-focused").each(function() { $(this).removeClass("bm-text-focused"); });
};


BMTextSelectionControl.prototype.createSelection = function(rs) {
	if (rs.anchorOffset != rs.focusOffset) {
		
		// check if the selection contains a html node
		var selectedNodes = [];
		$("#editor a").each(function() {
			if (rs.containsNode(this, true)) 
				selectedNodes.push(this);
		});
		
		$("#editor b").each(function() {
			if (rs.containsNode(this, true))
				selectedNodes.push(this);
		});
		
		$("#editor i").each(function() {
			if (rs.containsNode(this, true))
				selectedNodes.push(this);
		});
		
		if (selectedNodes.length > 0) {
			$(selectedNodes[0]).addClass("bm-text-focused");
		} else {
			this.selectionApplier.applyToSelection();
		}
		this.selection = $(".bm-text-focused");
		var sel = $(this.selection);
		rs.detach();
		$(document).trigger("_bmTextSelection", sel);
	}
};


BMTextStyleControl = function(editor, initialStyle) {
	this.editor 	= editor;
	this.iss	 	= initialStyle;
};

BMTextStyleControl.prototype.setup = function() {
	var nodes = $('[contenteditable="true"]');
	this.applyStyles(nodes);
	
	nodes = $('a.bm-hyperlink');
	this.applyStyles(nodes);
	
	// register to any changes in the dom
	var that = this;
	$(document).on("_bmFragmentAdded", function(event, fragment) {
		that.applyStyles($(fragment));
	});
};

BMTextStyleControl.prototype.applyStyles = function(nodes) {
	var that = this;
	
	nodes.each(function() {
		var cssStyle = {};
		var style = $(this).attr("style") || "";
		
		if (that.iss.defaultFontSize)
			cssStyle['font-size'] = that.iss.defaultFontSize + "px";
		if (that.iss.defaultFontFamily)
			cssStyle['font-family'] = that.iss.defaultFontFamily;
		if (that.iss.defaultFontColor) 
			cssStyle['color'] = that.iss.defaultFontColor;
		
		$(this).css(cssStyle);
	});
};


