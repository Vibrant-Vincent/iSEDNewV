package socket.record;

public class Message {
    private String content;//Content of the frame to be send
    private boolean isIntermediate;//Whether the frame is a ternination frame 0: yes, 1: no
    private String sampleId;
    private String sampleCollectionTime;
    private String barcode;
    private String saveInDBTime;//the time message saved into database
    private String sendTime;//the time message sent to peer, update on every try;
    private int status;//whether the message sent to peer and ACK replied
    private int frame_status;//whether the current frame sent to peer and ACK replied
    private int frameIndex;
    private int index;

    public Message(String content, boolean isIntermediate, String sampleId, String sampleCollectionTime, String barcode, int frameIndex){
        this.content = content;
        this.isIntermediate = isIntermediate;
        this.sampleId = sampleId;
        this.sampleCollectionTime = sampleCollectionTime;
        this.barcode = barcode;
        this.frameIndex = frameIndex;
    }

    public Message(int index, String content, boolean isIntermediate, String sampleId, String sampleCollectionTime, String barcode, int frameIndex, int messageStatus){
        this.content = content;
        this.isIntermediate = isIntermediate;
        this.sampleId = sampleId;
        this.sampleCollectionTime = sampleCollectionTime;
        this.barcode = barcode;
        this.frameIndex = frameIndex;
        this.index = index;
        this.status = messageStatus;
        this.frame_status = messageStatus;
    }
    /*
    public Message(String content, boolean isIntermediate, String sampleId, String sampleCollectionTime, String barcode, int frameIndex){
        this.content = content;
        this.isIntermediate = isIntermediate;
        this.sampleId = sampleId;
        this.sampleCollectionTime = sampleCollectionTime;
        this.barcode = barcode;
        this.frameIndex = frameIndex;
    }
    */
    public Message(String content, boolean isIntermediate, String sampleId, String sampleCollectionTime, String barcode, String saveInDBTime,
                   String sendTime, int frameIndex, int messageStatus, int frameStatus){
        this.content = content;
        this.isIntermediate = isIntermediate;
        this.sampleId = sampleId;
        this.sampleCollectionTime = sampleCollectionTime;
        this.barcode = barcode;
        this.saveInDBTime = saveInDBTime;
        this.frameIndex = frameIndex;
        this.sendTime = sendTime;
        this.status = messageStatus;
        this.frame_status = frameStatus;

    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsIntermediate() {
        return isIntermediate;
    }

    public void setIsIntermediate(boolean isIntermediate) {
        this.isIntermediate = isIntermediate;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleCollectionTime() {
        return sampleCollectionTime;
    }

    public void setSampleCollectionTime(String sampleCollectionTime) {
        this.sampleCollectionTime = sampleCollectionTime;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSaveInDBTime() {
        return saveInDBTime;
    }

    public void setSaveInDBTime(String saveInDBTime) {
        this.saveInDBTime = saveInDBTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFrame_status() {
        return frame_status;
    }

    public void setFrame_status(int frame_status) {
        this.frame_status = frame_status;
    }

    public int getFrameIndex() {
        return frameIndex;
    }

    public void setFrameIndex(int frameIndex) {
        this.frameIndex = frameIndex;
    }
}
