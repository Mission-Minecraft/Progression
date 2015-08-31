package joshie.progression.criteria.filters;

import java.util.ArrayList;
import java.util.List;

import joshie.progression.api.IItemFilter;
import joshie.progression.gui.fields.AbstractField;
import joshie.progression.helpers.ClientHelper;
import joshie.progression.json.Theme;

import com.google.gson.JsonObject;

import cpw.mods.fml.common.eventhandler.Event.Result;

public abstract class FilterBase implements IItemFilter {
    protected List<AbstractField> list = new ArrayList();
    protected String name;

    public FilterBase(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void readFromJSON(JsonObject data) {}

    @Override
    public void writeToJSON(JsonObject data) {}

    @Override
    public Result onClicked(int mouseX, int mouseY) {
        if (ClientHelper.canEdit()) {
            int index = 0;
            for (AbstractField t : list) {
                int color = Theme.INSTANCE.optionsFontColor;
                int yPos = 25 + (index * 8);
                if (mouseX >= 1 && mouseX <= 84) {
                    if (mouseY >= yPos && mouseY < yPos + 8) {
                        t.click();
                        return Result.ALLOW;
                    }
                }

                if (t.attemptClick(mouseX, mouseY)) {
                    return Result.ALLOW;
                }

                index++;
            }
        }

        return Result.DEFAULT;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        int index = 0;
        for (AbstractField t : list) {
            int color = Theme.INSTANCE.optionsFontColor;
            int yPos = 25 + (index * 8);
            if (ClientHelper.canEdit()) {
                if (mouseX >= 1 && mouseX <= 84) {
                    if (mouseY >= yPos && mouseY < yPos + 8) {
                        color = Theme.INSTANCE.optionsFontColorHover;
                    }
                }
            }

            t.draw(color, yPos);
            index++;
        }
    }
}
