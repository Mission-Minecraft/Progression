package joshie.progression.plugins.enchiridion.features;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonObject;
import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IButtonActionProvider;
import joshie.enchiridion.api.book.IPage;
import joshie.enchiridion.util.ELocation;
import joshie.progression.api.ProgressionAPI;
import joshie.progression.api.criteria.ICriteria;
import joshie.progression.api.criteria.ITab;
import joshie.progression.handlers.APIHandler;
import joshie.progression.helpers.JSONHelper;
import joshie.progression.helpers.PlayerHelper;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;


public class FeatureTab extends FeatureProgression {
    private static final Cache<UUID, Integer> numberCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build();
    protected transient ITab tab;
    protected transient UUID uuid = UUID.randomUUID();
    protected transient boolean isInit = false;
    public String display = "New Criteria";

    public FeatureTab() {}

    public FeatureTab(ITab tab) {
        this.tab = tab;
        if (tab != null) {
            uuid = tab.getUniqueID();
            display = tab.getLocalisedName();
        }
    }

    @Override
    public FeatureTab copy() {
        return new FeatureTab(tab);
    }

    @Override
    public void onFieldsSet(String field) {
        super.onFieldsSet(field);

        if (field.equals("")) {
            tab = APIHandler.getCache(true).getTabs().get(uuid);
            if (tab != null) display = tab.getLocalisedName();
        } else if (field.equals("display")) {
            for (ITab t : APIHandler.getCache(true).getTabs().values()) {
                if (t.getLocalisedName().equals(display)) {
                    tab = t;
                    uuid = t.getUniqueID();
                }
            }
        }
    }

    public int getTabNumber() {
        try {
            return numberCache.get(tab.getUniqueID(), new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int number = 1;
                    for (ITab t : APIHandler.getCache(true).getSortedTabs()) {
                        if (t.getUniqueID().equals(tab.getUniqueID())) return number;

                        number++;
                    }

                    return 0;
                }
            });
        } catch (Exception e) { return 0; }
    }

    public int getCompletionAmount() {
        int totaltasks = tab.getCriteria().size();
        if (totaltasks == 0) return 100;
        Set<ICriteria> completed = ProgressionAPI.player.getCompletedCriteriaList(PlayerHelper.getClientUUID(), true);

        int tasksdone = 0;
        for (ICriteria criteria: completed) {
            if (tab.equals(criteria.getTab())) tasksdone++;
        }

        return (tasksdone * 100) / totaltasks;
    }

    public void drawFeature(int mouseX, int mouseY) {
        int color = 0xFF404040;
        if (mouseX >= position.getLeft() && mouseX <= position.getRight()) {
            if (mouseY>= position.getTop() && mouseY <= position.getTop() + 8) {
                color = 0xFFAAAAAA;
            }
        }

        EnchiridionAPI.draw.drawSplitScaledString(getTabNumber() + ".", position.getLeft(), position.getTop(), 200, color, 1F);
        EnchiridionAPI.draw.drawSplitScaledString(tab.getLocalisedName(), position.getLeft() + 18, position.getTop(), 200, color, 1F);
        EnchiridionAPI.draw.drawSplitScaledString(getCompletionAmount() + "% Completed", position.getLeft() + 13, position.getTop() + 10, 100, 0xFF404040, 0.75F);
    }

    @Override
    public void performClick(int mouseX, int mouseY) {
        if (tab != null) {
            if (mouseX >= position.getLeft() && mouseX <= position.getRight()) {
                if (mouseY >= position.getTop() && mouseY <= position.getTop() + 8) {
                    int number = 10 + getTabNumber();
                    IPage page = EnchiridionAPI.book.getPageIfNotExists(number);
                    if (page != null) {
                        IButtonActionProvider button = EnchiridionAPI.editor.getJumpPageButton(EnchiridionAPI.book.getPage().getPageNumber());
                        button.getAction().setResourceLocation(true, new ELocation("arrow_left_on")).setResourceLocation(false, new ELocation("arrow_left_off"));
                        page.addFeature(button, 21, 200, 18, 10, true, false);
                        for (ICriteria c: tab.getCriteria()) {
                            FeatureCriteria criteria = new FeatureCriteria(c, true);
                            page.addFeature(criteria, new Random().nextInt(400), new Random().nextInt(200), 16, 16, false, false);
                            // ^ Put the stuff in a random position :D
                        }
                    }

                    EnchiridionAPI.book.jumpToPageIfExists(number);
                }
            }
        }
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        if (!isInit) {
            isInit = true;
            onFieldsSet("");
        }

        if (tab != null) {
            drawFeature(mouseX, mouseY);
        }
    }

    @Override
    public void readFromJson(JsonObject object) {
        try {
            uuid = UUID.fromString(JSONHelper.getString(object, "uuid", "d977334a-a7e9-5e43-b87e-91df8eebfdff"));
        } catch (Exception e){}
    }

    @Override
    public void writeToJson(JsonObject object) {
        if (uuid != null) {
            JSONHelper.setString(object, "uuid", uuid.toString(), "d977334a-a7e9-5e43-b87e-91df8eebfdff");
        }
    }
}
