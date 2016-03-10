package joshie.progression.gui.newversion.overlays;

import joshie.progression.gui.newversion.GuiCore;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public interface IGuiFeature {
	public IGuiFeature init(GuiCore gui);
	public void draw(int mouseX, int mouseY);
	public boolean mouseClicked(int mouseX, int mouseY, int button);
    public boolean isVisible();
    public boolean scroll(int mouseX, int mouseY, boolean down);
    public void setVisibility(boolean isVisible);
}
