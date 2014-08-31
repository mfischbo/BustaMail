function BMNodeEdit(doc, template) {
	this.editor			  = undefined;
	this.doc			  = doc;
	this.template 		  = template;
	this.templateSettings = template.settings; 
};

BMNodeEdit.prototype.setup = function() {

	this.editor		  = $("#editor");
	
	// create fragment controller
	this.fragControl = new BMFragmentControl(this.editor);
	this.fragControl.setup();
	
	// create element controller
	this.elementControl = new BMElementControl(this.editor);
	this.elementControl.setup();

	// create image controller
	this.imageControl = new BMImageControl(this.editor);
	this.imageControl.setup();

	
	// create the text selection controll
	this.textSelectionControl = new BMTextSelectionControl(this.editor);
	this.textSelectionControl.setup();
	
	// append stylesheets and scripts to the editor
	for (var i in this.doc.resources) {
		var r = this.doc.resources[i];
		if (r.mimetype == 'text/css')
			this.editor.append('<link rel="stylesheet" type="text/css" href="./img/media/' + r.id + '">');
		
		if (r.mimetype == 'text/javascript')
			this.editor.append('<script type="text/javascript" src="./img/media/'+r.id+'"></script>');
	}
	
	var fontSettings = {
		defaultFontFamily : "Arial",
		defaultFontSize 	: "12px;",
		defaultFontColor	:	"#000"
	};
	
	if (this.templateSettings) {
		fontSettings.defaultFontFamily = this.templateSettings.defaultFontFamily,
		fontSettings.defaultFontSize = this.templateSettings.defaultFontSize,
		fontSettings.defaultFontColor = this.templateSettings.defaultFontColor
	}
	
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
};

BMNodeEdit.prototype.appendWidget = function(w) {
	this.fragControl.appendFragment(w);
};

BMNodeEdit.prototype.replaceWidget = function(widget) {
	this.elementControl.replaceElement(widget);
};

BMNodeEdit.prototype.reloadCSSFile = function(id) {
	$('link[href="./img/media/'+id+'"]').remove();
	this.editor.append('<link rel="stylesheet" type="text/css" href="./img/media/'+id+'">');
};