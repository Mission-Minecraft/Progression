package joshie.progression.json;

import java.util.List;

public class DataCriteria {
	public DataCriteria() {}
	public DataCriteria(String uniqueName, String displayName, List<DataTrigger> triggers, List<DataGeneric> rewards, String[] requirements, String[] conflicts, int x, int y, boolean isVisible, boolean mustClaim) {
		this.uniqueName = uniqueName;
		this.displayName = displayName;
		this.triggers = triggers;
		this.rewards = rewards;
		this.prereqs = requirements;
		this.conflicts = conflicts;
		this.x = x;
		this.y = y;
		this.isVisible = isVisible;
		this.mustClaim = mustClaim;
	}
	
	String uniqueName;
	String displayName;
    String displayStack;
	List<DataTrigger> triggers;
	List<DataGeneric> rewards;
	String[] prereqs;
	String[] conflicts;
	int repeatable;
    boolean infinite;
	int x;
	int y;
	boolean isVisible;
	boolean mustClaim;
    boolean displayAchievement;
}
