package socket;


import main.java.common.utils.EventLog;
import main.java.common.utils.EventType;
import main.java.lis.constant.PConstants;
import org.jetbrains.annotations.NotNull;
import socket.jar.DataBaseUtility;
import socket.record.FrameFunctions;
import socket.record.LinkCommand;
import socket.record.Pair;
import socket.record.ParsedFrame;
import socket.record.DB_TABLE_NAME;
//static import socket.record.DB_TABLE_NAME.communicateLogPath;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

//... when the server received EOT, the server does not reply ACK

public class Server {
    final static char EOT = (char) 4;
    final static char ENQ = (char) 5;
    final static char LF = (char) 10;
    final static char ACK = (char) 6;
    final static char NAK = (char) 21;

    static ServerSocket serverSocket = null;
    static Socket socket = null;
    static SimpleDateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
    static EventLog eventLog = new EventLog(Server.class.getName(), "Roche_Result", DB_TABLE_NAME.communicateLogPath + dateFormat.format(new Date()));
    static DataBaseUtility dbUtilityLocal = new DataBaseUtility("", PConstants.LOCAL);//LOCAL = "LOCAL"
    static boolean waitForReceiving = true;
    static boolean receivingFramesStatus = false;
    static boolean connectionLost = false;
    static int timeOut = 0;
    static int aSingleChar = -1;
    static int frameNum = 0;
    static int messageIndex = 0;
    static BufferedReader receiveRead;
    static String messageReceived = "";
    static StringBuffer receiveMsgBuffer = new StringBuffer();
    static FrameFunctions frameFunctions = new FrameFunctions();
    static List<Pair<String, Boolean>> messageList = new ArrayList<>();
    static LinkCommand linkCommand = null;
    static ParsedFrame parsedFrame = null;
    static OutputStream outputStream;
    static PrintWriter printWriter;
    static InputStream inputStream;

    public static void main(String[] args) throws Exception {
        initialSocket();
        TimeCountAndPopup timeCountAndPopup = new TimeCountAndPopup(System.currentTimeMillis(), System.currentTimeMillis(), 5 * 1000, false);
        timeCountAndPopup.start();
        HeartbeatTimeCount heartbeatTimeCount = new HeartbeatTimeCount(System.currentTimeMillis(), 10 * 1000, false);
        heartbeatTimeCount.start();
        heartbeatTimeCount.setTimeCountAndPopup(timeCountAndPopup);
//        DialogPop dialogPop = new DialogPop();
//        dialogPop.start();
        while (true) {
            try {
                if (waitForReceiving) {
                    System.out.println("=================Waiting for messages...=================");

                    initialInOutStream();
                    socket.setSoTimeout(timeOut);
                    /*
                     * To get message from client either linkCommand(ENQ/EOT) or message frames
                     */
                    getMessageFromClient(heartbeatTimeCount,timeCountAndPopup);

                    if (linkCommand != null && linkCommand.isLinkCommand()) {
                        System.out.println("Got a message and the message is link command : " + linkCommand.getParsedMessage());
//                                eventLog.eventLog(Level.INFO, EventType.SUCCESS, "This message is link command : " + linkCommand.getParsedMessage());
                        if (!receivingFramesStatus && linkCommand.getParsedMessage().equals("<ENQ>")) {
                            /*
                             * Got <ENQ> from client,
                             * Into receiving frames status,
                             * Reply <ACK> to client,
                             * Set timeout 30 seconds.
                             */
                            handleENQ();
                        } else if (linkCommand.getParsedMessage().equals("<EOT>")) {
                            /*
                             * Got <EOT> from client,and client has sent all frames of the message,
                             * Exit receiving frames status,
                             * Reply <ACK> to client,
                             * Set timeout 0 seconds.
                             * Insert received frames to localDB
                             */
                            handleEOT();
                        }
                    } else if (parsedFrame != null && parsedFrame.isMessageFrame()) {
                        /*
                         * Got message frame from client,
                         * Set timeout as 30 seconds.
                         */
//                      eventLog.eventLog(Level.INFO, EventType.SUCCESS, "This is message frame, not link command.");
                        System.out.println("-----------This is a message frame-----------");
                        timeOut = 30 * 1000;
                        if (parsedFrame.getFrameNumber().equals(Integer.toString(frameNum))) {//check frameNum
                            /*
                             * Passed frame number check,
                             * Check it is intermediate or end frame,
                             *  - If it is intermediate frame, frame number plus one, and keep waiting for next frame,
                             *  - If it is end frame, exit receiving frames status, set frame number as 0.
                             * Reply <ACK> to Client
                             */
                            handleMsgFrames();
                        } else {
                            /*
                             * Got repeat message, reply <ACK>.
                             * Fail frame number check, reply <NAK>.
                             */
                            handleBadMsgFrames();
                        }
                        parsedFrame = null;
                    } else {
                        /*
                         * Got illegal message, reply <NAK>.
                         * Set timeout as 0 second.
                         */
                        handleIllegalFrames();
                    }
                }
            } catch (SocketTimeoutException socketTimeoutException) {
                System.out.println("Receive message frames timeout");
                System.out.println("Drop all frames have received of the message, reply <NAK> and keep waiting new frames...");
                sendToClient(NAK + "", printWriter);

                messageList.clear();
                receivingFramesStatus = false;
                timeOut = 0;
            } catch (SocketException socketException) {
                socketException.printStackTrace();
            }
        }
    }

