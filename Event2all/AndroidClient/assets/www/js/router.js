eventual.Router = eventual.BaseRouter.extend({
	currentDialog: null,
	firstPage:null,
	routes : {
		"" : "events",
		"events" 						:"events",
		"events/add"					:"addEvent",
		"events/:eventId/edit"			:"editEvent",
		"events/:eventId/post-image"	:"postImage",
		"events/:eventId/show"			:"showEvent",
		"events/:eventId/images"		:"images",
		"events/:eventId/posts"			:"posts",
		"events/:eventId/post/:postId"	:"comments",
		"events/:eventId/users2join"	:"users2join",
		"users/:userId/edit"			:"editUser",
		":entity/:entityId/add-image"	:"addImage",
		"createUser"					:"createUser",
		"login" 						:"showLogin",
		"profile"						:"showProfile",
		"promo"							:"promo",
		"back"							:"back"
	},
	//método de inicialización
	initialize:function () {
		console.log('iniciamos router');
		this.firstPage = true;
    },
    
    // rutas que no necesitan autentificación, si no hay un usuario autenticado
    // no redigirá a la página de login
    requresAuth : ['#login','#createUser'],

    // rutas que no debería ser accesibles, cuando el usuario esté logado
    preventAccessWhenAuth : ['#login'],
    
    before : function(params, next){
    	
    	//mostramos mensaje al usuario
    	eventual.utils.showLoading("Cargando...");
    	
        //comprobamos si el usuario actual está  autentificado
        var isAuth = eventual.auth.getData('authenticated')!==null?eventual.auth.getData('authenticated'):false;
        var path = Backbone.history.location.hash;
        var cancelAccess = _.contains(this.preventAccessWhenAuth, path);
        var allow = _.contains(this.requresAuth, path);
        
        //la pantalla solicitada necesita autenticación, por defecto todas.
        //el usuario no está autenticado y no se trata de una pantalla a evitar cuando
        //el usuario está autenticado
        if(!isAuth && !cancelAccess && !allow){
        	
        	//guardamos el path, para redirigir despues del login
        	eventual.auth.setData('redirectFrom', path);
            Backbone.history.navigate('login', { trigger : true });
        }else if(isAuth && cancelAccess){
        	//el usuario está autenticado y quiero ir al pagina de login, le
        	//mostramos la pagina de detalle de usuario
        	Backbone.history.navigate('profile', { trigger : false });
        }else{
        	//vamos a la ruta específicada
            return next();
        }                        
    },
    
    //rutas propias de la aplicación
    
    /*Crear nuevo evento */
    
    addEvent : function() {
    	var event = new eventual.Event();
       	this.loadView(new eventual.EventFormView({model: event}));
	},
	
	/*Editar evento */
	editEvent : function(eventId) {
		var self=this;
	    eventual.event  =  eventual.Event.findOrCreate(eventId);
	    if (!eventual.event) {
			eventual.event = new eventual.Event({id:eventId});
	    }
	    eventual.event.fetch({
            success: (function (data) {
            	console.log('success');
            	self.loadView(new eventual.EventFormView({model: eventual.event}));                    
            })
        });
    },
    
    /*Listar eventos */
	events : function() {
    	
		var view;
	    console.log('haschange events');
	    eventual.events = new eventual.Events();
	    var self=this;
	    var userId=eventual.auth.getData("user").replace(/"/g, '');
	    eventual.events.fetch({
	    	//solo mostramos los eventos en los que participa el usuario logado
		    //por eso pasamos la url que filtra por usuario
	    	url:"/by-user/"+userId,
            success: (function (data) {
            	console.log('success');
            	self.loadView(new eventual.EventsSearchView({collection: eventual.events,baseUrl: eventual.config.rest.url}));                    
            }),onGenericError:(function(model,xhr,options){})
        });
	},
	
	/*Mostrar evento */
	showEvent : function(eventId) {
		
		console.log('haschange eventDetail');
		Backbone.Relational.store.reset()
	    eventual.event  =  eventual.Event.findOrCreate(eventId);
	    if (!eventual.event) {
			eventual.event = new eventual.Event({id:eventId});
	    }
	    var self=this;
	    eventual.event.fetch({
            success: (function (data) {
            	self.loadView(new eventual.EventDetailsView({model: eventual.event,baseUrl: eventual.config.rest.url}));                    
            })
        });
	},
	
	/*Añadir usuario a un evento (listado de usuarios)*/
	users2join : function(eventId) {
		
		console.log('haschange join');
	    eventual.users = new eventual.Users();
	    var self=this;
	    eventual.users.fetch({
	    	//solo mostramos los usuarios que no están ya partipando en este evento
	    	url:"/nin/"+eventId,
			success: (function (data) {
			    console.log('success');
            	self.loadView(new eventual.UsersSearchView({collection: eventual.users,ownerId:eventId,baseUrl: eventual.config.rest.url}));                    
            })
        });
	},
	
	/*Enviar una imagen a un evento*/
	postImage : function(eventId){
		
	    console.log('haschange postImage');
	    eventual.post = new eventual.Post({eventId:eventId});
	    var userId=eventual.auth.getData("user").replace(/"/g, '');
	   	this.loadView(new eventual.PostImageView({model: eventual.post,userId:userId}));                    
	},	
	
	/*Cambiar la imagen del evento*/
	addImage : function(entity,entityId){
		this.loadView(new eventual.ImageForm({entity:entity,entityId:entityId}));
	},	
	
	/*Listado de posts de un evento*/
	posts : function(eventId) {
	    
	    eventual.posts = new eventual.Posts({eventId:eventId});
	    var userId=eventual.auth.getData("user").replace(/"/g, '');
	    var self=this;
	    eventual.posts.fetch({
	    	success: (function (data) {
	    		self.loadView(new eventual.PostsView({userId:userId,collection:eventual.posts}));              
			})
	    });
    },
    
    /*Listado de imagenes de un evento*/
	images : function(eventId) {
	    
	    eventual.images = new eventual.Images({eventId:eventId});
	    var self=this;
	    eventual.images.fetch({
	    	success: (function (data) {
	    		self.loadView(new eventual.ImagesView({collection:eventual.images})); 
	    	})
	    });
    },
    
    /*Detalle de un post concreto de un evento*/
    comments : function(eventId,postId) {
    	
		var userId=eventual.auth.getData("user").replace(/"/g, '');
	    eventual.post = new eventual.Post({eventId:eventId,id:postId});
	    var self=this;
	    eventual.post.fetch({
            success: (function (data) {
            	self.loadView(new eventual.Comments({userId:userId,model: eventual.post}));    
	        })
	    });
	},
	
	/*Edición de usuario*/
	editUser : function(userId) {
		
		var userId=eventual.auth.getData("user").replace(/"/g, '');
	    eventual.user  =  eventual.User.findOrCreate(userId);
		if (!eventual.user) {
		    eventual.user = new eventual.User({id:userId});
		}
		
		var self=this;
	    eventual.user.fetch({
            
	    	success: (function (data) {
            	self.loadView(new eventual.UserFormView({model: eventual.user}));                    
            })
        });
    },
    //seguridad
    showLogin : function(){
    
    	var that=this;
    	// itentamos autologarnos a partir de los credenciales almacenados en el dispositivo
		eventual.utils.autoLogin(function(autoLogin){
		    
			if (autoLogin){
		    	//redirigmos a la página a la que queriamos ir
				if(eventual.auth.getData('redirectFrom')){
	            	var path = eventual.auth.getData('redirectFrom');
	            	eventual.auth.unset('redirectFrom');
	                Backbone.history.navigate(path, { trigger : true });
	            }else{
	            	//sino a la pagina principal de la aplicación
	            	Backbone.history.navigate('events', { trigger : true });
	            }               
			}else{
				//no hay credenciales, vamos a la página de login
	        	var user = new eventual.User();
	        	that.loadView(new eventual.LoginView({model: eventual.auth}));
			}
		});
    },
    
    /*Perfil de usuario*/
    
    showProfile : function() {
		
    	//no hay credenciales de usuario correctas
    	if (!eventual.auth.getData("user")){
    		eventual.utils.redirectToLogin('profile');
    		return;
    	}
    	
    	//obtenemos el id delusuario logado del objeto auth
    	var userId=eventual.auth.getData("user").replace(/"/g, '');
    	eventual.user  =  eventual.User.findOrCreate(userId);
    	
    	if (!eventual.user) {
		    eventual.user = new eventual.User({id:userId});
		}
    	
    	var self=this;
		eventual.utils.customSync({url:eventual.user.urlRoot+"/"+userId+"/exists",method:'GET',data:null},
	    	function(data) { 
				eventual.user.fetch({
				    success: (function (data) {
						console.log('success');
						self.loadView(new eventual.UserDetailsView({model: eventual.user,baseUrl: eventual.config.rest.url}));                    
				   	})
			    })
			},null,"profile");
		
	},
	
	/*Creación de usuario*/
	
	createUser: function(){
		var user = new eventual.User();
		this.loadView(new eventual.UserFormView({model: user}));
	},
	
	promo : function() {
		var userId=eventual.auth.getData("user").replace(/"/g, '');
    	var promo = new eventual.Promo();
       	this.loadView(new eventual.PromoFormView({model: promo,userId:userId}));
	},
	
    //metodos para cargar las vistas mediante backbone, no se corresponden a vistas reales
    //sino que son objetos genéricos para la navegación entre vistas.
    loadView: function(view) {
	    
		if (this.currentView) {
			console.log('Detach the old view');
            this.currentView.close();
        }
	    console.log('Render view after it is in the DOM (styles are applied)');
	    this.currentView = view;
	    this.currentView.render();
	    $("#backbone-dynamic-content").html(this.currentView.el);
	    $("#backbone-dynamic-content").trigger('pagecreate');
	    $("#backbone-dynamic-content").krioImageLoader();
	    //dejamos de mostrar el mensaje de "Cargando.."
	  	eventual.utils.hideLoading();
	},
	
	//mostramos la vista solicitada en un dialogo
	loadDialog: function(view) {
	    
	    if (this.currentDialog) {
			console.log('Detach the old dialog view');
            this.currentDialog.close();
        }
	    console.log('Render view after it is in the DOM (styles are applied)');
	    this.currentDialog = view;
	    this.currentDialog.render();
	    $("#backbone-dynamic-dialog-content").html(this.currentDialog.el);
	    $("#backbone-dynamic-dialog-content").trigger("create");
	    $.mobile.changePage('#backbone-dynamic-dialog', { transition: "pop", role: "dialog"} );
	   
	},
	
	//mostramos la vista previamente cargada. Lo utilizamos al cerrar o aceptar un dialogo
	recoverView: function() {
	   console.log('Reload view');
	   this.loadView(this.currentView);
	   this.currentView.delegateEvents();
	},
	
	//mostramos la página anterio al cerrar un dialogo
	closeDialog:function(){
	   $('#backbone-dynamic-dialog').dialog('close');
	   window.history.back();
	   $.mobile.changePage("#backbone-dynamic-content", {changeHash:false, transition: "slide"} );
	}
});
