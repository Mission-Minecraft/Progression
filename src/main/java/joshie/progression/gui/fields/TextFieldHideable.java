package joshie.progression.gui.fields;

import joshie.progression.api.gui.Position;
import joshie.progression.api.special.IHideable;

public class TextFieldHideable extends TextField implements IHideable {
    private BooleanFieldHideable field;
    
    public TextFieldHideable(String name, Object object, Position type) {
        super(name, object, type);
        this.field = field;
    }

    public TextFieldHideable setBooleanField(BooleanFieldHideable bool) {
        this.field = bool;
        return this;
    }
    
    @Override
    public String getField() {
        return isVisible() ? super.getField() : field.getField();
    }

    @Override
    public boolean click(int button) {
        if (button != 0) {
            field.setBoolean(!field.getBoolean()); //Set the stuff
            return true;
        } else if (isVisible()) return super.click();
        else if (button != 0 && !isVisible()) {
            return field.click();
        }

        return false;
    }

    @Override
    public boolean isVisible() {
        return field.isVisible() == false;
    }
}
