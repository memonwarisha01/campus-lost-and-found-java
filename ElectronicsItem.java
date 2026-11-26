//ROLL NO:85,68,59,63.
package lostandfoundproject;

public class ElectronicsItem extends Item {
    private final String brandOrModel;

    public ElectronicsItem(String itemId, String title, String locationFound,
                           String brandOrModel, String verificationQuestion,
                           String verificationAnswer) {
        super(itemId, title, "Electronics", locationFound, verificationQuestion, verificationAnswer);
        this.brandOrModel = brandOrModel;
    }

    public String getBrandOrModel() {
        return brandOrModel;
    }

    @Override
    public String getItemBadge() {
        return "[TECH] " + getTitle() + " (" + brandOrModel + ")";
    }
}