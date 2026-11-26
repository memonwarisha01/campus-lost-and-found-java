//ROLL NO:85,68,59,63.
package lostandfoundproject;

public class GeneralItem extends Item {

    public GeneralItem(String itemId, String title, String category, String locationFound,
                       String verificationQuestion, String verificationAnswer) {
        super(itemId, title, category, locationFound, verificationQuestion, verificationAnswer);
    }

    @Override
    public String getItemBadge() {
        return "[" + getCategory().toUpperCase() + "] " + getTitle();
    }
}