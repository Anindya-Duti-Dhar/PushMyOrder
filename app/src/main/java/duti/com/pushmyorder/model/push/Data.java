package duti.com.pushmyorder.model.push;

public class Data {

    long RecordId;
    String image;
    String title;
    String message;
    String timestamp;
    Payload payload;

    public long getRecordId() {
        return RecordId;
    }

    public void setRecordId(long recordId) {
        RecordId = recordId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Data{" +
                "RecordId=" + RecordId +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", payload=" + payload +
                '}';
    }
}
