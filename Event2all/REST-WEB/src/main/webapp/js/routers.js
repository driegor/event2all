eventual.AppRouter=eventual.BaseRouter.extend({

	//metodos para cargar las vistas mediante backbone, no se corresponden a vistas reales
    //sino a objetos genéricos para la navegación entre vistas.
    loadView: function(view) {
	    
		if (this.currentView && this.currentView.close) {
			this.currentView.close();
        }
	    this.currentView = view;
	    this.currentView.render();
	    $("#content").html(this.currentView.el);
	},
	
	
    routes: {
        ""                  	: "list",
        "events/page/:page"		: "list",
        "events/add"        	: "addEvent",
        "events/:id" 			: "eventDetails",
        "events/:id/images"		: "postList",
        "login"					: "login",
        "logout"             	: "logout"
    },

    // rutas que no necesitan autentificación, si no hay un usuario autenticado
    // no redigirá a la página de login
    requresAuth : ['#login','#logout'],

    // rutas que no debería ser accesibles, cuando el usuario esté logado
    preventAccessWhenAuth : ['#login'],
    
    
    
    before : function(params, next){
    	
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
        	Backbone.history.navigate("", { trigger : false });
        }else{
        	//vamos a la ruta específicada
            return next();
        }                        
    },
    
    
    initialize: function () {
        this.headerView = new eventual.HeaderView();
        $('.header').html(this.headerView.el);
    },

	list: function(page) {
        var p = page ? parseInt(page, 10) : 1;
        var eventList = new eventual.EventCollection();
        var that=this;
        var userId=eventual.auth.getData("user").replace(/"/g, '');
        eventList.fetch({
        	success: function(){
        	that.loadView(new eventual.EventListView({model: eventList, page: p,baseUrl: eventual.config.rest.url}));
        }});
        this.headerView.selectMenuItem('home-menu');
    },

    logout: function() {
        
    	eventual.auth.logout(function (response){
    		eventual.utils.clearCredentials();
    		Backbone.history.navigate('login', { trigger : true });
    	});
    },
    
    eventDetails: function (id) {
    
        var event = new eventual.Event({id: id});
        var that=this;
        event.fetch({success: function(){
        	that.loadView(new eventual.EventView({model: event,baseUrl: eventual.config.rest.url}));
        }});
        this.headerView.selectMenuItem();
    },
    
    login: function () {
    	
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
	            	Backbone.history.navigate('', { trigger : true });
	            }               
			}else{
				that.loadView(new eventual.LoginView({model: eventual.auth}));
			}
		});
        this.headerView.selectMenuItem();
    },

    addEvent: function() {
    	console.log("addEvent");
        var event = new eventual.Event();
        var that=this;
        that.loadView(new eventual.EventView({model: event}));
        this.headerView.selectMenuItem('add-menu');
	},
	
	postList: function(eventId,page) {
        var p = page ? parseInt(page, 10) : 1;
        var postList = new eventual.PostCollection({eventId:eventId});
        var that=this;
        postList.fetch({success: function(){
        	that.loadView(new eventual.PostListView({model: postList, page: p,baseUrl: eventual.config.rest.url}));
        }});
        this.headerView.selectMenuItem('home-menu');
    },

    about: function () {
        that.loadView( new eventual.AboutView());
        this.headerView.selectMenuItem('about-menu');
    }

});