eventual.EventListView = eventual.BaseView.extend({
    baseUrl:null,
    initialize: function (options) {
    	this.baseUrl=options.baseUrl; 
    },

    render: function () {
        var events = this.model.models;
        var len = events.length;
        var startPos = (this.options.page - 1) * 8;
        var endPos = Math.min(startPos + 8, len);

        $(this.el).html('<ul class="thumbnails"></ul>');

        for (var i = startPos; i < endPos; i++) {
        
            $('.thumbnails', this.el).append(new eventual.EventListItemView({model: events[i],baseUrl:this.baseUrl}).render().el);
          
        }
        $(this.el).krioImageLoader();
        $("#li p").children().equalHeights();
               
        $(this.el).append(new eventual.Paginator({model: this.model, page: this.options.page}).render().el);

        return this;
    }
});

eventual.EventListItemView = Backbone.View.extend({
    baseUrl:null,
    tagName: "li",
    className: "span3",

    initialize: function (options) {
        this.model.bind("change", this.render, this);
        this.model.bind("destroy", this.close, this);
        this.baseUrl=options.baseUrl;
    },

    render: function () {
        $(this.el).html(this.template({"baseUrl":this.baseUrl,"event":this.model.toJSON()}));
        return this;
    }

});