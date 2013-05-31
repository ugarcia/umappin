window.Maps or= {}
OpenLayers.ImgPath = "/assets/img/openlayers/"
_.templateSettings.variable = 'rc'

class window.Maps.FeaturesMapView extends Maps.MapView
  drawLayer: null

  # ---------------------------- Controls ------------------------------ #
  initControls: ->
    OpenLayers.Feature.Vector.style['default']['strokeWidth'] = '2'
    # allow testing of specific renderers via "?renderer=Canvas", etc
    renderer = OpenLayers.Util.getParameters(window.location.href).renderer
    renderer = if renderer? then [renderer] else OpenLayers.Layer.Vector.prototype.renderers
    @drawLayer = new OpenLayers.Layer.Vector(
      "Draws"
      renderers: renderer
    )

    toolBarControls = [

      new OpenLayers.Control.DragPan(
        'displayClass': 'olControlDragPan'
      )

      new OpenLayers.Control.DrawFeature(
        @drawLayer
        OpenLayers.Handler.Point
        'displayClass': 'olControlDrawFeaturePoint'
        featureAdded: (feature, pixel) => @saveFeature feature, "node"
      )

      new OpenLayers.Control.DrawFeature(
        @drawLayer
        OpenLayers.Handler.Path
        'displayClass': 'olControlDrawFeaturePath'
        featureAdded: (feature, pixel) => @saveFeature feature, "way"
      )

      new OpenLayers.Control.DrawFeature(
        @drawLayer
        OpenLayers.Handler.Polygon
        'displayClass': 'olControlDrawFeaturePolygon'
        featureAdded: (feature, pixel) => @saveFeature feature, "relation"
      )

      new OpenLayers.Control.DrawFeature(
        @drawLayer
        OpenLayers.Handler.RegularPolygon
        'displayClass': 'olControlDrawFeatureRegularPolygon'
        handlerOptions: { sides: 8 }
        featureAdded: (feature, pixel) => @saveFeature feature, "relation"
      )

      new OpenLayers.Control.ModifyFeature(
        @drawLayer
        'displayClass': 'olControlModifyFeature'
        mode: OpenLayers.Control.ModifyFeature.RESHAPE | OpenLayers.Control.ModifyFeature.DRAG
        onModificationStart: (feature) => console.log feature
        onModificationEnd: (feature) => @updateFeature feature
      )

      new OpenLayers.Control.ModifyFeature(
        @drawLayer
        'displayClass': 'olControlModifyFeature'
        mode: OpenLayers.Control.ModifyFeature.RESIZE | OpenLayers.Control.ModifyFeature.ROTATE | OpenLayers.Control.ModifyFeature.DRAG
        onModificationStart: (feature) => console.log feature
        onModificationEnd: (feature) => @updateFeature feature
      )

      new OpenLayers.Control.SelectFeature(
        @drawLayer
        'displayClass': 'olControlDragFeature'
        box: true
        onSelect: (feat) => console.log feat
        onUnselect: (feat) => console.log feat
      )

    ]
    toolbar = new OpenLayers.Control.Panel(
      displayClass: 'olControlEditingToolbar'
      defaultControl: toolBarControls[0]
    )
    toolbar.addControls toolBarControls

    @controls.push toolbar

    # TODO: Add more controls here ....

    @map.addLayer @drawLayer
    super


  # ---------------------------- Geolocation Handler ------------------------------ #
  #Overriden
  handleGeoLocated: (e) ->
    super
    @map.zoomToExtent e.point.getBounds()


  # ---------------------------- Search handler ------------------------------ #
  # Overriden
  selectLocation: (location) ->
    super
    p = new OpenLayers.Geometry.Point location.lon, location.lat
    p.transform Maps.MapView.OSM_PROJECTION, @map.getProjectionObject()
    feature = new OpenLayers.Feature.Vector(
      p
      {}
      externalGraphic: location.icon
      pointRadius: 10
    )
    @drawLayer.addFeatures [feature]
    @map.zoomToExtent p.getBounds()


  # ---------------------------- Initialization ------------------------------ #
  initialize: ->
    @controls = []
    @baseLayers = []
    super


  # ---------------------------- Renderization ------------------------------ #
  render: ->
    features = @model.get 'features'
    @drawFeature f for f in features unless features == null
    super


  # ---------------------------- REST/Feature handlers ------------------------------ #
  drawFeature: (featureId) ->
    feature = new Maps.OsmNode id: featureId
    feature.fetch complete: (resp) =>  @addFeatureToMap feature


  saveFeature: (feature, type) ->
    console.log feature
    geojsonFormat = new OpenLayers.Format.GeoJSON()
    ft = @createOsmFeature type
    ft.set('user', (@model.get 'ownerId'))
    ft.set('version', 1)
    #ft.set('timeStamp', new Date())
    osmgeom = feature.geometry.clone()
    osmgeom.transform @map.getProjectionObject(), Maps.MapView.OSM_PROJECTION
    ft.set('geometry', (JSON.parse geojsonFormat.write osmgeom))
    ft.once 'change', (evt) =>
      feature.mapFeature = ft
      fts = @model.get('features') or []
      fts.push ft.get 'id'
      @model.save features: fts
    ft.save()

  updateFeature: (feature) ->
    geojsonFormat = new OpenLayers.Format.GeoJSON()
    osmgeom = feature.geometry.clone()
    osmgeom.transform @map.getProjectionObject(), Maps.MapView.OSM_PROJECTION
    feature.mapFeature.save
      geometry: JSON.parse geojsonFormat.write osmgeom
      version: feature.mapFeature.get('version') + 1

  deleteFeature: (feature) ->
    feature.mapFeature.destroy success: ()=>
      features = @model.get 'features'
      @model.save
        features: features.splice(features.indexOf(feature.mapFeature.get 'id'), 1)
        { success: () => @drawLayer.removeFeatures [feature] }

  createOsmFeature: (type) ->
    ft = switch
      when type is 'node' then new Maps.OsmNode
      when type is 'way' then new Maps.OsmWay
      when type is 'relation' then new Maps.OsmRelation

  addFeatureToMap: (feature) =>
    if feature.get('geometry')? and feature.get('geometry')!=null
      geojsonFormat = new OpenLayers.Format.GeoJSON()
      osmGeom = geojsonFormat.read(JSON.stringify(feature.get 'geometry'), 'Geometry')
      geom = osmGeom.clone().transform Maps.MapView.OSM_PROJECTION, @map.getProjectionObject()
      olGeom = new OpenLayers.Feature.Vector geom
      olGeom.mapFeature = feature
      @drawLayer.addFeatures [olGeom]
    
