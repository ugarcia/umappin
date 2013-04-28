class umappin.Router extends Backbone.Router
  subroutes: {}
  params: null
  routes:
    'account':            'account'
    'account/:id':        'account'
    'editmap':            'editmap'
    'messages/*subroute': 'messages'
    'signup':             'signup'
    'login':              'login'
    'logout':             'logout'
    'linkProvider':       'linkProvider'
    'forgotPassword':     'forgotPassword'
    'changePassword':     'changePassword'
    'leafletmap':         'leafletmap'
    'test':               'test'
    'synctest':           'syncTest'

  account: (id) ->
    @params = if id? then id: id else null
    setTemplate "/assets/templates/account.html", () ->
      requirejs ['/assets/js/account/account_main.js'], () ->
        Account.init()

  editmap: () ->
    setTemplate "/assets/templates/editmap.html", () ->
      requirejs ['/assets/js/lib/openlayers.min.js'], () ->
        requirejs ['/assets/js/map/editmap.js'], () ->
          MapEditor.init()

  messages: () ->
    subroutes = @subroutes
    requirejs ['/assets/js/messagesAPP/collection/discussionCollection.js'], () ->
      requirejs ['/assets/js/messagesAPP/routers/router.js'], () ->
        subroutes.messagesRouter or= new messagesApp.Router "messages/"
  
  login: () ->
    setTemplate "/assets/templates/login.html"
    requirejs ['/assets/js/messagesAPP/routers/router.js'], () ->
      subroutes.messagesRouter or= new messagesApp.Router "messages/"

  logout: () ->
    $.get "/logout", () ->
      setTemplate '/assets/templates/logout.html'
      sessionStorage.removeItem "user"
      updateSessionViews ""

  signup: () ->  # Need to separate js source
    setTemplate "/assets/templates/signup.html"

  login: () ->  # Need to separate js source
    setTemplate "/assets/templates/login.html"

  linkProvider: () ->  # Need to separate js source
    setTemplate "/assets/templates/linkProvider.html"

  forgotPassword: () -> # Need to separate js source
    setTemplate "/assets/templates/forgotPassword.html"

  changePassword: () ->  # Need to separate js source
    setTemplate "/assets/templates/changePassword.html"

  # Old leaflet map, remove before release
  leafletmap: () ->
    setTemplate "/assets/templates/leaflet-map.html", () ->
      requirejs ['/assets/js/lib/leaflet.min.js'], () ->
        requirejs ['/assets/js/lib/leaflet.draw.js'], () ->
          requirejs ['/assets/js/map/leaflet-map.js'], () ->
            Map.init()

  # Testing routes, remove before release
  test: () ->
    setTemplate "/assets/templates/test.html"

  syncTest: () ->
    setTemplate "/assets/templates/syncTest.html"
    requirejs ['/assets/js/test/subitem_model.js'], () ->
      requirejs ['/assets/js/test/subitem_collection.js'], () ->
        requirejs ['/assets/js/test/syncTest.js']
