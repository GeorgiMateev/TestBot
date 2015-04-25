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
	}

	public void insertEvents(List<EventViewModel> events, List<EventViewModel> mutations, long timeStamp) {
		BasicDBList eventsForDB = new BasicDBList();
		for (EventViewModel event : events) {
			eventsForDB.add(new BasicDBObject()
					.append("targetSelector", event.getTargetSelector())
					.append("type", event.getType())
					.append("timeStamp", event.getTimeStamp()));
		}

		BasicDBList mutationsForDb = new BasicDBList();
		for (EventViewModel mutation : mutations) {
			String targetSelector = mutation.getTargetSelector() != null ? mutation.getTargetSelector() : "html>body";
			mutationsForDb.add(new BasicDBObject()
					.append("targetSelector", targetSelector)
					.append("childSelector", mutation.getChildSelector())
					.append("type", mutation.getType())
					.append("timeStamp", mutation.getTimeStamp()));
		}

		this.eventsCollection.insert(new BasicDBObject()
				.append("events", eventsForDB)
				.append("mutations", mutationsForDb)
				.append("timeStamp", timeStamp));
	}
	
	public void addDummyData() {
		BasicDBList localEvents = new BasicDBList();
		localEvents.add(new BasicDBObject()
			.append("targetSelector", "ng-view>section>header>form>input")
			.append("type", "click")
			.append("timeStamp", "1429765607219"));
		
		BasicDBList localMutations = new BasicDBList();
		localMutations.add(new BasicDBObject()
			.append("targetSelector", "ng-view>section>section>ul")
			.append("childSelector", "ng-view>section>section>ul>li")
			.append("type", "added")
			.append("timeStamp", "1429765613290"));
		
		BasicDBObject entry = new BasicDBObject()
			.append("events", localEvents)
			.append("mutations", localMutations);
		
		this.eventsCollection.insert(entry);
	}
	
	public DBCursor GetEvents() {
		return this.eventsCollection
				.find()
				.sort(new BasicDBObject("timeStamp", 1));
	}
	
	public void saveErrorReport(String selector, String parentHtml, String type) {
		
	}

//	public DBCursor getResults() {
//		return this.results.find();
//	}
	
	private DB db;
	private DBCollection eventsCollection;
}