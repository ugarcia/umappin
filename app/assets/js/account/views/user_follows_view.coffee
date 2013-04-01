window.Account or= {}

_.templateSettings.variable = 'rc'

class window.Account.UserFollowsView extends Backbone.View

  follows: null
  followed: null
  tagName: 'div'
  className: 'span1'

  events:
    "click button":  "follow"

  template: _.template $('#user-follows-template').html()

  initialize: ->
    @follows = @options.follows
    @followed = @options.followed
    @listenTo @follows, 'change', @render

  render: ->
    text = if @follows.get("follow").indexOf(@model.get "id") != -1  then 'Unfollow' else 'Follow'
    @$el.html @template text
    @

  follow: () ->
    followsPos = @follows.get("follow").indexOf @model.get "id"
    followedPos = @followed.get("follow").indexOf @follows.get "userId"

    if followsPos == -1
      @follows.get("follow").push @model.get "id"
      if followedPos == -1
        @followed.get("follow").push @follows.get "userId"
    else
      @follows.get("follow").splice followsPos, 1
      if followedPos != -1
        @followed.get("follow").splice followedPos, 1

    @follows.save() # Here  to decide whether to use local/session storage as cache
    @followed.save() # Here  to decide whether to use local/session storage as cache
    @follows.trigger 'change'
    @followed.trigger 'change'