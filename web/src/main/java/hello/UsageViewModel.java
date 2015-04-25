package hello;

import java.util.List;

public class UsageViewModel {
	private List<EventViewModel> events;
	private List<EventViewModel> mutation;
	public List<EventViewModel> getEvents() {
		return events;
	}
	public void setEvents(List<EventViewModel> events) {
		this.events = events;
	}
	public List<EventViewModel> getMutation() {
		return mutation;
	}
	public void setMutation(List<EventViewModel> mutation) {
		this.mutation = mutation;
	}
}
