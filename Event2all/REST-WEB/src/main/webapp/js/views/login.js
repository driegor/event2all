eventual.LoginView = eventual.BaseView.extend({
    baseUrl:null,
    formData:null,
    initialize: function (options) {
       this.baseUrl=options.baseUrl;
    },
    render: function () {
        $(this.el).html(this.template({"baseUrl":this.baseUrl,"user":this.model.toJSON()}));
        return this;
    },
    events: {
        "change"        : "change",
        "click .save"   : "beforeSave",
        "click .cancel" : "cancel"
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
    beforeSave: function () {
		
	    var self = this;
	    var check = this.model.validateAll();
        if (check.isValid === false) {
        	eventual.utils.displayValidationErrors(check.messages);
            return false;
        }
        this.save();
        
        return false;
    },save: function(ev){
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
		            	Backbone.history.navigate('', { trigger : true });
		            }
    				eventual.utils.success("Bienvenido","Bienvenido de nuevo");
    			}
    		,function(error) { 
    			eventual.utils.clearCredentials();
    			eventual.utils.showAlert('Login incorrecto!!', 'Los datos introducidos no son válidos.', 'alert-error');
    		}
    	); 	
    	return false;
	}, 
    cancel: function () {
        window.history.back();
        return false;
    }
})
  