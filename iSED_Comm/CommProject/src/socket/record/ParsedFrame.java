package socket.record;

public class ParsedFrame {
    private String originalMessage;
    private String translatedMessage;
    private String messageSection;
    private String frameNumber;
    private String isEndFrame;//false:intermediate frame
    private boolean isPassChecksum;
    private boolean isMessageFrame;
    public ParsedFrame(boolean isPassChecksum, boolean isMessageFrame, String originalMessage, String translatedMessage,
                       String messageSection) {
        this.isPassChecksum = isPassChecksum;
        this.isMessageFrame = isMessageFrame;
        this.originalMessage = originalMessage;
        this.translatedMessage = translatedMessage;
        this.messageSection = messageSection;
    }
    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getTranslatedMessage() {
        return translatedMessage;
    }

    public void setTranslatedMessage(String translatedMessage) {
        this.translatedMessage = translatedMessage;
    }

    public String getMessageSection() {
        return messageSection;
    }

    public void setMessageSection(String messageSection) {
        this.messageSection = messageSection;
    }

    public String getFrameNumber() {
        return frameNumber;
    }

    public void setFrameNumber(String frameNumber) {
        this.frameNumber = frameNumber;
    }

    public String isEndFrame() {
        return isEndFrame;
    }

    public void setEndFrame(String endFrame) {
        isEndFrame = endFrame;
    }

    public boolean isPassChecksum() {
        return isPassChecksum;
    }

    public void setPassChecksum(boolean passChecksum) {
        isPassChecksum = passChecksum;
    }

    public boolean isMessageFrame() {
        return isMessageFrame;
    }

    public void setMessageFrame(boolean messageFrame) {
        isMessageFrame = messageFrame;
    }

    @Override
    public String toString() {
        return "ParsedFrame{" +
                "originalMessage='" + originalMessage + '\'' +
                ", translatedMessage='" + translatedMessage + '\'' +
                ", messageSection='" + messageSection + '\'' +
                ", frame_number=" + frameNumber +
                ", isEndFrame=" + isEndFrame +
                ", isPassChecksum=" + isPassChecksum +
                ", isMessageFrame=" + isMessageFrame +
                '}';
    }
}
