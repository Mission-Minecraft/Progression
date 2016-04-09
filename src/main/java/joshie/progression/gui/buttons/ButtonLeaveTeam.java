package joshie.progression.gui.buttons;

import joshie.progression.gui.core.FeatureTooltip;
import joshie.progression.helpers.SplitHelper;
import joshie.progression.network.PacketChangeTeam;
import joshie.progression.network.PacketHandler;

import static joshie.progression.player.PlayerSavedData.TeamAction.LEAVE;
import static net.minecraft.util.EnumChatFormatting.BOLD;

public class ButtonLeaveTeam extends ButtonBaseTeam {
    public ButtonLeaveTeam(String text, int x, int y) {
        super(text, x, y);
    }

    @Override
    public void onClicked() {
        PacketHandler.sendToServer(new PacketChangeTeam(LEAVE));
    }

    @Override
    public void addTooltip() {
        FeatureTooltip.INSTANCE.addTooltip(BOLD + "Leave Team");
        FeatureTooltip.INSTANCE.addTooltip(SplitHelper.splitTooltip("Clicking this button will make you leave your current team and return to single player", 40));
    }
}
