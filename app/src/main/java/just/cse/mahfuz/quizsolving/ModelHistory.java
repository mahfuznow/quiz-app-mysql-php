package just.cse.mahfuz.quizsolving;

public class ModelHistory {
    String timestamp,type,number,amount;

    public ModelHistory() {
    }

    public ModelHistory(String timestamp, String type, String number, String amount) {
        this.timestamp = timestamp;
        this.type = type;
        this.number = number;
        this.amount = amount;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
