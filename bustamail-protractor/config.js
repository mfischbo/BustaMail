exports.config = {
	seleniumAddress : 'http://localhost:4444/wd/hub',
	specs : [
		'specs/bustamail-security/login-spec.js',
		'specs/bustamail-security/orgunit-specs.js'
	],

	onPrepare : function() {
	},

	onComplete : function() {
	}
};

