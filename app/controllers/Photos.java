package controllers;

import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.*;
import play.mvc.Controller;
import play.mvc.Result;
import models.Photo;
import play.data.Form;

import static play.libs.Json.toJson;


//'photo manager' is just a page for tests on photos
public class Photos extends Controller {

    public static Result manager(){
        return ok(views.html.photo_manager.render());

    }

}
