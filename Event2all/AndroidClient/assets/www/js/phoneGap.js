//métodos nativos del móvil
eventual.phoneGapUtils = {

   
  //phoneGapMethods

    // Llama a onDeviceReady cuando PhoneGap se inicie.
	// Cuando PhoneGap esté listo y se comunique con el dispositivo 
	// se lanzara el evento `deviceready`.
	// 
	init: function (){
	    document.addEventListener("deviceready", eventual.utils.onDeviceReady, false);
	},

	// PhoneGap esta listo y ahora ya se pueden hacer llamadas a PhoneGap
	//
	onDeviceReady:function () {
	    console.log("phonegap cargado y listo para usar");
	},

	// Se ejecuta al llamar a la captura
	//
	captureSuccess:function (mediaFiles) {    
	    uploadFile(mediaFiles[0]);
	},
	
	// La captura falla
	//
	captureError:function (error) {
	    var msg = 'An error occurred during capture: ' + error.code;
	    navigator.notification.alert(msg, null, 'Uh oh!');
	},
	
	// Iniciamos la cámara
	//
	captureImage:function () {
	    // Lanzamos la ejecución de la camara
	    // Sólo permitimos capturar una imagen, cada vez
	    navigator.device.capture.captureImage(eventual.utils.CaptureSuccess, eventual.utils.CaptureError, { limit: 1 });
	},
	// Subimos el fichero al servidor
	uploadFile:function (params,onSuccess,onError,redirectionPostLogin) {
		
		var that=this;
 		var retryFunction = function (params,onSuccess,onError) {
 			eventual.phoneGapUtils.uploadFile(params,onSuccess,onError,null);
 		}
		
 	    var ft = new FileTransfer(),options = new FileUploadOptions();
 	    options.fileKey  = "fileData";
        options.fileName = "file.jpg";
        options.mimeType = "image/jpeg";
        options.chunkedMode = false;
        options.headers = { "Connection":"close",'X-CSRF-Token' : eventual.token};

        if (params.comments){
            options.params = {"comments": params.comments};
        }
        
        //progreso de subida
        ft.onprogress = function(progressEvent) {
        	if (progressEvent.lengthComputable) {
        		//mostramos un mensaje con el porcentaje de la imagen subida
			    var perc = Math.floor(progressEvent.loaded / progressEvent.total * 100);
			    eventual.utils.showLoading("Subiendo imagen...",perc);
			}
        	if (perc == 99){
        		//empezamos las distintas transformaciones en función del tipo de imagen subida (thumnbail, detalle, grande)
			    eventual.utils.showLoading("Procesando imagen...");
			}
		};

        // subimos la imagen..
	    ft.upload(params.imageURI, params.url,
	        //función éxito, la subida del fichero ha funcionado 
	        //el parámetro padado es FileUploadResult, que contiente la información de subida del fichero
	        //nos interesa pasar el objeto json devuelto  a la función onSucces
		    function(r) {
	    		if (onSuccess && typeof(onSuccess) === "function") {  
	    			console.log('call custom success');
	    			onSuccess(JSON.parse(r.response));  
	    		}   
	        },
	        function(f) {
	        	//hay una función callback error definida, la ejecutamos
				if (onError && typeof(onError) === "function") {  
					console.log('call custom error');
					onError(f.http_status,f.code);  
				}
				//no hay función personalizada de error, ejecutamos una genérica
				else{
					eventual.utils.handleGenericErrorCustomSync(f.http_status,"uploadFile",retryFunction,params,onSuccess,redirectionPostLogin);
				}
	        },options
		);
	}
};



 
