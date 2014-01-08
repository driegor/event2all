eventual.Event = eventual.BaseModel.extend({

    urlRoot: eventual.config.rest.url+"/api/event",
    initialize: function () {
        this.validators = {};

        this.validators.name = function (value) {
            return value.length > 0 ? {isValid: true} : {isValid: false, message: "Nombre obligatorio"};
        };

        this.validators.description = function (value) {
            return value.length > 0 ? {isValid: true} : {isValid: false, message: "Descripción obligatoria"};
        };
        
        this.validators.date = function (value) {
        	var re = /^(0[1-9]|[12][0-9]|3[01])[\- \/.](?:(0[1-9]|1[012])[\- \/.](201)[1-9]{1})$/;
			return !value || re.test(value) ? {isValid: true} : {isValid: false, message: "Formato de fecha incorrecto"};
        };
    },
    comparator: function(event){
    	return event.id;
    },
    validateItem: function (key) {
        return (this.validators[key]) ? this.validators[key](this.get(key)) : {isValid: true};
    },

    validateAll: function () {

        var messages = {};

        for (var key in this.validators) {
            if(this.validators.hasOwnProperty(key)) {
                var check = this.validators[key](this.get(key));
                if (check.isValid === false) {
                    messages[key] = check.message;
                }
            }
        }

        return _.size(messages) > 0 ? {isValid: false, messages: messages} : {isValid: true};
    },

    defaults: {
		id: null,
		name: '',
		description: '',
		date: '',
		token: ''
    }
});

eventual.EventCollection = eventual.BaseCollection.extend({

    model: eventual.Event,
    url: eventual.config.rest.url+"/api/event",	

});


eventual.Post = eventual.BaseModel.extend({
    eventId:null,
    urlRoot: eventual.config.rest.url+"/api/event"+this.eventId+"/images",
    initialize: function (options) {
        this.eventId=options.eventId;
    },
    comparator: function(post){
    	return post.id;
    }
});

eventual.PostCollection = eventual.BaseCollection.extend({
	
	model: eventual.Post,
	eventId:null,
	initialize: function(options) {
    	this.eventId = options.eventId;
    },url: function () {
    	return  eventual.config.rest.url+"/api/event/"+this.eventId+"/images/";
	},
    initialize: function (options) {
    	this.eventId=options.eventId;
	},
});

/* 
 * 
 * AUTHENTICATION
 * 
 */

eventual.Authentication = eventual.BaseModel.extend({
	
	urlRoot:  eventual.config.rest.url+'/api/auth',
	
	defaults: {
		 mail: '',
		 password: '',
		 authenticated:false
	},
	
	initialize: function (options) {
		
		//en todas las peticiones contra el servidor, incluiremos el token, generado por el servidor, como 
    	//medida de seguridad
    	//si el token no existe, asociado al usuario
    	//devolverá un 401 y el usuario deberá volver a logarse
    	  
    	//las credenciales estarán guardadas en el localstorage del dispositivo, asi podrá logarse de forma transparente para 
    	//el usuario, cuando la sesión caduque.
		$.ajaxSetup({
			headers : {'X-CSRF-Token' : eventual.token}
		});
          
		this.validators = {};
		this.validators.mail = function (value) {
			return value && value.length > 0 ? {isValid: true} : {isValid: false, message: "Mail obligatorio"};
		};
		this.validators.password = function (value) {
			return value && value.length > 0 ? {isValid: true} : {isValid: false, message: "Password obligatorio"};
		};
	},
	
	validateItem: function (key) {
		return (this.validators[key]) ? this.validators[key](this.get(key)) : {isValid: true};
	},
	
	validateAll: function () {
		var messages = {};
        for (var key in this.validators) {
        	if(this.validators.hasOwnProperty(key)) {
        		var check = this.validators[key](this.get(key));
                if (check.isValid === false) {
                	messages[key] = check.message;
                }
        	}
        }
        return _.size(messages) > 0 ? {isValid: false, messages: messages} : {isValid: true};
	},
	
	getData : function(key){
		return Backbone.Model.prototype.get.call(this, key);
	},

	setData : function(key, value){
		Backbone.Model.prototype.set.call(this, key, value);
	    return this;
	},

    unset : function(key){
    	Backbone.Model.prototype.unset.call(this, key);
    	return this;   
    },

    clear : function(){
    	this.setData('authenticated', false);
        this.setData('user', '');
        this.setData('mail', '');
        this.setData('password', '');
        this.setData('redirectFrom', '');
        return this;
    },
    
    //el usuario decide deslogarse
    logout : function(callback){
          var that = this;
          $.ajax({
              url : this.url() + '/logout',
              type : 'DELETE'
          }).done(function(response){
              //limpiamos los datos de la sesión
              that.clear();
              //actualizamos el nuevo token
              eventual.token = response.csrf;
              that.initialize();
              callback();
          });
    },
    
    //el usuario está autenticado ?? 
    isAuth : function(callback){
    	var that = this;
        var auth = this.fetch();

        auth.done(function(response){
        	that.setData('authenticated', true);
            that.setData('user', JSON.stringify(response.user));
        });

        auth.fail(function(response){
        	response = response.responseText && response.responseText !="" ? JSON.parse(response.responseText):"";
	        that.clear();
            eventual.token = response.token && response.token !== eventual.token ? response.token : eventual.token;
            that.initialize();
        });
        
        auth.always(callback);
    
    }
});
 
 
	/* 
	* 
	* APP
	* 
	*/
	 
eventual.App = eventual.BaseModel.extend({
		router:null,
		start : function(){
			
			var that=this;
			//instanciamos el objeto autenticación
			eventual.auth = new  eventual.Authentication();
	        	  
		    //comprobamos si el usuario está autenticado en el servidor
			eventual.auth.isAuth(function(response){
		        		  
		    	//iniciamos el router
			    that.router = new eventual.AppRouter();
			    Backbone.history.start();
			});
		}
	});
 
 

