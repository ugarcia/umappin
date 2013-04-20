package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.mapping.Mapper;
import com.google.code.morphia.query.UpdateOperations;

import controllers.MorphiaObject;

@Entity
public class UserStatistics {
	@Id
	public ObjectId id;
	
	public String userId;
	
	public int points = 0;
	
	public int level = 1;
	
	public boolean newLevel = false;

	public Map<String, Integer> statistics = new HashMap<String, Integer>();
	
	@Embedded
	public List<UserAward> userAwards = new ArrayList<UserAward>();

	public static UserStatistics findByUserId(String userId) {
		return	MorphiaObject.datastore.find(UserStatistics.class).
				field("userId").equal(userId).get();
	}
	
	public static UserStatistics updateByUserId(String userId, Map<String, Integer> newStatistics) {
		UserStatistics userStatistics = findByUserId(userId);
		if(userStatistics == null) {
			userStatistics = UserStatistics.init(userId);
		}
		//TODO: update each item
        userStatistics.update();
        return userStatistics;
    }
	
	public void updateStatistic(String statistic, Integer addingValue) {
		Integer previousValue = this.statistics.get(statistic);
		Integer newValue = previousValue+addingValue;
		this.statistics.put(statistic, newValue);
		updateAwards(statistic, previousValue, newValue);
	}
	
	private void updateAwards(String statistic, Integer previousValue, Integer newValue) {
		//TODO
	}
	
	public static UserStatistics init(String userId) {
		UserStatistics userStatistics = new UserStatistics();
		userStatistics.userId = userId;
        //TODO: initialize the hashmap values to 0...
        return userStatistics;
    }
	
	public UserStatistics save() {
		MorphiaObject.datastore.save(this);
		return this;
	}
	
	public UserStatistics update() {
		UpdateOperations<UserStatistics> ops = 
				MorphiaObject.datastore.createUpdateOperations(UserStatistics.class)
					.set("points", this.points)
					.set("level", this.level)
					.set("newLevel", this.newLevel)
					.set("statistics", this.statistics)
					.set("userAwards", this.userAwards);

		//update, if not found create it
		MorphiaObject.datastore.updateFirst(MorphiaObject.datastore
				.createQuery(UserStatistics.class).field(Mapper.ID_KEY)
				.equal(new ObjectId(this.userId)), ops, true);
		return this;
	}
	
	public static ObjectNode userStatisticsToObjectNode (UserStatistics userStatistics){
		ObjectNode statisticsNode = Json.newObject();
		statisticsNode.put("id", userStatistics.userId);
		statisticsNode.put("points", userStatistics.points);
		statisticsNode.put("level", userStatistics.level);
		statisticsNode.put("newLevel", userStatistics.newLevel);
		statisticsNode.put("statistics", Json.toJson(userStatistics.statistics));
		statisticsNode.put("userAwards", Json.toJson(UserAward
				.userAwardsToObjectNodes(userStatistics.userAwards)));
		return statisticsNode;
	}
}
