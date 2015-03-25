package joshie.crafting.conditions;

import joshie.crafting.api.ICondition;
import joshie.crafting.api.IHasUniqueName;

public abstract class ConditionBase implements ICondition {
	private String uniqueName;
	private String typeName;
	private boolean inverted = false;
	
	public ConditionBase(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String getTypeName() {
		return typeName;
	}
	
	@Override
	public String getUniqueName() {
		return uniqueName;
	}

	@Override
	public IHasUniqueName setUniqueName(String unique) {
		this.uniqueName = unique;
		return this;
	}

	@Override
	public ICondition setInversion(boolean inverted) {
		this.inverted = inverted;
		return this;
	}

	@Override
	public boolean isInverted() {
		return inverted;
	}
}
