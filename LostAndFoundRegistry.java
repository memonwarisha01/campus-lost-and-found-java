//ROLL NO:85,68,59,63.
package lostandfoundproject;

import java.util.ArrayList;
import java.util.List;

public class LostAndFoundRegistry {
    private final List<Item> items = new ArrayList<>();
    private int sequence = 100;

    public LostAndFoundRegistry() {
        seedSampleItems();
    }

    private void seedSampleItems() {
        addGeneralItem("Insulated Water Bottle", "Accessories", "Central Library - 2nd Floor",
                "What color is the lid and is there any dent?", "black lid with small dent");
        addElectronicsItem("Wireless USB Mouse", "CS Lab 3", "Logitech M185",
                "What is inside the battery compartment or on the bottom?", "qr sticker");
        addGeneralItem("Algorithms Notebook", "Books & Notes", "Student Lounge",
                "What course code is written on the front cover?", "cs-201");
    }

    public String nextId() {
        sequence++;
        return "LF-" + sequence;
    }

    public Item addElectronicsItem(String title, String locationFound, String brandOrModel,
                                   String question, String answer) {
        Item item = new ElectronicsItem(nextId(), title, locationFound, brandOrModel, question, answer);
        items.add(item);
        return item;
    }

    public Item addGeneralItem(String title, String category, String locationFound,
                               String question, String answer) {
        Item item = new GeneralItem(nextId(), title, category, locationFound, question, answer);
        items.add(item);
        return item;
    }

    public List<Item> getFilteredItems(String query, boolean onlyAvailable) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (onlyAvailable && item.isClaimed()) {
                continue;
            }
            boolean matchesQuery = q.isEmpty()
                    || item.getItemId().toLowerCase().contains(q)
                    || item.getTitle().toLowerCase().contains(q)
                    || item.getCategory().toLowerCase().contains(q)
                    || item.getLocationFound().toLowerCase().contains(q)
                    || item.getItemBadge().toLowerCase().contains(q);
            if (matchesQuery) {
                result.add(item);
            }
        }
        return result;
    }

    public int getTotalCount() {
        return items.size();
    }

    public int getAvailableCount() {
        int count = 0;
        for (Item item : items) {
            if (!item.isClaimed()) {
                count++;
            }
        }
        return count;
    }

    public int getClaimedCount() {
        return getTotalCount() - getAvailableCount();
    }

    public Item findById(String itemId) {
        for (Item item : items) {
            if (item.getItemId().equals(itemId)) {
                return item;
            }
        }
        return null;
    }
}