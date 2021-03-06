package hello;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BulkWriteOperation;
import com.mongodb.BulkWriteResult;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.ParallelScanOptions;
import com.mongodb.ServerAddress;
import org.bson.types.ObjectId;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.concurrent.TimeUnit.SECONDS;

class Database {
	Database() {
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient( "localhost" , 27017 );
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.db = mongoClient.getDB("testbot");
		this.eventsCollection = db.getCollection("events");
		this.resultsCollection = db.getCollection("results");
	}

	public void insertEvents(List<EventViewModel> events, List<EventViewModel> mutations, long timeStamp) {
		BasicDBList eventsForDB = new BasicDBList();
		for (EventViewModel event : events) {
			eventsForDB.add(new BasicDBObject()
					.append("targetSelector", event.getTargetSelector())
					.append("type", event.getType())
					.append("surroundingHtml", event.getSurroundingHtml())
					.append("timeStamp", event.getTimeStamp()));
		}

		BasicDBList mutationsForDb = new BasicDBList();
		for (EventViewModel mutation : mutations) {
			String targetSelector = mutation.getTargetSelector() != null ? mutation.getTargetSelector() : "html>body";
			mutationsForDb.add(new BasicDBObject()
					.append("targetSelector", targetSelector)
					.append("childSelector", mutation.getChildSelector())
					.append("type", mutation.getType())
					.append("surroundingHtml", mutation.getSurroundingHtml())
					.append("timeStamp", mutation.getTimeStamp()));
		}

		this.eventsCollection.insert(new BasicDBObject()
				.append("events", eventsForDB)
				.append("mutations", mutationsForDb)
				.append("timeStamp", timeStamp));
	}

	public DBCursor GetEvents() {
		return this.eventsCollection
				.find()
				.sort(new BasicDBObject("timeStamp", 1));
	}
	
	public Object createRun(long timeStamp) {
		BasicDBObject run = (new BasicDBObject("timeStamp", timeStamp))
			.append("issues", new BasicDBList());
		
		this.resultsCollection.insert(run);
		
		return run.get("_id");
	}
	
	public void saveErrorReport(Object runId, String selector, String expectedParentHtml, String actualParentHtml, String type, long timeStamp) {		
		
		BasicDBObject issue = (new BasicDBObject())
				.append("selector", selector)
				.append("expectedParentHtml", expectedParentHtml)
				.append("actualParentHtml", actualParentHtml)
				.append("type", type)
				.append("timeStamp", timeStamp);
		
		this.resultsCollection.update(new BasicDBObject("_id", runId),
				new BasicDBObject("$push",
						new BasicDBObject("issues", issue)));
	}

	public List<RunResult> getResults() {
		ArrayList<RunResult> results = new ArrayList<>();
		DBCursor cursor = this.resultsCollection.find();
		for (DBObject run : cursor) {
			RunResult runResult = new RunResult();
			BasicDBList issues = (BasicDBList) run.get("issues");
			runResult.setIssues(this.extractIssues(issues));
			runResult.setTimeStamp(Long.parseLong(run.get("timeStamp").toString()));
			results.add(runResult);
		}
		return results;
	}

	public RunResult getResultById(Object id) {
		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(id.toString()));
		DBObject dbResult = this.resultsCollection.findOne(query);

		RunResult result = new RunResult();
		BasicDBList issues = (BasicDBList) dbResult.get("issues");
		result.setIssues(this.extractIssues(issues));
		result.setTimeStamp(Long.parseLong(dbResult.get("timeStamp").toString()));

		return result;
	}

	private ArrayList<IssueDiff> extractIssues(BasicDBList issues) {
		ArrayList<IssueDiff> issueDiffs = new ArrayList<>();
		for (Object issue : issues) {
			DBObject dbIssue = (DBObject) issue;
			IssueDiff issueDiff = new IssueDiff();
			issueDiff.setExpectedParentHtml(dbIssue.get("expectedParentHtml").toString());
			issueDiff.setActualParentHtml(dbIssue.get("actualParentHtml").toString());
			issueDiff.setTarget(dbIssue.get("selector").toString());
			issueDiff.setType(dbIssue.get("type").toString());
			issueDiff.setTimeStamp(Long.parseLong(dbIssue.get("timeStamp").toString()));
			issueDiffs.add(issueDiff);
		}

		return issueDiffs;
	}

	private DB db;
	private DBCollection eventsCollection;
	private DBCollection resultsCollection;
}