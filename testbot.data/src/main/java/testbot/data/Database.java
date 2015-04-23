package testbot.data;

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
		
		this.db = mongoClient.getDB( "testbot" );
		this.eventsCollection = db.getCollection("events");
	}
	
	public void addDummyData() {
		BasicDBList localEvents = new BasicDBList();
		localEvents.add(new BasicDBObject()
			.append("targetSelector", "ng-view>section>header>form>input")
			.append("eventName", "click")
			.append("timeStamp", "1429765607219"));
		
		BasicDBList localMutations = new BasicDBList();
		localMutations.add(new BasicDBObject()
			.append("targetSelector", "ng-view>section>section>ul")
			.append("addedSelector", "ng-view>section>section>ul>li")
			.append("type", "added")
			.append("timeStamp", "1429765613290"));
		
		BasicDBObject entry = new BasicDBObject()
			.append("events", localEvents)
			.append("mutations", localMutations);
		
		this.eventsCollection.insert(entry);
	}
	
	private DB db;
	private DBCollection eventsCollection;
}