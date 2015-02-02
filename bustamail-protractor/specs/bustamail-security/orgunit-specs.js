describe("Bustamail Security / OrgUnit", function() {

	it('Should show created organizational unit' ,function() {

		browser.get('http://localhost/bustamail/#/security/orgUnits');
	
		var units = element.all(by.repeater('node in rootUnits'));	
		var users = element.all(by.repeater('u in users.content'));	
		
		expect(units.count()).toBe(1);
		expect(element(by.model('orgUnit.name')).getAttribute('value')).toBe('Test Unit');
		expect(element(by.model('orgUnit.description')).getAttribute('value')).toBe('First real unit');
		expect(users.count()).toBe(1);
	});
});