    private static void handleIllegalFrames() {
        System.out.println("--1-- Got illegal message and reply <NAK>");
        receivingFramesStatus = false;
        timeOut = 0;
        sendToClient(NAK + "", printWriter);
    }

    private static void handleBadMsgFrames() {
        if (parsedFrame.getFrameNumber().equals(Integer.toString(frameNum - 1))) {
            System.out.println("Got repeat message frame and keep waiting ...");
            sendToClient(ACK + "", printWriter);
        } else {
//                                        eventLog.eventLog(Level.INFO, EventType.FAILURE, "Fail frame number check.");
            System.out.println("Fail frame number check and send NAK.");
            sendToClient(NAK + "", printWriter);
        }
    }

    private static void handleMsgFrames() {
        //                                    eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Passed frame number check");
//                                    eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Store the frame in message list.");
        System.out.println("-----------Passed frame number check and Store the frame in message list-----------");
        messageList.add(new Pair<>(parsedFrame.getMessageSection(), parsedFrame.isEndFrame().equals("false")));//intermediate frame
//                                    System.out.println("-----------Show message list-----------" + messageList);
        if (parsedFrame.isEndFrame().equals("true")) {
//                                        eventLog.eventLog(Level.INFO, EventType.SUCCESS, "This is the end frame.");
            System.out.println("-----------This is the end frame, got all message frames-----------");
            receivingFramesStatus = false;
            frameNum = 0;
        } else {
//                                        eventLog.eventLog(Level.INFO, EventType.SUCCESS, "This is not the end frame and keep receiving frames...");
            System.out.println("-----------This is not the end frame and keep receiving frames...-----------");
            frameNum++;
            if (frameNum == 8) {
                frameNum = 0;
            }
        }
//                          eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Reply <ACK> to Client.");
        System.out.println("Reply <ACK> to Client");
        sendToClient(ACK + "", printWriter);
    }

    private static void handleEOT() {
        //                                    eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Got a message(Expected <EOT>) : " + linkCommand.getParsedMessage());
//                                    eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Client has sent all frames of the message.");
        System.out.println("Got a message(Expected <EOT>) : " + linkCommand.getParsedMessage());
        System.out.println("Client has sent all frames of the message.");
//                                    eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Reply <ACK> to Client.");
        receivingFramesStatus = false;
        /*
         * Get the max index of messages in database.
         */
        getMaxIndex();
        System.out.println("-----------Start to store all frames into database...-----------");
        for (int i = 0; i < messageList.size(); i++) {
            //pair.key: content , pair.value : isIntermediate
            insertReceivedFrameToLocalDB(dbUtilityLocal, messageList.get(i).key, messageList.get(i).value, i);
        }
        messageList.clear();
        timeOut = 0;
//                            System.out.println("Reply <ACK> to Client.");
//                            sendToClient(ACK + "", printWriter);
        linkCommand = null;
    }

    private static void initialInOutStream() throws IOException {
        outputStream = socket.getOutputStream();
        printWriter = new PrintWriter(outputStream, true);
        inputStream = socket.getInputStream();
        receiveRead = new BufferedReader(new InputStreamReader(inputStream));
    }

