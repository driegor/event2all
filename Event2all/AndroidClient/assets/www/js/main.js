$(document).ready(function () {

	console.log('document ready');
	eventual.token=new Date();
    //iniciamos la aplicación
    eventual.app = new eventual.App();
    eventual.app.start(); 

});

