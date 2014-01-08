
/* 
 * 
 * EVENTO
 * 
 */

	eventual.Event = eventual.BaseRelationalModel.extend({
		urlRoot: eventual.config.rest.url+"/api/event",	
		idAttribute: 'id',
	 		defaults: {
				id: null,
				name: '',
				description: '',
				date: '',
				ownerId:null
				//relacion entre el evento y los parcitipantes
        	},relations: [{
                type: Backbone.HasMany,
                key: 'users',
                relatedModel: 'eventual.User',
                reverseRelation: {
        	    	key: 'participates',
        	    	includeInJSON: 'id',
        		},}]
        	,initialize: function () {
                this.validators = {};
				this.validators.name = function (value) {
				    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Nombre obligatorio"};
				};
				this.validators.description = function (value) {
				    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Descripción obligatoria"};
				};
				this.validators.date = function (value) {
				    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Fecha obligatoria"};
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
	});
	
	/* 
	 * 
	 * EVENTOS
	 * 
	 */
    
	eventual.Events = eventual.BaseCollection.extend({
        model: eventual.Event,
      
        url: function () {
        	return  eventual.config.rest.url+"/api/event";
          },
        
        comparator: function(event){
        	return event.id;
        }
	});
	
/* 
 * 
 * USUARIO
 * 
 */
	
	eventual.User =  eventual.BaseRelationalModel.extend({
		
	    urlRoot:  eventual.config.rest.url+"/api/user",	
				defaults: {
				id: null,
				name: '',
				password: '',
				mail: '',
				status:''
		}
		,initialize: function () {
		    this.validators = {};
			this.validators.name = function (value) {
			    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Nombre obligatorio"};
			};
			this.validators.mail = function (value) {
				var validation= value && value.length > 0 ? {isValid: true} : {isValid: false, message: "Mail obligatorio"};
				if (!validation.isValid){
					return validation;
				}
				var re = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
				return re.test(value) ? {isValid: true} : {isValid: false, message: "Formato de mail incorrecto"};
			};
			this.validators.password = function (value) {
				return value.length > 0 ? {isValid: true} : {isValid: false, message: "Password obligatorio"};
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
	});
    
	
	/* 
	 * 
	 * USUARIOS
	 * 
	 */
	
	eventual.Users = eventual.BaseCollection.extend({
       model: eventual.User,
      
        url: function () {
        	return  eventual.config.rest.url+"/api/user";
          },
        
        comparator: function(user){
        	return user.id;
        }
    });
	
	
	/* 
	 * 
	 * POST
	 * 
	 */
	
	eventual.Post =  eventual.BaseModel.extend({
		eventId:null,
		
		defaults: {
				id: null,
				description: ''
		},
		initialize: function (options) {
			this.eventId = options.eventId;
            this.validators = {};
			this.validators.description = function (value) {
			    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Descripción obligatoria"};
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
    	
    	url: function () {
			return  eventual.config.rest.url+"/api/event/"+this.eventId+"/posts"+((this.id !== null && typeof(this.id) !== "undefined")?"/"+this.id:"");
		}
	});
    
	/* 
	 * 
	 * POSTS
	 * 
	 */
	
	eventual.Posts = eventual.BaseCollection.extend({
	    eventId:null,
	    model: eventual.Post,
		initialize: function(options) {
	    	this.eventId = options.eventId;
		},
		url: function () {
    		return  eventual.config.rest.url+"/api/event/"+this.eventId+"/posts/";
		},
		comparator: function(post){
        	return post.postTime;
        }
	});

	
	/* 
	 * 
	 * IMAGE
	 * 
	 */
	
	eventual.Image =  eventual.BaseModel.extend({
		eventId:null,
		
		defaults: {
				id: null
		},
		initialize: function (options) {
			this.eventId = options.eventId;
        },
    	url: function () {
			return  eventual.config.rest.url+"/api/event/"+this.eventId+"/images"+((this.id !== null && typeof(this.id) !== "undefined")?"/"+this.id:"");
		}
	});
	
	
	/* 
	 * 
	 * IMAGES
	 * 
	 */
	
	eventual.Images = eventual.BaseCollection.extend({
	    eventId:null,
	    model: eventual.Image,
		initialize: function(options) {
	    	this.eventId = options.eventId;
		},
		url: function () {
    		return  eventual.config.rest.url+"/api/event/"+this.eventId+"/images/";
		},
		comparator: function(post){
        	return post.postTime;
        }
	});
	
	
	/* 
	 * 
	 * PROMO
	 * 
	 */
	
	eventual.Promo =  eventual.BaseModel.extend({
		defaults: {
				token: ''
		},
		initialize: function (options) {
			this.validators = {};
			this.validators.token = function (value) {
			    return value.length > 0 ? {isValid: true} : {isValid: false, message: "Código obligatorio"};
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
    	
    	url: function () {
			return  eventual.config.rest.url+"/api/event/token";
		}
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
            this.setData('user', null);
            this.setData('mail', null);
            this.setData('password', null);
            this.setData('redirectFrom', null);
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
			    that.router = new eventual.Router();
			    Backbone.history.start();
			});
		}
	});

	 
