package joshie.progression.plugins.enchiridion.actions;

import joshie.enchiridion.api.EnchiridionAPI;
import joshie.enchiridion.api.book.IBook;
import joshie.enchiridion.api.book.IButtonAction;
import joshie.enchiridion.gui.book.buttons.actions.ActionJumpPage;

public class ActionJumpTab extends ActionJumpPage {
    public transient int tempPage;

    public ActionJumpTab(int page) {
        super(page);
    }

    @Override
    public IButtonAction copy() {
        ActionJumpTab action = new ActionJumpTab(pageNumber);
        action.bookID = this.bookID;
        this.copyAbstract(action);
        return action;
    }

    @Override
    public void initAction() {
        tempPage = pageNumber;
    }

    @Override
    public void performAction() {
        if(bookID != null) {
            IBook book = EnchiridionAPI.instance.getBook(bookID);
            if(book != null) {
                EnchiridionAPI.book.setBook(book, EnchiridionAPI.book.isEditMode());
            }
        }

        EnchiridionAPI.book.jumpToPageIfExists(tempPage);
    }
}