    private static void handleENQ() {
        //                            eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Client is asking for connection and got the message from client(Expected <ENQ>) : " + messageReceived);
//                            eventLog.eventLog(Level.INFO, EventType.SUCCESS, "Reply <ACK> to Client");
        System.out.println("Client is asking for connection and got the message from client(Expected <ENQ>) : " + linkCommand.getParsedMessage());
        System.out.println("Reply <ACK> to Client");
        sendToClient(ACK + "", printWriter);
        receivingFramesStatus = true;
        timeOut = 30 * 1000;
        linkCommand = null;
    }

    private static void getMessageFromClient(HeartbeatTimeCount heartbeatTimeCount, TimeCountAndPopup timeCountAndPopup ) throws IOException {

        if (!receivingFramesStatus) {//handle ENQ/EOT
            System.out.println("-----------Not in receiving message frames status-----------");
//            System.out.println("-----------heartbeatHasSent-----------" + timeCountAndPopup.isHeartbeatHasSent());
            aSingleChar = receiveRead.read(); //block
            /*
             * For heartbeat, in receiving ENQ/EOT status,
             * we take this status as receivingFramesStatus == true
             */
            heartbeatTimeCount.setReceivingFramesStatus(true);
            receiveMsgBuffer.append((char) aSingleChar);
            messageReceived = receiveMsgBuffer.toString();
            linkCommand = frameFunctions.parseLinkCommand(messageReceived);
            System.out.println("-----------Message got <Link Command> : " + linkCommand.getParsedMessage());
        } else {
            System.out.println("-----------In receiving message frames status-----------");
            /*
             * In receiving msg status, will not send heartbeat/
             */
            heartbeatTimeCount.setReceivingFramesStatus(true);
            do {
                aSingleChar = receiveRead.read();
                receiveMsgBuffer.append((char) aSingleChar);
            } while (aSingleChar != LF && aSingleChar != EOT);//10:<LF>    4:<EOT>
            messageReceived = receiveMsgBuffer.toString();
            parsedFrame = frameFunctions.parseFrame(messageReceived);
            System.out.println("-----------Message got <Message Frame>: " + parsedFrame.getTranslatedMessage());
        }
        receiveMsgBuffer = new StringBuffer();
        heartbeatTimeCount.setReceivingFramesStatus(false);
        heartbeatTimeCount.setStartTime(System.currentTimeMillis());
    }
//    public static class DialogPop extends Thread {
//        @Override
//        public void run() {
////            System.out.println("DialogPop Thread : Connection lost, Please restart server.");
//            UIManager.put("OptionPane.minimumSize",new Dimension(500,500));
//            JLabel label = new JLabel("Connection lost, Please restart server.");
//            label.setForeground(Color.RED);
//            label.setFont(new Font("Arial", Font.BOLD, 18));
//            JOptionPane.showMessageDialog(null,label,"WARNING",JOptionPane.WARNING_MESSAGE);
//        }
//    }
    public static class TimeCountAndPopup extends Thread {
        long startTime;
        long currentTime;
        long waitingInterval;
        boolean heartbeatHasSent;


