package duti.com.pushmyorder.model.push;

public class Payload {

    long RecordId;
    String item;
    String quantity;

    public long getRecordId() {
        return RecordId;
    }

    public void setRecordId(long recordId) {
        RecordId = recordId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "RecordId=" + RecordId +
                ", item='" + item + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
