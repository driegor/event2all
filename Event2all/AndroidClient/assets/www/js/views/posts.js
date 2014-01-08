
/* 
 * 
 * LISTADO DE POSTS DE UN EVENTO
 * 
 */

eventual.PostsView =  eventual.BaseView.extend({
   
   eventName:null,
   eventId:null,
   ws : null,
   wsURI : eventual.config.ws.url  + '/api/post/realTime',
   
   initialize: function(options) {
		this.template = _.template($('#post-list-template').html());
    	this.newItemtemplate = _.template($('#posts-new-post-template').html());
    	this.eventId=this.collection.eventId;
    	this.userId=options.userId;
    	this.eventName=options.eventName;
    	this.wsConnect();
    },
	events: {
		"click #addPost"	: "addPost",
	    "click #back"		: "back",
	    "keyup"     		: "keyup",
	},   
	render: function() {
	 	$(this.el).html(this.template( { "posts": this.collection.toJSON(),"eventId":this.eventId,"eventName":this.eventName,"baseUrl": eventual.config.rest.url}));
	 	return this;
    },
    keyup: function (event) {
    	if ($("#post").val().length==0){
    		$('#addPost').fadeOut("slow");
    	}else{
    		$('#addPost').fadeIn("slow");
    	}
	},	
    addPost: function(ev){
    	var postMessage=$("#post").val();
    	$("#post").val("");
    	var post =new eventual.Post({eventId:this.eventId});
       	
    	var that=this;
    	
    	
    	
    	eventual.utils.customSync({url:post.url()+'/user/'+this.userId,method:'POST',data:postMessage},
    		function(data) { 
    			$('#addPost').fadeOut("slow");
	    		$.scrollTo('0', {onAfter: function() { 
	    			that.collection.push(data);
	    			that.updatePost(data);
		    	}});
	    		if (that.ws!=null){
	    			that.ws.send(JSON.stringify({userId : that.userId, eventId : that.eventId,message : data.id}));
	    		}
	    	},null,'events/'+this.eventId+'/posts'); 	
    	
       	return false;
    },
    //actualizamos la lista de posts, con uno nuevo
    updatePost: function(postMessage){
    	var templateNewItem = _.template($('#posts-new-post-template').html());
    	var html=this.newItemtemplate( { "post": postMessage,"baseUrl": eventual.config.rest.url,"eventId":this.eventId});
      	$("<li>"+html+"</li>").prependTo(".ui-listview").hide().fadeIn('slow');
      	$(".ui-listview").listview("refresh");
      	$("#anchor").html(this.collection.length);
    },
    //establecemos la conexión con el websocket
    wsConnect:function() {

    	this.ws = new WebSocket(this.wsURI + '?userId=' + this.userId+'&eventId='+this.eventId);
    	
    	if (this.ws==null){
    		return false;
    	}
    	
        this.ws.onopen = function () {
        };
        
        var that=this;
        this.ws.onmessage = function (event) {
        	console.log(event.data);
        	var data = JSON.parse(event.data);
           
            if (data.message) {
            	//acabamos de recibir un mensaje con el id de un post nuevo
            	//obtendremos el valor y lo añadiremos a la vista actual
            	
            	eventual.utils.customSync({url:eventual.config.rest.url+"/api/event/"+that.eventId+"/posts/"+data.message,method:'GET',data:null},
                	function(data) { 
                		$.scrollTo('0', {onAfter: function() { 
            	   			that.collection.push(data);
            	   			that.updatePost(data);
            	    	}});
            	},null,null); 	
            }
        };
    },
    //al cerrar la vista, nos desconectamos del webscocket
    onClose:function(){
    	this.wsDisconnect();
    },
    wsDisconnect:function() {
        if (this.ws != null) {
            this.ws.close();
            this.ws = null;
        }
    }
 });

/* 
 * 
 * COMENTARIOS DE UN POST
 * 
 */

