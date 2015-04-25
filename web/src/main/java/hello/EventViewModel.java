package hello;

public class EventViewModel {
	private String type;
	private String targetSelector;
	private String childSelector;
	private long timeStamp;
	private String surroundingHtml;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTargetSelector() {
		return targetSelector;
	}
	public void setTargetSelector(String targetSelector) {
		this.targetSelector = targetSelector;
	}
	public String getChildSelector() {
		return childSelector;
	}
	public void setChildSelector(String childSelector) {
		this.childSelector = childSelector;
	}
	public long getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getSurroundingHtml() {
		return surroundingHtml;
	}
	public void setSurroundingHtml(String surroundingHtml) {
		this.surroundingHtml = surroundingHtml;
	}
	
}
