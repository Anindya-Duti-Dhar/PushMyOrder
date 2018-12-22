package duti.com.pushmyorder.model.push;

public class Payload {

    long RowId;
    long RecordId;
    String webLink;
    String item;
    String quantity;

    public long getRowId() {
        return RowId;
    }

    public void setRowId(long rowId) {
        RowId = rowId;
    }

    public long getRecordId() {
        return RecordId;
    }

    public void setRecordId(long recordId) {
        RecordId = recordId;
    }

    public String getWebLink() {
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
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
                "RowId=" + RowId +
                ", RecordId=" + RecordId +
                ", webLink='" + webLink + '\'' +
                ", item='" + item + '\'' +
                ", quantity='" + quantity + '\'' +
                '}';
    }
}
