 //BaseRouter
eventual.BaseRouter = Backbone.Router.extend({
	before: function(){},
    after: function(){},
    route : function(route, name, callback){
    	
    	if (!_.isRegExp(route)) route = this._routeToRegExp(route);
        if (_.isFunction(name)) {
        	callback = name;
            name = '';
        }
        
        if (!callback) callback = this[name];
        var router = this;
        
        Backbone.history.route(route, function(fragment) {
        	var args = router._extractParameters(route, fragment);
            var next = function(){
            	
            	callback && callback.apply(router, args);
                router.trigger.apply(router, ['route:' + name].concat(args));
                router.trigger('route', name, args);
                
                Backbone.history.trigger('route', router, name, args);
                	router.after.apply(router, args);                
            }
            router.before.apply(router, [args, next]);
        });
        return this;
    }
});


//BaseModel
eventual.BaseModel = Backbone.Model.extend({
	save: function(model,options) {
		
		options || (options = {});
		 
		//comprobamos si existe una url
		if (typeof(options.url) !== "undefined"){
			options.url=this.url()+options.url;
		}
		
		//preparamos la función de callback
		var that=this;
		var retryFunction = function (options) { 
			return Backbone.Model.prototype.save.call(that, model,options);
		}
		
		//preparamos los callback de vuelta en caso de exito o fallo
		//en  caso de error reintentaremos ejecutar esta misma función
		eventual.utils.handleSaveSuccess(options);
		eventual.utils.handleSaveError("save",retryFunction,options);
		
		//seguimos con la ejecución normal
		return Backbone.Model.prototype.save.call(this, model,options);
	},
	fetch: function(options) {
		
		options || (options = {});
		 
		//comprobamos si existe una url
		if (typeof(options.url) !== "undefined"){
			options.url=this.url()+options.url;
		}
		
		//preparamos la función de callback
		var that=this;
		var retryFunction = function (options) { 
			return Backbone.Model.prototype.fetch.call(that, options);
		}
		
		//preparamos los callback de vuelta en caso de exito o fallo
		//en  caso de error reintentaremos ejectuar esta misma función
		eventual.utils.handleFetchSuccess(options);
		eventual.utils.handleFetchError("fetch",retryFunction,options);
		
		//seguimos con la ejecución normal
		return Backbone.Model.prototype.fetch.call(this, options);
	}
});

//BaseCollection
eventual.BaseCollection = Backbone.Collection.extend({
	fetch: function(options) {
	   
		options || (options = {});
		 
		//comprobamos si existe una url
		if (typeof(options.url) !== "undefined"){
			options.url=this.url()+options.url;
		}
		
		//preparamos la función de callback
		var that=this;
		var retryFunction = function (options) {
			return Backbone.Collection.prototype.fetch.call(that, options);
		}
		
		//preparamos los callback de vuelta en caso de exito o fallo
		//en el caso de error reintentaremos ejectuar esta misma función
		eventual.utils.handleFetchSuccess(options);
		eventual.utils.handleFetchError("fetch",retryFunction,options);
		
		//seguimos con la ejecución normal
		return Backbone.Collection.prototype.fetch.call(this, options);
	}
});


//BaseView
eventual.BaseView = Backbone.View.extend({
	
	//sobreescribimos el método borrar de todas las vistas
	close : function(){

		this.remove();
		this.unbind();
		if (this.onClose){
		    this.onClose();
		}
	 }
});

