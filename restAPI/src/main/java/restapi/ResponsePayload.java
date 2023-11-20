package restapi;

public class ResponsePayload {

    String seg;
    String requestedLogic;

    public ResponsePayload(String seg, String requestedLogic) {
        this.seg = seg;
        this.requestedLogic = requestedLogic;
    }

    public String getSeg() {
        return seg;
    }

    public void setSeg(String seg) {
        this.seg = seg;
    }

    public String getRequestedLogic() {
        return requestedLogic;
    }

    public void setRequestedLogic(String requestedLogic) {
        this.requestedLogic = requestedLogic;
    }
}
