$(document).ready(function () {
	eventual.utils.loadTemplate(['HeaderView', 'EventView', 'EventListItemView', 'AboutView','PostListView','LoginView'], function() {
		console.log('document ready');
		eventual.token=-1;
	    //iniciamos la aplicación
		eventual.app = new eventual.App();
		eventual.app.start(); 
	});
});


