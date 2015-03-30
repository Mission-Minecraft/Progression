package joshie.crafting.gui;

import java.lang.reflect.Field;

import joshie.crafting.gui.SelectTextEdit.ITextEditable;
import net.minecraft.item.ItemStack;

public class TextFieldHelper implements ITextEditable {
    public Object o;
    public Field f;

    public TextFieldHelper(String f, Object o) {
        try {
            this.f = o.getClass().getDeclaredField(f);
            this.o = o;
        } catch (Exception e) {}
    }

    public void select() {
        SelectTextEdit.INSTANCE.select(this);
    }

    public int getInteger() {
        try {
            return (Integer) f.get(o);
        } catch (Exception e) { e.printStackTrace();}

        return 0;
    }

    public String getString() {
        try {
            return (String) f.get(o);
        } catch (Exception e) {}

        return "";
    }

    public void set(Object o2) {
        try {
            f.set(o, o2);
        } catch (Exception e) {}
    }

    @Override
    public String toString() {
        return SelectTextEdit.INSTANCE.getText(this);
    }

    @Override
    public String getTextField() {
        return getString();
    }

    @Override
    public void setTextField(String str) {
        set(str);
    }

    public static class IntegerFieldHelper extends TextFieldHelper {
        private String textField = null;

        public IntegerFieldHelper(String f, Object o) {
            super(f, o);
        }

        @Override
        public String getTextField() {
            if (textField == null) {
                textField = "" + getInteger();
            }

            return textField;
        }

        @Override
        public void setTextField(String str) {
            String fixed = str.replaceAll("[^0-9]", "");
            this.textField = fixed;

            try {
                setNumber(Integer.parseInt(str));
            } catch (Exception e) {}
        }

        public void setNumber(int parseInt) {
            set(parseInt);
        }
    }

    public static class ItemAmountHelper extends IntegerFieldHelper {
        public ItemAmountHelper(String f, IItemGettable o) {
            super(f, o);
        }

        @Override
        public void setNumber(int parseInt) {
            super.setNumber(parseInt);
            ItemStack clone = ((IItemGettable) o).getItemStack().copy();
            clone.stackSize = parseInt;
            ((IItemGettable) o).setItemStack(clone);
        }
    }

    public static interface IItemGettable {
        public ItemStack getItemStack();

        public void setItemStack(ItemStack stack);
    }
}
