package hello;

import java.util.ArrayList;

public class RunResult {
	private ArrayList<IssueDiff> issues;
	private long timeStamp;
	private String status;
	
	public ArrayList<IssueDiff> getIssues() {
		return issues;
	}
	public void setIssues(ArrayList<IssueDiff> issues) {
		this.issues = issues;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
