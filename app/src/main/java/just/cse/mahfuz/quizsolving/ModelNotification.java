package just.cse.mahfuz.quizsolving;

public class ModelNotification {
    String timestamp,message;

    public ModelNotification() {
    }

    public ModelNotification(String timestamp, String message) {
        this.timestamp = timestamp;
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
