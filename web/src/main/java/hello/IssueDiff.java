package hello;

public class IssueDiff {
	private String target;
	private String expectedParentHtml;
	private String actualParentHtml;
	private String type;
	private long timeStamp;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getActualParentHtml() {
		return actualParentHtml;
	}
	public void setActualParentHtml(String actualParentHtml) {
		this.actualParentHtml = actualParentHtml;
	}
	public String getExpectedParentHtml() {
		return expectedParentHtml;
	}
	public void setExpectedParentHtml(String expectedParentHtml) {
		this.expectedParentHtml = expectedParentHtml;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
}
