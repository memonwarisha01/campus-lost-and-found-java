//ROLL NO:85,68,59,63.
package lostandfoundproject;

import java.time.LocalDate;

public abstract class Item {
    private final String itemId;
    private String title;
    private String category;
    private String locationFound;
    private final LocalDate dateReported;
    private String verificationQuestion;
    private String verificationAnswer;
    private boolean claimed;
    private String claimedByStudentId;

    public Item(String itemId, String title, String category, String locationFound,
                String verificationQuestion, String verificationAnswer) {
        this.itemId = itemId;
        this.title = title;
        this.category = category;
        this.locationFound = locationFound;
        this.dateReported = LocalDate.now();
        this.verificationQuestion = verificationQuestion;
        this.verificationAnswer = verificationAnswer.trim().toLowerCase();
        this.claimed = false;
        this.claimedByStudentId = "-";
    }

    public abstract String getItemBadge();

    public boolean verifyAnswer(String candidateAnswer) {
        if (candidateAnswer == null) {
            return false;
        }
        return verificationAnswer.equals(candidateAnswer.trim().toLowerCase());
    }

    public boolean markClaimed(String studentId, String candidateAnswer) {
        if (claimed || studentId == null || studentId.trim().isEmpty()) {
            return false;
        }
        if (!verifyAnswer(candidateAnswer)) {
            return false;
        }
        this.claimed = true;
        this.claimedByStudentId = studentId.trim().toUpperCase();
        return true;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getLocationFound() {
        return locationFound;
    }

    public LocalDate getDateReported() {
        return dateReported;
    }

    public String getVerificationQuestion() {
        return verificationQuestion;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public String getClaimedByStudentId() {
        return claimedByStudentId;
    }

    public String getStatusText() {
        return claimed ? "Claimed (" + claimedByStudentId + ")" : "Available";
    }
}