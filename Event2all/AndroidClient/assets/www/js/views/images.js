
/* 
 * 
 * POST CON IMAGENES
 * 
 */

eventual.PostImageView =  eventual.BaseView.extend({
	userId:null,
	formData:null,
	ws : null,
	wsURI : eventual.config.ws.url  + '/api/post/realTime',
	initialize: function(options) {
		this.userId=options.userId;
		this.template = _.template($('#post-image-form-template').html());
		this.wsConnect();
	},
	
	events: {
	    "click #back"			: 	"back",
	    "change"     			: 	"change",
	    "click #post"			: 	"beforeSave",
	    "click #pick"			: 	"pick",
	    "change #fileToUpload" 	:	"fileSelect",
	    "click #takePicture"	: 	"takePicture"
	},   
	pick: function(){
	    this.formData = new FormData();
	 	// Abrimos la galeria del móvil (en el emulador navegaremos por la imagenes de la SDCARD)
	   	var self=this;
	    if (eventual.config.mode==="mobile"){
			navigator.camera.getPicture(function (imageURI) {
							            self.formData = {"fileData": imageURI};
							            eventual.utils.previewUpload(imageURI,"filesInfo",self.formData);
							        },
							    	function(message) {},
							                { quality: 50,destinationType: navigator.camera.DestinationType.FILE_URI,
							                   sourceType: navigator.camera.PictureSourceType.PHOTOLIBRARY });
		}
		return false;
	}, 
	//cuando estemos en modo mobile, usaremos esta función, tirando de phoneGap
    takePicture : function (e) {
    	 if (eventual.config.mode==="mobile"){
             var self=this;
    	     var options = {
                quality: 50,
                targetWidth: 1000,
                targetHeight: 1000,
                destinationType: Camera.DestinationType.FILE_URI,
                encodingType: Camera.EncodingType.JPEG,
                sourceType: Camera.PictureSourceType.CAMERA
            };

            navigator.camera.getPicture(
                function (imageURI) {
                    console.log(imageURI);
                    self.formData = {"fileData": imageURI};
					eventual.utils.previewUpload(imageURI,"filesInfo",self.formData);
                },
                function (message) {
                    //el usuario no hace finalmente la foto y cancela
                }, 
            options);
        };
        return false;
    },
    //cuando estemos en modo navegador, usaremos esta función, tirando del input file
	fileSelect: function(evt){
	 	 console.log('se lanza el evento fileSelect')
	     if (window.File && window.FileReader && window.FileList && window.Blob) {
	        var files = evt.target.files;
	 		this.formData = new FormData();
	        for (var i = 0; file = files[i]; i++) {
	        	console.log('itero ficheros')
	            
	        	// if the file is not an image, continue
	            if (!file.type.match('image.*')) {
	            	console.log('no es una imagen ??');
	            	alert(file.type);
	            	console.log(file.type);
	                continue;
	            }
	            
	            console.log('son imagenes')
	           	this.formData.append('fileData', files[i]);
                
	           	reader = new FileReader();
                console.log('file Reader')
	            reader.onload = (function (tFile) {
	                return function (evt) {
	                	console.log('Append image')
	                    $('#filesInfo').html('<img style="width: 150px;" src="' + evt.target.result + '" />');
	                };
	            }(file));
	            reader.readAsDataURL(file);
	        }
	     } else {
	        console.log('The File APIs are not fully supported in this browser.');
	    }
	}, 
	save:function(evt){
	    var post=this.model;
	    var that=this;
	    //estamos en modo emulador 
		if (eventual.config.mode==="mobile"){
			eventual.phoneGapUtils.uploadFile({url:eventual.config.rest.url+"/api/event/"+this.model.eventId+"/posts/user/"+this.userId+"/postImage",
											imageURI:this.formData.fileData,comments:$("#description").val()},
								function(data) { 
										that.ws.send(JSON.stringify({userId : that.userId, eventId : that.model.eventId,message : data.id}));
										window.history.back();
								},null,null);
		}else{
			//estamos en modo navegador 
			this.formData.append("comments",$("#description").val());
			eventual.utils.uploadFile({url:eventual.config.rest.url+"/api/event/"+this.model.eventId+"/posts/user/"+this.userId+"/postImage",data:this.formData},
				function(data) { 
			    	console.log('posted');
			    	that.ws.send(JSON.stringify({userId : that.userId, eventId : that.model.eventId,message : data.id}));
			    	window.history.back();
			    },null,null); 
		}
		return false;
	},
	wsConnect:function() {

       	this.ws = new WebSocket(this.wsURI + '?userId=' + this.userId+'&eventId='+this.model.eventId);
        
       	this.ws.onopen = function () {
            
        };
        
        var that=this;
        
        this.ws.onmessage = function (event) {
        	//en esta pantalla, no haremos nada en caso que nos llegue el mensaje de un nuevo
        	//post, ya lo mostraremos de forma normal, cuando lleguemos al listado
        };
    },
    onClose:function(){
    	this.wsDisconnect();
    },
    wsDisconnect:function() {
        if (this.ws != null) {
            this.ws.close();
            this.ws = null;
        }
    },
	
	render: function() {
	   	$(this.el).html(this.template());
		return this;
    }
});


