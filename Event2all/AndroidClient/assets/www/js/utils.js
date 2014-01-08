eventual.utils = {

	// no siempre nos interesa llamar al método sync de backbone, utilizamos
	// está función para
	// sincronizar llamadas personalizadas contra la capa REST
	customSync : function(options, onSuccess, onError, redirectionPostLogin) {

		var that = this;
		var retryFunction = function(options, onSuccess, onError,
				redirectionPostLogin) {
			eventual.utils.customSync(options, onSuccess, onError,
					redirectionPostLogin);
		}

		$.ajax({
			type : options.method,
			url : options.url,
			data : (options.data) ? JSON.stringify(options.data) : null,
			contentType : "application/json; charset=utf-8",
			dataType : "json",
			async : true,
			cache : false,
			success : function(data, status, settings) {
				// se ha sobreescrito el método onSuccess
				if (onSuccess && typeof (onSuccess) === "function") {
					onSuccess(data);
				}
			},
			// ha habido un error al acceder al REST
			error : function(xhr, error) {
				// hay una función callback de error definida, la ejecutamos
				if (onError && typeof (onError) === "function") {
					console.log('call custom error');
					onError(xhr, error);
				}
				// no hay función, ejecutamos una genérica
				else {
					eventual.utils.handleGenericErrorCustomSync(xhr.status,
							"customSync", retryFunction, options, onSuccess,
							redirectionPostLogin);
				}
			}
		});
	},
	// este método lo utilizaremos para subir ficheros al servidor (cuando no
	// estemos en modo phoneGap)
	uploadFile : function(options, onSuccess, onError, redirectionPostLogin) {
		var that = this;
		var retryFunction = function(options, onSuccess, onError,
				redirectionPostLogin) {
			eventual.utils.uploadFile(options, onSuccess, onError,
					redirectionPostLogin);
		}

		$.ajax({
			url : options.url,
			type : 'POST',
			data : options.data,
			processData : false,
			cache : false,
			contentType : false,
			success : function(data, status, settings) {
				// se ha sobreescrito el método onSuccess
				if (onSuccess && typeof (onSuccess) === "function") {
					console.log('call custom success');
					onSuccess(data);
				}
			},
			// ha habido un error al acceder al REST
			error : function(xhr, error) {
				// hay una función callback error definida, la ejecutamos
				if (onError && typeof (onError) === "function") {
					console.log('call custom error');
					onError(xhr, error);
				}
				// no hay función, ejecutamos una genérica
				else {
					eventual.utils.handleGenericErrorCustomSync(xhr.status,
							"uploadFile", retryFunction, options, onSuccess,
							redirectionPostLogin);
				}
			}
		});
	},
	previewUpload : function(imageUri, previewElement) {
		$("#" + previewElement).html(
				'<img style="width: 150px;" src="' + imageUri + '" />');
	},
	displayValidationErrors : function(messages) {
		for ( var key in messages) {
			if (messages.hasOwnProperty(key)) {
				this.addValidationError(key, messages[key]);
			}
		}
		this.showAlert('Los datos introducidos no son correctos, corrígelos e inténtalo de nuevo.');
	},
	addValidationError : function(field, message) {
		$('#' + field + '_error').html(message);
	},
	removeValidationError : function(field) {
		$('#' + field + '_error').html("");
	},
	showAlert : function(text) {
		$('#alert').html(text);
	},

	hideAlert : function() {
		$('#alert').html("");
	},
	hideLoading : function() {
		$.mobile.loading('hide');
	},
	showLoading : function(text, value) {
		$.mobile.loading('show', {
			text : text,
			textVisible : true,
			theme : 'a',
			html : value ? text + "<progress value='" + value + "%' max='100'>"
					+ value + "%</progress>" : ""
		});
	},

	warning : function(line, message) {
		toastr.options = {
			"closeButton" : true,
			"debug" : false,
			"positionClass" : "toast-top-full-width",
			"onclick" : null,
			"showDuration" : "3000",
			"hideDuration" : "1000",
			"timeOut" : "5000",
			"extendedTimeOut" : "1000",
			"showEasing" : "linear",
			"hideEasing" : "linear",
			"showMethod" : "fadeIn",
			"hideMethod" : "fadeOut"
		}
		toastr['warning'](message, line);
	},

	success : function(line, message) {
		toastr.options = {
			"closeButton" : true,
			"debug" : false,
			"positionClass" : "toast-top-full-width",
			"onclick" : null,
			"showDuration" : "3000",
			"hideDuration" : "1000",
			"timeOut" : "5000",
			"extendedTimeOut" : "1000",
			"showEasing" : "linear",
			"hideEasing" : "linear",
			"showMethod" : "fadeIn",
			"hideMethod" : "fadeOut"
		}
		toastr['success'](message, line);
	},
	error : function(line, message) {
		toastr.options = {
			"closeButton" : true,
			"debug" : false,
			"positionClass" : "toast-top-full-width",
			"onclick" : null,
			"showDuration" : "3000",
			"hideDuration" : "1000",
			"timeOut" : "5000",
			"extendedTimeOut" : "1000",
			"showEasing" : "linear",
			"hideEasing" : "linear",
			"showMethod" : "fadeIn",
			"hideMethod" : "fadeOut"
		}
		toastr['error'](message, line);
	},

	// método para autenticarnos automáticamente en el servidor, sin tener que
	// introducir manualmente los datos
	// sino mirando en los credenciales almacenadas en el propio móvil
	autoLogin : function(callback) {

		// obtenemos las credenciales del móvil (fileStorage)
		var credentials = eventual.utils.getCredentials();
		if (credentials) {
			// nos logamos contra el sistema con los datos recuperados
			eventual.utils.customSync({
				url : eventual.auth.url() + '/authenticate',
				method : 'POST',
				data : credentials
			}, function(data) {
				// nos hemos podido autentificar con éxito
				eventual.auth.setData('authenticated', true);
				eventual.auth.setData('user', JSON.stringify(data.user));
				callback(true);
			}, function(xhr, error) {
				// las credenciales no son validas
				eventual.utils.clearCredentials();
				callback(false);
			}, null);
		} else {
			callback(false);
		}
	},
	redirectToLogin : function(redirectFrom) {
		eventual.auth.setData('user', null);
		eventual.auth.setData('authenticated', null);
		eventual.auth.setData('redirectFrom', redirectFrom);
		Backbone.history.navigate('login', {
			trigger : true
		});
	},

	// acceso al localStorage
	saveCredentials : function(credentials) {
		if (window.localStorage) {
			localStorage['mail'] = credentials.mail;
			localStorage['password'] = credentials.password;
		}
	},
	clearCredentials : function() {
		if (window.localStorage) {
			localStorage.removeItem('mail');
			localStorage.removeItem('password');
		}
	},
	getCredentials : function() {
		return window.localStorage && localStorage['mail']
				&& localStorage['password'] ? {
			mail : localStorage['mail'],
			password : localStorage['password']
		} : null;
	},

	// metodos de error y éxito
	handleSaveError : function(origin, retryMethod, model, params) {
		// solo ejecutaremos este código, si el modelo no ha implementado su
		// propio método de error
		if (typeof (params.error) === "undefined") {
			params.error = function(model, xhr, options) {
				eventual.utils.handleGenericErrorCustomSync(xhr.status, origin,
						retryMethod, {
							model : model,
							options : params
						});
			};
		}
	},
	// metodos de error y éxito
	handleFetchError : function(origin, retryMethod, params) {
		// solo ejecutaremos este código, si el modelo no ha implementado su
		// propio método de error
		if (typeof (params.error) === "undefined") {
			params.error = function(model, xhr, options) {
				eventual.utils.handleGenericErrorCustomSync(xhr.status, origin,
						retryMethod, {
							options : params
						});
			};
		}

	},
	// metodos de error y éxito
	handleSaveSuccess : function(options) {
		// solo ejecutaremos este código, si el modelo no ha implementado su
		// propio método de éxito
		if (typeof (options.success) === "undefined") {
			options.success = function(response) {
				console.log(options);
				console.log('Saving data...');
				console.log(response);
			};
		}
	},// metodos de error y éxito
	handleFetchSuccess : function(options) {
		// solo ejecutaremos este código, si el modelo no ha implementado su
		// propio método de éxito
		if (typeof (options.success) === "undefined") {
			options.success = function(response) {
				console.log(options);
				console.log('Fetching data...');
				console.log(response);
			};
		}
	},
	// este método se ejecutará siempre que falle una llamada a la rest desde
	// Backbone, mediante
	// una llamada personalizada
	handleGenericErrorCustomSync : function(status, origin, retryMethod,
			params, onSuccess, redirectionPostLogin) {
		// ha caducado el token para nuestra sesión??,
		if (status == 401) {
			// itentamos autologarnos a partir de los credenciales almacenados
			// en el dispositivo
			// si es que estas existen
			eventual.utils
					.autoLogin(function(autoLogin) {
						if (autoLogin) {
							// intentaremos ejecutar otra vez esta misma
							// función, pero ahora con credenciales válidas
							eventual.utils
									.retry(
											origin,
											retryMethod,
											params,
											onSuccess,
											function(model, xhr, error) {
												// si vuelve a fallar por
												// credenciales abortamos la
												// ejecución y vamos a la página
												// de login
												// especfificando donde queremos
												// ir despues
												if (xhr.status == 401) {
													if (params.on401RetryError
															&& typeof (params.on401RetryError) === "function") {
														params.on401RetryError(
																model, xhr,
																error);
													} else {
														// alert("Error de
														// credenciales al
														// reintentar");
													}
												} else {
													// es un error generico no
													// controlado, mostramos un
													// mensaje al usuario, para
													// evitar
													// bucles infinitos
													if (params.onRetryError
															&& typeof (params.onRetryError) === "function") {
														params.onRetryError(
																model, xhr,
																error);
													} else {
														// alert("Error generico
														// al reintentar");
													}
												}
											});
						} else {
							// la aplicación no ha podido autologarse de forma
							// transparente para el usuario,
							// iremos a la pantalla de login, especificando a
							// que página debemos volver, tras logarnos
							if (params.onAutoLoginError
									&& typeof (params.onAutoLoginError) === "function") {
								params.onAutoLoginError(model, xhr, error);
							} else {
								eventual.utils.warning("","No existen datos de usuario, introdúcelos por favor");
								eventual.utils
										.redirectToLogin(redirectionPostLogin);
							}
						}
					});
		} else {
			// es un error generico no controlado, mostramos un mensaje al
			// usuario
			if (params.onGenericError
					&& typeof (params.onGenericError) === "function") {
				params.onGenericError(model, xhr, error);
			} else {
				eventual.utils.error("Error!!!","Se ha producido un error, inténtalo de nuevo.");
			}

		}
	},
	// intentamos ejecutar de nuevo la función
	retry : function(origin, retryMethod, params, onSuccess, onError) {

		// cada tipo de objeto tendrá su forma de llamar al metodo de retry
		// en función de si el método es un fetch, un save, o una customSync

		if (origin === "fetch") {
			// el metodo fetch esperar como parametros sólo el objeto options
			// las funciones de error que queremos sobrescribir, para evitar
			// bucles
			// debe estar en este objeto
			params.options.error = onError;
			retryMethod(params.options);
		} else if (origin === "save") {
			// el metodo save esperar como parametros el objeto model y options,
			// las funciones de error que queremos sobrescribir, para evitar
			// bucles
			// debe estar en el objeto options
			params.options.error = onError;
			retryMethod(params.model, params.options);
		} else if (origin === "customSync" || origin === "uploadFile") {
			// el metodo customSync y uploadFile esperan como paáametros
			// options, options,onSuccess,onError
			retryMethod(params, onSuccess, onError);
		} else {
			// otro caso no contemplado
		}
	}
};
