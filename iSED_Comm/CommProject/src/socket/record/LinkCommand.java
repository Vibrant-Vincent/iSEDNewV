package socket.record;

public class LinkCommand {
    private String originalMessage;
    private String parsedMessage;
    private boolean isLinkCommand;

    public LinkCommand (String originalMessage, String parsedMessage, boolean isLinkCommand) {
        this.originalMessage = originalMessage;
        this.parsedMessage = parsedMessage;
        this.isLinkCommand = isLinkCommand;

    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getParsedMessage() {
        return parsedMessage;
    }

    public void setParsedMessage(String parsedMessage) {
        this.parsedMessage = parsedMessage;
    }

    public boolean isLinkCommand() {
        return isLinkCommand;
    }

    public void setLinkCommand(boolean linkCommand) {
        isLinkCommand = linkCommand;
    }

    @Override
    public String toString() {
        return "LinkCommand{" +
                "originalMessage='" + originalMessage + '\'' +
                ", parsedMessage='" + parsedMessage + '\'' +
                ", isLinkCommand=" + isLinkCommand +
                '}';
    }
}
