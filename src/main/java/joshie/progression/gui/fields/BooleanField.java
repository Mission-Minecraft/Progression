package joshie.progression.gui.fields;

import java.lang.reflect.Field;

import joshie.progression.api.ProgressionAPI;

public class BooleanField extends AbstractField {
    public Field field;
    public String truth;
    public String lies;

    public BooleanField(String displayName, String fieldName, Object object) {
        super(displayName);
        this.object = object;

        try {
            field = object.getClass().getField(fieldName);
        } catch (Exception e) {}
    }
    
    public BooleanField(String displayName, Object object, String truth, String lies) {
        this(displayName, displayName, object, truth, lies);
    }

    public BooleanField(String displayName, String fieldName, Object object, String truth, String lies) {
        this(displayName, fieldName, object);
        this.truth = truth;
        this.lies = lies;
    }

    public boolean getBoolean() throws IllegalArgumentException, IllegalAccessException {
        return field.getBoolean(object);
    }

    public void setBoolean(boolean bool) throws IllegalArgumentException, IllegalAccessException {
        field.set(object, bool);
    }

    public BooleanField(String name, Object object) {
        this(name, name, object);
    }

    @Override
    public void click() {
        try {
            setBoolean(!getBoolean());
        } catch (Exception e) {}
    }

    @Override
    public void draw(int color, int yPos) {
        try {
            boolean value = getBoolean();
            if (truth != null) {
                ProgressionAPI.draw.drawSplitText(name + ": " + (value ? truth: lies), 4, yPos, 105, color);
            } else {
                ProgressionAPI.draw.drawSplitText(name + ": " + value, 4, yPos, 105, color);
            }
        } catch (Exception e) {}
    }
}