        public TimeCountAndPopup(long startTime, long currentTime, long waitingInterval, boolean heartbeatHasSent) {
            this.startTime = startTime;
            this.currentTime = currentTime;
            this.waitingInterval = waitingInterval;
            this.heartbeatHasSent = heartbeatHasSent;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public void setHeartbeatHasSent(boolean heartbeatHasSent) {
            this.heartbeatHasSent = heartbeatHasSent;
        }

        public boolean isHeartbeatHasSent() {
            return heartbeatHasSent;
        }

        @Override
        public void run() {
//            System.out.println("======= heartbeatHasSent 1=======" + heartbeatHasSent);
            while (true) {
                currentTime = System.currentTimeMillis();
//                System.out.println("======= heartbeatHasSent 2=======" + heartbeatHasSent);
//                System.out.println("======= currentTime - startTime=======" + (currentTime - startTime));
//                if (currentTime - startTime > waitingInterval) {
//                    System.out.println("======= heartbeatHasSent 2=======" + heartbeatHasSent);
//                    System.out.println("======= currentTime - startTime=======" + (currentTime - startTime));
//                }
                if (currentTime - startTime > waitingInterval) {
                    if (heartbeatHasSent) {
                        System.out.println("==================pop up <connection lost> ...======================");
                        dialogPopup();
                    }
                    startTime = System.currentTimeMillis();
                } else {
                    try {
                        Thread.sleep(waitingInterval - (currentTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
    private static void dialogPopup() {
        eventLog.eventLog(Level.INFO, EventType.FAILURE, "Connection lost, Please restart server.");

        UIManager.put("OptionPane.minimumSize",new Dimension(500,500));
        JLabel label = new JLabel("Connection lost, Please restart server.");
        label.setForeground(Color.RED);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        JOptionPane.showMessageDialog(null,label,"WARNING",JOptionPane.WARNING_MESSAGE);
    }
    public static class HeartbeatTimeCount extends Thread {
        long startTime;
        long currentTime;
        long heartBeatInterval;
        boolean receivingFramesStatus;
        TimeCountAndPopup timeCountAndPopup;

        public void setTimeCountAndPopup(TimeCountAndPopup timeCountAndPopup) {
            this.timeCountAndPopup = timeCountAndPopup;
        }

        public boolean isReceivingFramesStatus() {
            return receivingFramesStatus;
        }

        public void setReceivingFramesStatus(boolean receivingFramesStatus) {
            this.receivingFramesStatus = receivingFramesStatus;
        }

        public HeartbeatTimeCount(long startTime, long heartBeatInterval, boolean receivingFramesStatus) {
            this.startTime = startTime;
            this.heartBeatInterval = heartBeatInterval;
            this.receivingFramesStatus = receivingFramesStatus;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public void run() {
            while (true) {
                currentTime = System.currentTimeMillis();
                if (currentTime - startTime > heartBeatInterval) {
                    if (!receivingFramesStatus) {
                        timeCountAndPopup.setHeartbeatHasSent(true);
                        timeCountAndPopup.setStartTime(System.currentTimeMillis());
                        sendHeartbeat();
                    }
                    startTime = System.currentTimeMillis();
                } else {
                    //not strict 10 mins
                    try {
                        Thread.sleep(heartBeatInterval - (currentTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void sendHeartbeat() {
        System.out.println("~~~~~~~~~~Sending heartbeat signal out~~~~~~~~~~");
        sendToClient(ENQ + "", printWriter);
    }

    private static void insertReceivedFrameToLocalDB(DataBaseUtility dbUtilityLocal, String frameSection, boolean isIntermediate, int frameIndex) {
        System.out.println("*** insert a frame section to database..." + frameSection);


//        String sql = "INSERT INTO receive_status_check (idx, frame_index, is_intermediate, frame_content) VALUES (?, ?, ?, ?)";
//        dbUtilityLocal.changeSQL(sql);
//        dbUtilityLocal.setInt(1, messageIndex);
//        dbUtilityLocal.setInt(2, frameIndex);
//        dbUtilityLocal.setInt(3, (isIntermediate?1:0));
//        dbUtilityLocal.setString(4, frameSection);
//        dbUtilityLocal.executeUpdate();

    }

    private static void getMaxIndex() {//start form 1
//        System.out.println("<<<<<<<<<<<<<<<<<<<<..............<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        String sql = "SELECT MAX(idx) as `index` from " + DB_TABLE_NAME.RECEIVE_STATUS_CHECK_TABLE;
        dbUtilityLocal.changeSQL(sql);
        dbUtilityLocal.generateRecords();
//        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<1  messageIndex : " + messageIndex);
        try (ResultSet resultSet = dbUtilityLocal.getRecords()) {
            if (resultSet.next()) {
                messageIndex = resultSet.getInt("index") + 1;
//                System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<2  messageIndex : " + messageIndex);
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    private static void initialSocket() throws IOException {
        if (serverSocket == null || serverSocket.isClosed()) {
            serverSocket = new ServerSocket(3000);
            System.out.println("Server is ready for communicating");
        }
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            try {
                socket = serverSocket.accept();//block
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    // sending to client
    private static void sendToClient(String input, @NotNull PrintWriter printWriter) {
        printWriter.println(input);
        printWriter.flush();                    // flush the data
    }
}