eventual.Comments =  eventual.BaseView.extend({
   eventId:null,
   ws : null,
   myPhotoSwipe:null,
   wsURI : eventual.config.ws.url  + '/api/post/comments/realTime',
   initialize: function(options) {
    	this.template = _.template($('#comments-template').html());
    	this.newItemtemplate = _.template($('#comment-new-item-template').html());
    	this.eventId=this.model.eventId;
    	this.userId=options.userId;
    	this.wsConnect();
    },
	events: {
	    "click #addComment"	: "addComment",
	    "click #back"		: "back",
	    "keyup"     		: "keyup",
	},   
	render: function() {
	 	$(this.el).html(this.template( { "post": this.model.toJSON(),"baseUrl": eventual.config.rest.url}));
	 	
	 	this.myPhotoSwipe = $(this.el).find("#gallery a" ).photoSwipe({
			
	 		jQueryMobile: false,
	 	    loop: false,
	 	    enableMouseWheel: false,
	 	    enableKeyboard: false,
	 	    backButtonHideEnabled: false
	 	});
		
		this.myPhotoSwipe.addEventHandler(window.Code.PhotoSwipe.EventTypes.onShow, function(e) {
			$(document).on('click', '#custom-close', function(){    
				e.target.hide();
		    });
		});
	 	
	 	return this;
    },
    keyup: function (event) {
    	if ($("#comments").val().length==0){
    		$('#addComment').fadeOut("slow");
    	}else{
    		$('#addComment').fadeIn("slow");
    	}
	},	
    addComment: function(ev){
    	var comment=$("#comments").val();
    	$("#comments").val("");
    	$('#addComment').fadeOut("slow");
    	var that=this;
    	
    	eventual.utils.customSync({url:this.model.url()+'/user/'+this.userId+'/comment',method:'POST',data:comment},
        	function(data) { 
        		that.model.get("content").comments.push(data);
        		that.updateComments(data);
        		if (that.ws!=null){
        			that.ws.send(JSON.stringify({userId : that.userId, eventId : that.eventId,postId : that.model.id,message : data.id}));
        		}
    		},null,null); 	
     	return false;
    },
    updateComments: function(comment){
    	var html=this.newItemtemplate( { "comment": comment,"baseUrl": eventual.config.rest.url});
      	$("<li>"+html+"</li>").prependTo(".ui-listview").hide().fadeIn('slow');
    	$(".ui-listview").listview("refresh");
    },
    //establecemos la conexión con el websocket
    wsConnect:function() {

    	this.ws = new WebSocket(this.wsURI + '?userId=' + this.userId+'&eventId='+this.eventId+'&postId='+this.model.id);
    	
    	if (this.ws==null){
    		return false;
    	}
    	
        this.ws.onopen = function () {
            
        };
        
        var that=this;
        this.ws.onmessage = function (event) {
        	console.log(event.data);
           	var data = JSON.parse(event.data);
            if (data.message) {
            	//acabamos de recibir un mensaje con el id de un comentario nuevo
            	//obtendremos el valor y lo añadiremos a la vista actual
            	eventual.utils.customSync({url:eventual.config.rest.url+"/api/event/"+that.eventId+"/posts/"+that.model.id+"/comments/"+data.message,method:'GET',data:null},
                	function(data) { 
	            		that.model.get("content").comments.push(data);
	            		that.updateComments(data);
            	},null,null); 	
            }
        };
    },
    //al cerrar la vista, nos desconectamos del webscocket
    onClose:function(){
    	this.wsDisconnect();
    	var photoSwipe = window.Code.PhotoSwipe;
        var photoSwipeInstance = photoSwipe.getInstance(this.myPhotoSwipe.id);
        if (typeof photoSwipeInstance != "undefined" && photoSwipeInstance != null) {
        	
        	try{
        		photoSwipeInstance.hide();
       	  	}catch(err){
       	  		console.log(err.message); 
       	  	}
        	photoSwipe.unsetActivateInstance(photoSwipeInstance);
            photoSwipe.detatch(photoSwipeInstance);
        }
    },
    wsDisconnect:function() {
        if (this.ws != null) {
            this.ws.close();
            this.ws = null;
        }
    }
});
