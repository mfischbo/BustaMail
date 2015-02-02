var fb = require('frisby');

var fName = __filename.split('/').pop();
var asJson = { json : true };
var auth = {
	email		: process.env.email || 'schdahle@art-ignition.de',
	password	: process.env.pass  || 'test'
};

var base = process.env.baseUrl || 'http://localhost/bustamail';
var api  = base + '/api/security/orgUnits';

fb.create(fName + ' / Can login')
	.post(base + '/api/security/authentication', auth, asJson)
	.expectStatus(200)
	.after(function() {

		
		

	})
	.toss();
