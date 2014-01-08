    var eventual = {};
    eventual.config= {
		rest: {
				url: 'http://event2all.herokuapp.com/eventual'
		      },
		ws: {
				url: 'ws://event2all.herokuapp.com/eventual'
			},
		//modo ejecución "chrome", para realizar pruebas directamente en el navegador
		//modo ejecución "mobile", activamos la libreria phoneGap para probar en el emulador /movil real
		mode:'mobile'
		//mode="mobile"
	};
    //formato y textos de las fechas en Español
	moment.lang('es');
	
