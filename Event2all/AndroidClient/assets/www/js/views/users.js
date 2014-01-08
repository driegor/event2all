
/* 
 * 
 * BUSCADOR DE USUARIOS PARA UN EVENTO
 * 
 */

eventual.UsersSearchView =  eventual.BaseView.extend({
   ownerId:null,
   baseUrl:null,
   attributes: {'data-add-back-btn': 'true'},
   initialize: function(options) {
		this.ownerId=options.ownerId;
    	this.collection.bind('remove', this.render, this);
       	this.template = _.template($('#users-search-template').html());
       	this.baseUrl=options.baseUrl;
    },
    events: {
		"click #add-user": "addUser",
		"click #back": "back",
    },   
    addUser: function(ev){
		var userId=$(ev.currentTarget).data("id");
    	eventual.event  =  eventual.Event.findOrCreate(this.ownerId);
	    
    	if (!eventual.event) {
			eventual.event = new eventual.Event({id:this.ownerId});
	    }
	    
    	eventual.utils.customSync({url:eventual.event.url()+'/users',method:'PUT',data:{ids:[userId]}},
	    		function(data){ 
	    			window.history.back();
    	},null,"events/"+this.ownerId+"/users2join"); 	
	    
    	ev.preventDefault();
	    return false;
    },
    render: function() {
		var template = this.template;
	 	$(this.el).html(template( { "users": this.collection.toJSON(),"ownerId":this.ownerId,"baseUrl":this.baseUrl}));
	 	return this;
    }
});

/* 
 * 
 * DETALLE DE USUARIO
 * 
 */

   eventual.UserDetailsView =  eventual.BaseView.extend({
    	baseUrl:null,
        attributes: {'data-add-back-btn': 'true'},
    	initialize: function(options) {
        	this.template = _.template($('#user-details-template').html());
        	this.baseUrl=options.baseUrl;
    	},
        render: function() {
    		$(this.el).html(this.template({"user":this.model.toJSON(),"baseUrl":this.baseUrl}));
    	    return this;
    	},
    	events: {
    		"click #back": "back",
    	}
    });

   
   /* 
    * 
    * POPUP BORRAR USUARIO
    * 
    */
   
   
   eventual.DeleteUserView =  eventual.BaseView.extend({
	   ownerId:null,
	   initialize: function(options) {
			this.ownerId=options.ownerId;
	    	this.template = _.template($('#delete-generic-template').html());
	    	this.customMessage={question:'¿Seguro que deseas eliminar al usuario seleccionado del evento?'};
	   },
	   
	   events: {
			"click #delete": "deleteUser",
			"click #cancel": "cancel"
	   },   
	    	    
	   deleteUser: function(ev){
		   console.log('clicked');
		   eventual.event  =  eventual.Event.findOrCreate(this.ownerId);
		    
		   if (!eventual.event) {
				eventual.event = new eventual.Event({id:this.ownerId});
		   }
		    
		   eventual.utils.customSync({url:eventual.event.url()+'/users',method:'DELETE',data:{ids:[this.model.id]}},
		    		function(data){ 
		    			console.log("close dialog");
		    			eventual.app.router.closeDialog();
	    	},null,"events/"+this.ownerId+"/show"); 	
		    
	    	ev.preventDefault();
		  	return false;
    	},
		cancel: function(ev){
	    	console.log("close dialog");
		 	eventual.app.router.closeDialog();
		 	return false;
		},
		onClose: function(){
			console.log('closing view DeleteUserView');
		},
	    render: function() {
		 	var template = this.template;
		 	this.customMessage["item2delete"]=this.model.toJSON().name;
		 	$(this.el).html(template( {"message":this.customMessage}));
		   	return this;
	    }
	});


    /* 
    * 
    * POPUP BORRAR CREDENCIALES DE USUARIO
    * 
    */
   
   eventual.DeleteCredUserView =  eventual.BaseView.extend({
	  initialize: function(options) {
			this.template = _.template($('#delete-generic-template').html());
	    	this.customMessage={question:'¿Seguro que deseas eliminar los datos de sesión?'};
	   },
	   
	   events: {
			"click #delete": "deleteCred",
			"click #cancel": "cancel"
	   },   
	    	    
	   deleteCred: function(ev){
		   var self=this;
		   eventual.auth.logout(function (response){
			   eventual.utils.clearCredentials();
			   eventual.app.router.closeDialog();
			   Backbone.history.navigate('login', { trigger : true });
		   });
		   ev.preventDefault();
		   return false;
	   },
	   cancel: function(ev){
		   console.log("close dialog");
		   eventual.app.router.closeDialog();
		   return false;
	   },
	   onClose: function(){
		   console.log('closing view DeleteUserView');
	   },
	   render: function() {
		   var template = this.template;
		   this.customMessage["item2delete"]='Eliminar datos del usuario:'+eventual.auth.getData("mail");
		   $(this.el).html(template( {"message":this.customMessage}));
		   return this;
	   }
   });
   
   
   /* 
    * 
    * CREAR/EDITAR USUARIO
    * 
    */

	eventual.UserFormView =  eventual.BaseView.extend({
	    initialize: function(options) {
			this.template = _.template($('#user-form-template').html());
		},
	    	
	  	events: {
		    "change"     				: "change",
		    "click #save"				: "beforeSave",
		    "click #clearCredentials"	: "logout",
			"click #back"				: "back",
	    },
	    
	    save: function(ev){
		
		   	var userId =this.model.id,
	    	user=this.model,
	    	formJSON = $('#form').formParams();
	    	  
		   	if (userId){
		   	    formJSON["id"]=userId;
		   	    
		   	    eventual.utils.customSync({url:user.url(),method:'PUT',data:formJSON},
		   	    	function(data){ 
			    		window.history.back();
		    	},null,"profile"); 	
			    
		   	}else{
		   		
		   		user = new eventual.User(formJSON);
		   		var that=this;
		   		
		   		//validamos que el mail no existe previamente
		   		eventual.utils.customSync({url:eventual.config.rest.url+"/api/user/checkmail/"+ window.btoa(formJSON.mail),method:'GET',data:null},
			   	    	function(data){ 
				    		if (data===true){
		   					
		   			           	user.save({},{
			   		           		success:function(user){
			   		           			//acabamos de crear el nuevo usuario
			   		           			//podemos setean las guardar las nuevas credenciales e intentar autenticarnos en la aplicación
			   		           			eventual.utils.saveCredentials({mail:formJSON.mail,password:formJSON.password});
			   		           			
			   		           			//vamos a intentar auto logarnos         			
			   		           			eventual.utils.autoLogin(function(autoLogin){
			   		           			    if (autoLogin){
			   		           			    	Backbone.history.navigate('events', { trigger : true });  
			   		           				}else{
			   		           					//no hemos podido logarnos
			   		           					Backbone.history.navigate('login', { trigger : true });  
			   		           				}
			   		           			});
			   		           		}
			   					});
		   					}else{
		   						eventual.utils.error("Error!!!","'"+formJSON.mail+"' ya está siendo usado por otro usuario");
		   					}
		   			},null,"profile"); 	
		   		
	    	   
	        }
		   	return false;
		}, 
		
		logout: function(ev){
			eventual.app.router.loadDialog(new eventual.DeleteCredUserView());
		},
    	render: function() {
		    var user = this.model;
	    	$(this.el).html(this.template(this.model.toJSON()));
	    	return this;
	    },
	    
	   
	});


