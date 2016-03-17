package joshie.progression.gui.fields;

import joshie.progression.gui.fields.FieldHelper.DoubleFieldHelper;
import joshie.progression.gui.fields.FieldHelper.FloatFieldHelper;
import joshie.progression.gui.fields.FieldHelper.IntegerFieldHelper;
import joshie.progression.gui.newversion.overlays.DrawFeatureHelper;

public class TextField extends AbstractField {
    protected FieldHelper data;

    public TextField(String displayName, String fieldName, Object object) {
        super(displayName);
        this.data = getField(name, object);
    }

    public TextField(String name, Object object) {
        this(name, name, object);
    }
    
    @Override
    public String getFieldName() {
        return data.getFieldName();
    }

    @Override
    public void click() {
        data.select();
    }
    
    @Override
    public String getField() {
        return data.getText();
    }

    @Override
    public void draw(DrawFeatureHelper helper, int renderX, int renderY, int color, int yPos, int mouseX, int mouseY) {
        helper.drawSplitText(renderX, renderY, name + ": " + data.getText(), 4, yPos, 125, color, 0.75F);
    }

    public static FieldHelper getField(String name, Object object) {
        try {
            Class clazz = object.getClass().getField(name).getType();
            String className = clazz.getSimpleName();
            if (className.equalsIgnoreCase("double")) return new DoubleFieldHelper(name, object);
            if (className.equalsIgnoreCase("float")) return new FloatFieldHelper(name, object);
            if (className.equalsIgnoreCase("int")) return new IntegerFieldHelper(name, object);
            if (className.equalsIgnoreCase("string")) return new FieldHelper(name, object);
        } catch (Exception e) { e.printStackTrace(); }

        return null;
    }
    
    @Override
    public void setObject(Object object) {
        if (this.data == null) {
            this.data = getField(name, object);
        }
    }
}