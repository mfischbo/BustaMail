describe('Bustamail Security Specs / Login', function() {

	it('Should be able to successfully login', function() {
	
		browser.get('http://localhost/bustamail/login#/login');
		element(by.model('credentials.email')).sendKeys('schdahle@art-ignition.de');
		element(by.model('credentials.password')).sendKeys('test');
		element(By.css('button[type="submit"]')).click();

		expect(browser.getLocationAbsUrl()).toBe('/subscriber');
	});

/*
	it('Should successfully log out', function() {
	
		browser.get('http://localhost/bustamail/#/subscriber');
		element(By.css('a[href="#/logout"]')).click();
	
		browser.sleep(2000);
		expect(browser.getLocationAbsUrl()).toBe('/login');
	});
*/
});
