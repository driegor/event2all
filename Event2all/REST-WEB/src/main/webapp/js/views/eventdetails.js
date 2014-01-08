eventual.EventView = eventual.BaseView.extend({
    baseUrl:null,
    imageChanged:null,
    formData:null,
    initialize: function (options) {
       this.baseUrl=options.baseUrl;
       this.model.on("checkUploadPendingFile", this.checkUploadPendingFile,this);
    },
    render: function () {
    	$(this.el).html(this.template({"baseUrl":this.baseUrl,"event":this.model.toJSON()}));
    
    	$("#mainImgUrl").fancybox({
    		fitToView	: true,
    		autoSize	: false,
    		closeClick	: false,
    		openEffect	: 'none',
    		closeEffect	: 'none'
    	});
    	
    	return this;
    },
    events: {
        "change"        : "change",
        "click .save"   : "beforeSave",
        "click .cancel" : "cancel",
        "click #genToken"  : "genToken",
        "change #fileToUpload":"fileSelect",
    },
    change: function (event) {
        // borramos las alertas previas
    	eventual.utils.hideAlert();

        // actualizamos el model
        var target = event.target;
        var change = {};
        change[target.name] = target.value;
        this.model.set(change);

       
        var check = this.model.validateItem(target.id);
        if (check.isValid === false) {
        	eventual.utils.addValidationError(target.id, check.message);
        } else {
        	eventual.utils.removeValidationError(target.id);
        }
    },
	fileSelect: function(evt){
	 	console.log('se lanza el evento fileSelect')
	     if (window.File && window.FileReader && window.FileList && window.Blob) {
	        var files = evt.target.files;
	 		this.formData = new FormData();
	        for (var i = 0; file = files[i]; i++) {
	        	console.log('itero ficheros')
	            
	        	// si no es una imagen continuamos
	            if (!file.type.match('image.*')) {
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
	                    $('#mainImgThumb').attr('src', evt.target.result);
	                	$('#mainImgUrl').attr('href', evt.target.result);
	                };
	            }(file));
	            reader.readAsDataURL(file);
	            this.imageChanged=true;
	        }
	     } else {
	        console.log('The File APIs are not fully supported in this browser.');
	    }
	}, 
    beforeSave: function () {
		
	    var self = this;
	    var check = this.model.validateAll();
        if (check.isValid === false) {
        	eventual.utils.displayValidationErrors(check.messages);
            return false;
        }
        this.saveEvent();
        
        return false;
    },
    saveEvent: function () {
    	var formJSON = $('#form').formParams();
    	var that=this;
    	if (this.model.id){
        	//update event
        	eventual.utils.customSync({url:this.model.url(),method:'PUT',data:formJSON},
        		function(data) { 
    				console.log("update event");
    				that.model.trigger('checkUploadPendingFile');
        		},null,''); 
        }else{
        	this.model.set({ownerId:eventual.auth.getData("user").replace(/"/g, '')});
         	this.model.save({}, {
         		success: function (model,resp, options) {
         			 console.log(model);
             		//una vez actualizado el modelo, comprobamos si hay alguna
             		//foto por subir
             		model.trigger('checkUploadPendingFile');
         		}
 	        });
         }
    },
    cancel: function () {
        window.history.back();
        return false;
    },
    checkUploadPendingFile:function(){
		
		//solo subo una imagen , si ésta es nueva
		var that=this;
		
		if (this.imageChanged) {
			$('#loading-indicator').show();
			eventual.utils.uploadFile({url:this.model.url()+"/images",data:this.formData},function(data) { 
				that.model.set("image",data);
			    Backbone.history.navigate('events/' + that.model.id, { trigger : true });
				eventual.utils.showAlert('Ok!', 'Los datos del evento se han guardado correctamente.', 'alert-success');
				$('#loading-indicator').hide();
				},null,null);
		}else{
			Backbone.history.navigate('events/' + this.model.id, { trigger : true });
    		eventual.utils.showAlert('Ok!', 'Los datos del evento se han guardado correctamente.', 'alert-success');
    	}
	},
	genToken:function(){
		var self=this;
		eventual.utils.customSync({url:this.model.urlRoot+"/token",method:'GET',data:null},
        		function(data) { 
					self.model.set("token",data.token);
					$('#token').val(data.token);
        		},null,''); 
		return false;
	}
})
    