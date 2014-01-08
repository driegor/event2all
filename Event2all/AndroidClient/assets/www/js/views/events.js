
/* 
 * 
 * LISTADO DE EVENTOS
 * 
 */


eventual.EventsSearchView = eventual.BaseView.extend({
   baseUrl:null,
   initialize: function(options) {
    	this.template = _.template($('#events-search-template').html());
       	this.baseUrl=options.baseUrl;
	},
	render: function() {
	 	$(this.el).html(this.template( { "events": this.collection.toJSON(),"baseUrl":this.baseUrl}));
	 	return this;
    },
    changeItem: function(){
        this.collection.sort();
    }
});

/* 
 * 
 * DETALLE DEL EVENTO
 * 
 */

eventual.EventDetailsView =  eventual.BaseView.extend({
	transition:"flip",
	baseUrl:null,
    attributes: {'data-add-back-btn': 'true'},
	initialize: function(options) {
    	this.template = _.template($('#event-details-template').html());
    	this.baseUrl=options.baseUrl;
	},
    render: function() {
		$(this.el).html(this.template({"event":this.model.toJSON(),"baseUrl":this.baseUrl}));
		return this;
	},
	events: {
		"click #back"				: "back",
		"click #showDeleteDialog"	: "showDeleteDialog",
		"click #nothing"			: "preventDefault"
    },
    showDeleteDialog: function(ev){
    	var userId=$(ev.currentTarget).data("id");
    	var user = this.model.get("users").findWhere({id:userId});
    	eventual.app.router.loadDialog(new eventual.DeleteUserView({model: user,ownerId:this.model.id}));
	},
	preventDefault: function(ev){
		ev.preventDefault();
	    return false;
	}
});


/* 
 * 
 * CREAR/EDITAR EVENTO
 * 
 */

eventual.EventFormView =  eventual.BaseView.extend({
	initialize: function() {
		this.template = _.template($('#event-form-template').html());
	},
	
	events: {
        "change"     : "change",
        "click #save": "beforeSave",
	    "click #back": "back",
	},
    save: function(ev){
	 	var eventId =this.model.id,
	    event=this.model,
	    formJSON = $('#form').formParams();
	    
	    if (eventId){
	    	formJSON["id"]=eventId;
		
	    	//update event
	    	eventual.utils.customSync({url:eventual.event.url(),method:'PUT',data:formJSON},
	    		function(data) { 
					console.log("update event");
					window.history.back();
	    		},null,
	    		'events/add'); 
	    
	    }else{
	    	
	    	event = new eventual.Event(formJSON);
	        //asignaremos como creador del evento al usuario logado
	        event.set({ownerId:eventual.auth.getData("user").replace(/"/g, '')});
	        
	        event.save({},{
				success:function(event){
					window.history.back();
				}
			});
		}
	    return false;
	}, 
	        
	render: function() {
	    var event = this.model;
		$(this.el).html(this.template(this.model.toJSON()));
		return this;
    }
});


/* 
 * 
 * NUEVA PROMOCIÓN DE EVENTO
 * 
 */
eventual.PromoFormView =  eventual.BaseView.extend({
	userId:null,
	initialize: function(options) {
		this.template = _.template($('#add-promo-form-template').html());
		this.userId=options.userId;
	},
	events: {
        "change"     : "change",
        "click #save": "beforeSave",
	    "click #back": "back",
	},
    save: function(ev){
	 	var formJSON = $('#form').formParams();

	 	//usamos la invitación
	 	
    	eventual.utils.customSync({url:this.model.url()+'/'+formJSON['token'],method:'PUT',data:{ids:[this.userId]}},
    		function(data) { 
				if (data===true){
					window.history.back();
				}else{
					alert('Token no valido listillo de los cojones');
				}
    		},null,
    		'promo'); 
	 	
	    return false;
	}, 
	render: function() {
	    var event = this.model;
		$(this.el).html(this.template(this.model.toJSON()));
		return this;
    }
});

	
   