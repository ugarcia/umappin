package controllers;

import java.util.LinkedHashMap;
import java.util.List;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import models.Route;
import models.User;
import models.User2Routes;
import play.libs.Json;
import play.mvc.Result;

public class User2RoutesREST extends ItemREST {


	public static Result getRoutes() {

		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		User2Routes user2routes = User2Routes.findById(user.id);
		if (user2routes == null) {
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}
		List<Route> routes = user2routes.all();
		if (routes.size() == 0) {
			return badRequest(Constants.ROUTES_EMPTY.toString());
		} else {
			// Return the response
			return ok(Json.toJson(Route.routesToObjectNodes(routes)));
		}
	}


	public static Result getRoute(String routeId) {
		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		User2Routes user2route = User2Routes.findById(user.id);
		if (user2route == null) {
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}
		Route route = user2route.findRouteById(routeId);
		if (route == null) {
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}
		// Return the response
		return ok(Json.toJson(Route.routeToFullObjectNode(route)));
	}


	public static Result getNearRoutes(int amount) {
		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Constants.JSON_EMPTY.toString());
		}
		List<Route> routes = Route.findByLocation(json.findPath("geometry"), amount, -1);
		// Return the response
		return ok(Json.toJson(Route.routesToObjectNodes(routes)));
	}
	
	public static Result getNearRoutes(int amount, int difficulty) {
		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Constants.JSON_EMPTY.toString());
		}
		List<Route> routes = Route.findByLocation(json.findPath("geometry"), amount, difficulty);
		// Return the response
		return ok(Json.toJson(Route.routesToObjectNodes(routes)));
	}


	public static Result updateRoute(String routeId) {
		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Constants.JSON_EMPTY.toString());
		}
		User2Routes user2route = User2Routes.findById(user.id);
		if (user2route == null) {
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}
		Route route = user2route.findRouteById(routeId);
		if (route == null){
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}

		route.difficulty = json.findPath("message").getIntValue();
		route.name = json.findPath("name").getTextValue();
		route.geometry = json.findPath("geometry");

		JsonNode propertiesNode = json.findPath("properties");
		route.tags = new LinkedHashMap<String, String>();

		for (int x = 0; x < propertiesNode.size(); x++){
			route.tags.put(propertiesNode.get(x).get(0).asText(), propertiesNode.get(x).get(1).asText());
		}

		route.save();

		// Return a copy of the message
		return ok(Json.toJson(Route.routeToFullObjectNode(route)));
	}


	public static Result addRoute() {
		final User user = Application.getLocalUser(session());

		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Constants.JSON_EMPTY.toString());
		}

		Route route = new Route();		// Create route
		route.name = json.findPath("name").getTextValue();
		route.difficulty = json.findPath("difficulty").getIntValue();
		route.userId = user.id;
		route.geometry = json.findPath("geometry");

		JsonNode propertiesNode = json.findPath("properties");
		route.tags = new LinkedHashMap<String, String>();

		for (int x = 0; x < propertiesNode.size(); x++){
			route.tags.put(propertiesNode.get(x).get(0).asText(), propertiesNode.get(x).get(1).asText());
		}
		
		route.save();
		
		User2Routes user2route = User2Routes.findById(user.id);
		
		if (user2route == null){
			user2route = new User2Routes();
			user2route.id = user.id;
		}

		user2route.addRoute(route);
		user2route.save();

		//Return a copy of the discussion
		return ok(Json.toJson(Route.routeToFullObjectNode(route)));
	}

	
	public static Result deleteRoute(String id){
		final User user = Application.getLocalUser(session());
		if (user == null){
			return badRequest(Constants.USER_NOT_LOGGED_IN.toString());
		}
		JsonNode json = request().body().asJson();
		if(json == null) {
			return badRequest(Constants.JSON_EMPTY.toString());
		}
		User2Routes user2routes = User2Routes.findById(user.id);
		if (user2routes == null || !user2routes.routeIds.contains(new ObjectId(id))){
			return badRequest(Constants.ROUTES_EMPTY.toString());
		}
		Route route = user2routes.findRouteById(id);

		user2routes.removeRoute(route);
		if(user2routes.routeIds.isEmpty()){
			user2routes.routeIds = null;
		}
		user2routes.save();
		route.delete();
		
		return ok(Json.toJson(Route.routeToFullObjectNode(route)));
	}
	
	
}