/* 
* 
* LOGIN
* 
*/


	eventual.LoginView =  eventual.BaseView.extend({
		
		initialize: function(options) {
			this.template = _.template($('#login-template').html());
		},
		
	    events: {
		    "change"     	: "change",
		    "click #save"	: "beforeSave",
		    "click #createUser"	: "createUser"
		},
	    
	    save: function(ev){
		   	var formJSON = $('#form').formParams();
		   	var that=this;
	    	
	    	// nos logamos contra el sistema con los datos introducidos
	    	eventual.utils.customSync({url:this.model.url()+'/authenticate',method:'POST',data:formJSON},
	    		function(data) { 
	    				eventual.auth.setData('authenticated', true);
	    				eventual.auth.setData('user', JSON.stringify(data.user));
	    				
	    				//guardamos las credencials en el localstorage para evitar que el usuario 
	    				//tenga que logarse una y otra vez , lo hace de forma transparente
	    				eventual.utils.saveCredentials({mail:formJSON.mail,password:formJSON.password});
	    				
	    				if(eventual.auth.getData('redirectFrom')){
			            	var path = eventual.auth.getData('redirectFrom');
			            	eventual.auth.unset('redirectFrom');
			                Backbone.history.navigate(path, { trigger : true });
			            }else{
			            	Backbone.history.navigate('events', { trigger : true });
			            }
	    				eventual.utils.success("Bienvenido "+formJSON.mail,"Bienvenido de nuevo");
	    			}
	    		,function(error) { 
	    			eventual.utils.clearCredentials();
	    			eventual.utils.error("Error !!!","Datos no válidos,prueba de nuevo");
	    		}
	    	); 	
	    	return false;
		}, 
		createUser: function(ev) {
			Backbone.history.navigate('createUser', { trigger : true });
		    ev.preventDefault();
	        return false;
		},
		render: function() {
		   
	    	$(this.el).html(this.template(this.model.toJSON()));
	    	return this;
	    }
	});

    