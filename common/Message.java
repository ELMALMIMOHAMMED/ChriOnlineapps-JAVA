package common;

public class Message {

    private String type;
    private String requestId;
    private String status;
    private String data;
    private String payload;

    public Message(String type, String requestId, String status, String data, String payload) {
        this.type = type;
        this.requestId = requestId;
        this.status = status;
        this.data = data;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getData() {
        return data;
    }

    public String getPayload() {
        return payload;
    }

    // 🔥 IMPORTANT : JSON helper
    public String getJson() {
        if (payload != null && !payload.isEmpty()) return payload;
        return data;
    }

    @Override
    public String toString() {
        return "Message{type=" + type + ", status=" + status + ", data=" + data + ", payload=" + payload + "}";
    }
}