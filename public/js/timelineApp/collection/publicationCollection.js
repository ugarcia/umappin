var timelineApp = timelineApp || {};

(function(){
	var PublicationCollection =Backbone.Collection.extend({
		url:'/publications',
		
		comparator: function(collection){
		    return(collection.get('timeStamp'));
		}
	});
	
	// Create our global collection of **Publications**.
	timelineApp.PublicationCollection = new PublicationCollection();
}());

