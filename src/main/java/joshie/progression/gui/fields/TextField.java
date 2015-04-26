package joshie.progression.gui.fields;

import joshie.progression.api.ProgressionAPI;
import joshie.progression.gui.TextFieldHelper;
import joshie.progression.gui.TextFieldHelper.DoubleFieldHelper;
import joshie.progression.gui.TextFieldHelper.FloatFieldHelper;
import joshie.progression.gui.TextFieldHelper.IntegerFieldHelper;

public class TextField extends AbstractField {
    protected TextFieldHelper data;

    public TextField(String displayName, String fieldName, Object object) {
        super(displayName);
        this.data = getField(name, object);
    }

    public TextField(String name, Object object) {
        this(name, name, object);
    }

    @Override
    public void click() {
        data.select();
    }

    @Override
    public void draw(int color, int yPos) {
        ProgressionAPI.draw.drawSplitText(name + ": " + data.getText(), 4, yPos, 105, color);
    }

    public static TextFieldHelper getField(String name, Object object) {
        try {
            Class clazz = object.getClass().getField(name).getType();
            String className = clazz.getSimpleName();
            if (className.equalsIgnoreCase("double")) return new DoubleFieldHelper(name, object);
            if (className.equalsIgnoreCase("float")) return new FloatFieldHelper(name, object);
            if (className.equalsIgnoreCase("int")) return new IntegerFieldHelper(name, object);
            if (className.equalsIgnoreCase("string")) return new TextFieldHelper(name, object);
        } catch (Exception e) {}

        return null;
    }
    
    @Override
    public void setObject(Object object) {
        if (this.data == null) {
            this.data = getField(name, object);
        }
    }
}