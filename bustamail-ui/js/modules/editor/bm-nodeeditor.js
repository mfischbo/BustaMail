function BMNodeEdit(templateSettings) {
	this.editor			  = undefined;
	this.templateSettings = templateSettings; 
};

BMNodeEdit.prototype.setup = function() {

	this.editor		  = $("#editor");
	this.fragControl = new BMFragmentControl(this.editor);
	this.fragControl.setup();
	
	this.elementControl = new BMElementControl(this.editor);
	this.elementControl.setup();

	this.imageControl = new BMImageControl(this.editor);
	this.imageControl.setup();
	
	this.textSelectionControl = new BMTextSelectionControl(this.editor);
	this.textSelectionControl.setup();

	var fontSettings = {
			defaultFontFamily 	: this.templateSettings.defaultFontFamily,
			defaultFontSize		: this.templateSettings.defaultFontSize,
			defaultFontColor	: this.templateSettings.defaultFontColor
	};
	
	this.textStyleControl = new BMTextStyleControl(this.editor, fontSettings);
	this.textStyleControl.setup();
	
	var that = this;
	$(document).on("_bmImagePickerRequested", function() {
		that.fragControl.hideControls();
		that.elementControl.hideControls();
		that.imageControl.hideControls();
	});
};


BMNodeEdit.prototype.destroy = function() {
	this.fragControl.destroy();
	this.elementControl.destroy();
	this.imageControl.remove();
	this.slider.remove();
};

BMNodeEdit.prototype.appendWidget = function(w) {
	this.fragControl.appendFragment(w);
};

BMNodeEdit.prototype.replaceWidget = function(widget) {
	this.elementControl.replaceElement(widget);
};