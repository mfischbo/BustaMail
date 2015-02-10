BMApp.TemplateService = angular.module("TemplateServiceModule", []);
BMApp.TemplateService.service("TemplateService", ['$http', function($http) {
	
	return {
		/**
		 * Returns all template packs for the given owner ObjectId
		 * @returns A page containing template packs
		 */
		getTemplatePacksByOwner : function(owner) {
			return $http.get('/api/templatepacks?owner=' + owner).success(function(data) {
				return data;
			});
		},
	
		/**
		 * Prepares a page of template packs to be used by a selection
		 */
		prepareForSelection : function(templatePacks) {
			var retval = [];
			for (var i in templatePacks.content) {
				var p = templatePacks.content[i];
				for (var q in templatePacks.content[i].templates) {
					templatePacks.content[i].templates[q].pack = {
							name : p.name,
							id   : p.id
					};
					retval.push(templatePacks.content[i].templates[q]);
				}
			}
			return retval;
		},
		
		getTemplatePackByTemplateId : function(id, packs) {
			for (var q in packs) {
				for (var x in packs[q].templates) {
					if (packs[q].templates[x].id == id)
						return packs[q];
				}
			}
		},
		
		getTemplatePackById : function(id) {
			return $http.get('/api/templatepacks/' + id).success(function(data) {
				return data;
			});
		},
		
		createTemplatePack : function(pack) {
			return $http.post('/api/templatepacks/', pack).success(function(data) {
				return data;
			});
		},
		
		copyTemplatePack   : function(id) {
			return $http.put('/api/templatepacks/' + id).success(function(data) {
				return data;
			});
		},
		
		updateTemplatePack : function(pack) {
			return $http.patch('/api/templatepacks/' + pack.id, pack).success(function(data) {
				return data;
			})
		},
		
		deleteTemplatePack : function(id) {
			return $http({
				method : 'DELETE',
				url    : '/api/templatepacks/' + id
			});
		}
	}
}]);