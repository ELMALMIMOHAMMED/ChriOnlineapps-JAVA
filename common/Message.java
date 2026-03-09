package common;

public class Message {

    private String type;
    private String requestId;
    private String status;
    private String payload;
    private String errorCode;

    // constructeur vide
    public Message() {}

    // constructeur complet
    public Message(String type, String requestId, String status, String payload, String errorCode) {
        this.type = type;
        this.requestId = requestId;
        this.status = status;
        this.payload = payload;
        this.errorCode = errorCode;
    }

    // getters
    public String getType() {
        return type;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // setters
    public void setType(String type) {
        this.type = type;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    // méthode statique pour créer une requête
    public static Message request(String type, String requestId, String payload) {

        Message m = new Message();

        m.setType(type);
        m.setRequestId(requestId);
        m.setPayload(payload);
        m.setStatus("");
        m.setErrorCode("");

        return m;
    }

}
