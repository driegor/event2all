eventual.PostListView = eventual.BaseView.extend({
    baseUrl:null,
    images:null,
    active:false,
    initialize: function (options) {
		this.baseUrl=options.baseUrl;
	},
    render: function () {
    	//$('#footer').hide();	
        this.images=new Array();
        
        for (var i=0;i<this.model.models.length;i++){
            var post=this.model.models[i];
        	if (post.get("content").images){
        		var imageArray=post.get("content").images;
        		for (var j=0;j<imageArray.length;j++){
        			var item={};
        			item.image=this.baseUrl+"/"+imageArray[j].files[2].url;
        			item.medium=this.baseUrl+"/"+imageArray[j].files[1].url;
        			item.thumbnail=this.baseUrl+"/"+imageArray[j].files[0].url;
        			item.id=post.id;
        			item.userName=post.get("user").name;
					item.title=post.get("content").description;
					item.content=post.get("content");
					item.posTime=post.get("posTime");
					this.images.push(item);
				}
        	}
        };
        $(this.el).html(this.template({"baseUrl":this.baseUrl,"posts":this.images}));
        $(this.el).krioImageLoader();
        
        $("a.grouped_elements").fancybox({        
            loop    			: true,
            autoPlay          	: true,
            playSpeed	        : 4000,
            nextSpeed  	    : 500,
            prevSpeed      	: 500,
            openSpeed          : 500,
            speedOut           : 500,
            autoScale			: false,
            fitToView: false,
    		 nextEffect        	: 'elastic',
            prevEffect        	: 'elastic',
            width    : "70%",
            height   : "90%",
            beforeShow: function(){
           	    //transparent background
           	    $(".fancybox-skin").css("background","transparent");

           	    //remove dropshadow
           	    $(".fancybox-skin").css("-webkit-box-shadow","0 0 0 rgba(0, 0, 0, 0)");
           	    $(".fancybox-skin").css("-moz-box-shadow","0 0 0 rgba(0, 0, 0, 0)");
           	    $(".fancybox-skin").css("box-shadow","0 0 0 rgba(0, 0, 0, 0)");
           	},onComplete: function(links, index) {
           	    //leave this at the bottom
           	$.fancybox.center(true);
           	}
        });
        
        return this;
    },
    events: {
		"click .thumbnail img"	:"showComments",
		"click  #play-button"	:"stopAndPlay"
    },
    showComments:function(evt){
    	var postId=evt.currentTarget.id;
    	$.fancybox({
            'content' : $("#comments_"+postId).html(),
            fitToView	: true,
    		autoSize	: false,
    		closeClick	: false,
    		openEffect	: 'none',
    		closeEffect	: 'none'
        });
    },
   
    stopAndPlay:function(){
    	$("a.grouped_elements").trigger('click');
    	
    },
    
    onClose : function(){
    	
    }
    
});
