/*global Backbone*/
var timelineApp = timelineApp || {};

timelineApp.Router = Backbone.SubRoute.extend({

	routes: {
		'comment/:id' : 		'comments',
		'news' :			    'news',
		'/'	: 				    'publicationHeaders',
		'user/:id' :		  	'userWall'
	},

	comments: function(idComment) {
		this.loadTemplateIfNeed(function() {
			requirejs(['/assets/js/timelineApp/model/publication.js'], function() {
				requirejs(['/assets/js/timelineApp/model/comment.js'], function() {
					requirejs(['/assets/js/timelineApp/view/publicationComments_view.js'], function() {
						publication = new timelineApp.Publication({id:idComment});
						console.log(idComment);
						publication.fetch({
							success: function() {
								$('#new_post').hide();
								view = new timelineApp.CommentsView({ model: publication });
								$('#comments_body').html(view.addTimeAgoAndRender().el);
							},
							error: function() {
								console.log("Error getting comments from server");
							}
						});
					});
				});
			});
		});
	},

	news: function() {
		//this.loadTemplateIfNeed(function() {
		that = this;
		setTemplate ("/assets/templates/activity.html", function() {
			requirejs(['/assets/js/timelineApp/model/publication.js'], function() {
				requirejs(['/assets/js/timelineApp/model/comment.js'], function() {
					requirejs(['/assets/js/timelineApp/collection/publicationCollection.js'], function() {
						requirejs(['/assets/js/timelineApp/collection/newsCollection.js'], function() {
							requirejs(['/assets/js/timelineApp/view/receivedNews_view.js'], function() {
								requirejs(['/assets/js/timelineApp/view/commentsApp_view.js'], function() {
									requirejs(['/assets/js/timelineApp/view/newsApp_view.js'], function() {
										console.log("routing received");
										// Create our global collection of **Publications**.
										that.cleanUpViews();
										timelineApp.newsAppView =  new timelineApp.NewsAppView();
										timelineApp.NewsCollection.reset();
										timelineApp.NewsCollection.fetch();
									});
								});
							});
						});
					});
				});
			});
		});
	},

	publicationHeaders: function() {
		//this.loadTemplateIfNeed(function() {
		that = this;
		setTemplate ("/assets/templates/wall.html", function() {
			requirejs(['/assets/js/timelineApp/model/publication.js'], function() {
				requirejs(['/assets/js/timelineApp/model/comment.js'], function() {
					requirejs(['/assets/js/timelineApp/collection/publicationCollection.js'], function() {
						requirejs(['/assets/js/timelineApp/collection/newsCollection.js'], function() {
							requirejs(['/assets/js/timelineApp/view/receivedComments_view.js'], function() {
								requirejs(['/assets/js/timelineApp/view/commentsApp_view.js'], function() {
									requirejs(['/assets/js/timelineApp/view/publicationComments_view.js'], function() {
										console.log("routing received");
										// Create our global collection of **Publications**.
										that.cleanUpViews();
										$('#new_post').show();
										$('#user_wall').hide();
										timelineApp.appView =  new timelineApp.AppView();
										timelineApp.PublicationCollection.reset();
										timelineApp.PublicationCollection.fetch();
									});
								});
							});
						});
					});
				});
			});
		});
	},

	userWall: function(idUser){
		//this.loadTemplateIfNeed(function() {
		that = this;
		setTemplate ("/assets/templates/wall.html", function() {
			requirejs(['/assets/js/timelineApp/model/publication.js'], function() {
				requirejs(['/assets/js/timelineApp/model/userPublication.js'], function() {
					requirejs(['/assets/js/timelineApp/model/comment.js'], function() {
						requirejs(['/assets/js/timelineApp/collection/userPublicationCollection.js'], function() {
							requirejs(['/assets/js/timelineApp/collection/newsCollection.js'], function() {
								requirejs(['/assets/js/timelineApp/view/receivedComments_view.js'], function() {
									requirejs(['/assets/js/timelineApp/view/userCommentsApp_view.js'], function() {
										requirejs(['/assets/js/timelineApp/view/publicationComments_view.js'], function() {
											requirejs(['/assets/js/account/models/user_model.js'], function() {
												requirejs(['/assets/js/account/models/follow_model.js'], function() {
													requirejs(['/assets/js/account/models/usrfollows_model.js'], function() {
														requirejs(['/assets/js/account/models/usrfollowed_model.js'], function() {
															requirejs(['/assets/js/account/views/user_follows_view.js'], function() {
																var searchedUser = new Account.User({id: idUser})
																searchedUser.fetch({
																	complete: function() {
																		var img = searchedUser.get('profilePicture');
																		var profileImg = (img != null) ? './photos/'+ img +'/content' : './assets/img/140x140.gif';
																		$('#user-wall-picture').html('<img id="user-wall-avatar" src="'+profileImg+'" onload="resize(this,128)">');
																		$('#user-wall-nick h3').text(searchedUser.get('name'));
																	}
																});
																var follows = new Account.UsrFollows({id: Account.session.get('id')});
																follows.fetch({
																	complete: function(data) {
																		var followed = new Account.UsrFollowed({id: idUser});
																		followed.fetch({
																			complete: function(data) {
																				var followsView = new Account.UserFollowsView({
																					model: searchedUser,
																					follows: follows,
																					followed: followed
																				});
																				$('#user-wall-follow').html(followsView.render().el);
																			}
																		});
																	}
																});


																/*
                      var userFollowProfile = $.get('/userfollowed/'+idUser),
                          follow = idUser;
                      userFollowProfile.done(function( data ) {
                          var currentUser = JSON.parse(sessionStorage.getItem('user')),
                              exist = false;

                          for (var i = 0; i < data.follow.length; i++) {
                            var userfollow = data.follow[i];
                            if (currentUser.id === userfollow) {
                              exist = true;
                            }
                          }

                          if (!exist) {
                            $('#user-wall-follow').html('<button id="user-follow" class="btn btn-primary">Follow</button>');
                            sessionStorage.setItem('wall-follow-user',follow);
                          } else {
                            $('#user-wall-follow').html('<button id="user-unfollow" class="btn btn-primary">Unfollow</button>');
                            sessionStorage.setItem('wall-follow-user',follow);
                          }
                      });
                      userFollowProfile.error(function( data ) {
                          $('#user-wall-follow').html('<button id="user-follow" class="btn btn-primary">Follow</button>');
                            sessionStorage.setItem('wall-follow-user',follow);
                      });
                      console.log("User " + idUser + " Wall");
																 */
																that.cleanUpViews();
																timelineApp.userAppView = new timelineApp.UserAppView();
																timelineApp.UserPublicationCollection.url = "/userpublications/" + idUser;
																timelineApp.UserPublicationCollection.reset();
																timelineApp.UserPublicationCollection.fetch();
															});
														});
													});
												});
											});
										});
									});
								});
							});
						});
					});
				});
			});
		});
	},

	loadTemplateIfNeed:function(callback){
		//it reloads the template only if not set
		if($('#comments_body').length ===0){
			console.log("Load Publications template");
			setTemplate ("/assets/templates/wall.html", callback);
		}else{
			callback();
		}
	},

	loadNewsTemplateIfNeed:function(callback){
		//it reloads the template only if not set
		if($('#news_body').length ===0){
			console.log("Load News template");
			setTemplate ("/assets/templates/activity.html", callback);
		}else{
			callback();
		}
	},

	loadUserTemplateIfNeed:function(callback){
		//it reloads the template only if not set
		if($('#userNews_body').length ===0){
			console.log("Load User Activity template");
			setTemplate ("/assets/templates/userActivity.html", callback);
		}else{
			callback();
		}
	},

	cleanUpViews: function() {
		if (timelineApp.appView && timelineApp.appView != undefined) {
			console.log(timelineApp.appView);
			timelineApp.appView.unbind();
			timelineApp.appView.remove();
		}
		if (timelineApp.userAppView && timelineApp.userAppView != undefined) {
			console.log(timelineApp.userAppView);
			timelineApp.userAppView.unbind();
			timelineApp.userAppView.remove();
		}
	}

});
