package models;

import com.google.code.morphia.annotations.Entity;
import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import play.libs.Json;

import java.util.List;
@Entity
public class Map extends Item {

	private static final long serialVersionUID = 1L;

    public ObjectId ownerId;
    public List<String> features;

    public ObjectId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ObjectId userId) {
        this.ownerId = userId;
    }

    public List<String> getFeatures() {
        return features;
    }

    public void setFeatures(List<String> features) {
        this.features = features;
    }


    /** ------------ Map model needs special ObjectIds handling ------------- **/
    @Override
    public JsonNode toJson() {
        JsonNode json = super.toJson();
        ((ObjectNode)json).put("ownerId", ownerId != null ? ownerId.toString() : null);
        return json;
    }

    public static Map mapFromJson(JsonNode srcJson) {
        JsonNode json = Map.fromJson(srcJson);
        JsonNode jtemp = json.findValue("ownerId");
        if (!jtemp.isNull())
            ((ObjectNode)json).putPOJO("ownerId", new ObjectId(jtemp.asText()));
        return Json.fromJson(json, Map.class);
    }

}