/* 
 * 
 * FORMULARIO DE NUEVA IMAGEN PARA USUARIO /EVENTO
 * 
 */


eventual.ImageForm =  eventual.BaseView.extend({
	formData:null,
	entity:"",
	entityId:null,
	initialize: function(options) {
		this.template = _.template($('#add-image-form-template').html());
		this.entity=options.entity;
		this.entityId=options.entityId;
	},
	
	events: {
	    "click #back"			: 	"back",
	    "change #fileToUpload"	:	"fileSelect",
	    "click #add" 			: 	"add",
	    "click #pick"			: 	"pick",
	    "click #takePicture"	: 	"takePicture"
	},  
	
	pick: function(){
	    this.formData = new FormData();
	 	// Abrimos la galeria del mobile (en el emulador navegaremos por la imagenes de la SDCARD)
	   	var self=this;
	    if (eventual.config.mode==="mobile"){
			navigator.camera.getPicture(function (imageURI) {
							            self.formData = {"fileData": imageURI};
							            eventual.utils.previewUpload(imageURI,"filesInfo");
							        },
							    	function(message) {},
							                { quality: 50,destinationType: navigator.camera.DestinationType.FILE_URI,
							                   sourceType: navigator.camera.PictureSourceType.PHOTOLIBRARY });
		}
		return false;
	}, 
	//cuando estemos en modo navegador, usaremos esta función, tirando del input file
	fileSelect: function(evt){
	 	 console.log('se lanza el evento fileSelect')
	     if (window.File && window.FileReader && window.FileList && window.Blob) {
	        var files = evt.target.files;
	 		this.formData = new FormData();
	        for (var i = 0; file = files[i]; i++) {
	        	console.log('itero ficheros')
	            
	        	// if the file is not an image, continue
	            if (!file.type.match('image.*')) {
	            	console.log('no es una imagen ??');
	            	alert(file.type);
	            	console.log(file.type);
	                continue;
	            }
	            
	            console.log('son imagenes')
	           	this.formData.append('fileData', files[i]);
                
	           	reader = new FileReader();
                console.log('file Reader')
	            reader.onload = (function (tFile) {
	                return function (evt) {
	                	console.log('Append image')
	                    $('#filesInfo').html('<img style="width: 150px;" src="' + evt.target.result + '" />');
	                };
	            }(file));
	            reader.readAsDataURL(file);
	        }
	     } else {
	        console.log('The File APIs are not fully supported in this browser.');
	    }
	}, 
	//cuando estemos en modo mobile, usaremos esta función, tirando de phoneGap
    takePicture : function (e) {
    	 if (eventual.config.mode==="mobile"){
             var self=this;
    	     var options = {
                quality: 50,
                targetWidth: 1000,
                targetHeight: 1000,
                destinationType: Camera.DestinationType.FILE_URI,
                encodingType: Camera.EncodingType.JPEG,
                sourceType: Camera.PictureSourceType.CAMERA
            };

            navigator.camera.getPicture(
                function (imageURI) {
                    console.log(imageURI);
                    self.formData = {"fileData": imageURI};
					eventual.utils.previewUpload(imageURI,"filesInfo",self.formData);
                },
                function (message) {
                    //el usuario no hace foto y cancela
                }, 
            options);
        };
        return false;
    },
    add:function(evt){
	    var post=this.model;
	  	console.log(this.formData.fileData);
		//estamos en modo emulador 
		if (eventual.config.mode==="mobile"){
			eventual.phoneGapUtils.uploadFile({url:eventual.config.rest.url+"/api/"+this.entity+"/"+this.entityId+"/images",imageURI:this.formData.fileData,comments:null},
					function(data) { 
				 		console.log('posted');
					    window.history.back();
				    },null,null);
		}else{//estamos en modo "navegador"
			this.formData.append("comments",$("#description").val());
		    eventual.utils.uploadFile({url:eventual.config.rest.url+"/api/"+this.entity+"/"+this.entityId+"/images",data:this.formData},
		    		function(data) { 
		   				console.log('posted');
		   				window.history.back();
		             },null,null);
		}
		return false;
	},
	render: function() {
	   	$(this.el).html(this.template());
		return this;
    }
});

/* 
 * 
 * LISTADO DE IMAGENES DE UN EVENTO
 * 
 */

eventual.ImagesView =  eventual.BaseView.extend({
   
   myPhotoSwipe:null,
   eventName:null,
   eventId:null,
   
   initialize: function(options) {
		this.template = _.template($('#images-list-template').html());
    	this.eventId=this.collection.eventId;
    	this.eventName=options.eventName
    	
    },
	events: {
		"click #back"		: "back"
	},   
	render: function() {
	 	$(this.el).html(this.template( { "images": this.collection.toJSON(),"eventId":this.eventId,"eventName":this.eventName,"baseUrl": eventual.config.rest.url}));
	 	
	 	this.myPhotoSwipe = $(this.el).find("#gallery a#link" ).photoSwipe({
			
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
    onClose:function(){
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
    }
 });

  