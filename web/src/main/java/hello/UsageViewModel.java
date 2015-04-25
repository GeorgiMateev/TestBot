package hello;

import java.util.List;

public class UsageViewModel {
	private List<EventViewModel> events;
	private List<EventViewModel> mutations;
	public List<EventViewModel> getEvents() {
		return events;
	}
	public void setEvents(List<EventViewModel> events) {
		this.events = events;
	}
	public List<EventViewModel> getMutations() {
		return mutations;
	}
	public void setMutations(List<EventViewModel> mutations) {
		this.mutations = mutations;
	}
}
