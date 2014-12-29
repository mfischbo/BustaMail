BMApp.BounceMails.service("BounceAccountService", ['$http', function($http) {

	return {

		getAllByOwner : function(ownerId) {
			return $http.get('/api/bounceaccounts?owner=' + ownerId).success(function(data) {
				return data;
			});
		},
		
		getAccountById : function(id) {
			return $http.get('/api/bounceaccounts/' + id).success(function(data) {
				return data;
			});
		},
		
		createAccount : function(account) {
			return $http.post("/api/bounceaccounts", account).success(function(data) {
				return data;
			});
		},
		
		updateAccount : function(account) {
			account.userCreated = undefined;
			account.userModified = undefined;
			return $http.patch('/api/bounceaccounts/' + account.id, account).success(function(data) {
				return data;
			});
		},
		
		deleteAccount : function(accountId) {
			return $http({
				method : 'DELETE',
				url	   : '/api/bounceaccounts/' + accountId
			});
		}
	};
}]);


BMApp.BounceMails.service('BounceMailService', ['$http', function($http) {
	
	return {
		
		getAllByAccount : function(account) {
			return $http.get("/api/bounceaccounts/" + account.id + "/mails").success(function(data) {
				return data;
			});
		},
		
		getById : function(account, mailId) {
			return $http.get('/api/bounceaccounts/' + account.id + '/mails/' + mailId).success(function(data) {
				return data;
			});
		},
		
		deleteBounceMails : function(account, mailIds) {
			return $http({
				method : 'DELETE',
				url    : '/api/bounceaccounts/' + account.id + '/mails/',
				data   : mailIds
			});
		}
	};
}]);