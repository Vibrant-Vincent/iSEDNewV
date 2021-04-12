package socket;


import org.jetbrains.annotations.NotNull;
import socket.record.FrameFunctions;
import socket.record.LinkCommand;

import java.io.*;
import java.net.*;
//Bug : In sending MSG status, when server reply <NAK>
//client can not handle it right now
public class Client {
    final static char STX = (char) 2;
    final static char ETX = (char) 3;
    final static char EOT = (char) 4;
    final static char ENQ = (char) 5;
    final static char LF = (char) 10;
    final static char CR = (char) 13;
    final static char ETB = (char) 23;
    //    final static char ACK = (char) 6;
//    final static char NAK = (char) 21;
    final static String ACK = "<ACK>";
    final static String NAK = "<NAK>";
    public static void main(String[] args) throws Exception {
        Socket sock = new Socket("127.0.0.1", 3000);
        // reading from keyboard (keyRead object)
        BufferedReader keyRead = new BufferedReader(new InputStreamReader(System.in));
        // sending to client (pwrite object)
        OutputStream ostream = sock.getOutputStream();
        PrintWriter pwrite = new PrintWriter(ostream, true);
        // receiving from server ( receiveRead  object)
        InputStream istream = sock.getInputStream();
        BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
        System.out.println("Connection is established");
//        boolean autoSendingMsgStatus = false;
        int autoSendingMsgCount = 0;
        FrameFunctions frameFunctions = new FrameFunctions();
        String messageReceived = "", messageToSend = "", keyboardRead = "";
        boolean allMsgDelivered = false;
        String[] allMessageToSend = new String[]{
//                STX + "0H|\\^&|||||||||v8.00.0009|P|1|20080512103151" +
//                        CR + "P|1|8457|||||19540228|M|" + CR + "O|1|8701|^5100^1|^^^767|R|||||||||20080512103151|1|||||||||Modular" +
//                        CR + "R|1|^^^767|1|||27||OPER42^|||E11" + CR + "L|1|N" + CR + ETX +
//                        "41" + CR + LF,
//                STX + "0H|\\^&|||||||||v8.00.0009|P|1|20080512103151" +
//                        CR + "P|1|8457|||||19540228|M|" + CR + "O|1|8701|^5100^1|^^^767|R|||||||||20080512103151|1|||||||||Modular" +
//                        CR + "R|1|^^^767|1|||27||OPER42^|||E11" + CR + "L|1|N" + CR + ETX +
//                        "41" + CR + LF,
                STX +  "1O|1|8701|^5100^1|^^^767|R|||||||||20080512103151|1|||||||||Modular" +
                        CR + "R|1|^^^767|1|||27||OPER42^|||E12" + CR + "L|1|N" + CR + ETX +
                        "CE" + CR + LF,
//                STX +  "0O|1|8701|^5100^1|^^^767|R|||||||||20080512103151|1|||||||||Modular" +
//                        CR + "R|1|^^^767|1|||27||OPER42^|||E12" + CR + "L|1|N" + CR + ETB +
//                        "CE" + CR + LF,
                STX + "0H|\\^&|||||||||v8.00.0009|P|1|20080512103151" + CR +
                        "P|1|8457|||||19540228|M|" + CR +
                        "O|1|8701|^5100^1|^^^767|R|||||||||20080512103151|1|||||||||Modular" + CR +
                        "R|1|^^^767|1|||27||OPER42^|||E11" +
                        ETB + "41" + CR + LF,
        };
        int messageToSendCount = allMessageToSend.length;
        boolean iniCommunication = true;
        LinkCommand linkCommand = null;
        while (true) {
            System.out.println("===============================================================");
            if (iniCommunication || allMsgDelivered) {
                System.out.println("Please type <ENQ> to start communicating");

                keyboardRead = keyRead.readLine();  // keyboard reading
                iniCommunication = false;
                allMsgDelivered = false;
                System.out.println("initiate communicating...");
                System.out.println("----------length : " + keyboardRead.length());
                System.out.println("----------value : " + keyboardRead);
                if (keyboardRead.equals("<ENQ>")) {
                    System.out.println("sending <ENQ> to server");
                    sendToServer(ENQ + "", pwrite);    // sending to server
                } else {
                    sendToServer(keyboardRead, pwrite);    // sending to server
                }
            }
            messageReceived = receiveRead.readLine();//block
            linkCommand = frameFunctions.parseLinkCommand(messageReceived);
            System.out.println("-----111-----" + linkCommand.getParsedMessage());

            if (linkCommand != null) { //receive from server
                System.out.println("Got the message from server(Expected <ACK>): " + linkCommand.getParsedMessage());
                if (linkCommand.getParsedMessage().equals(ACK)) {
                    if (messageToSendCount > 0) {
                        System.out.println("Currently having " + messageToSendCount + " records to send");
                        messageToSend = getSendMessage(messageToSendCount, allMessageToSend);
                        messageToSendCount--;
                        System.out.println("Sending a message ...");
                        sendToServer(messageToSend, pwrite);//sending to server
                        Thread.sleep(500);
                    } else {
                        System.out.println("All records have been sent and send <EOT> to server to stop this transmission");
                        messageToSend = EOT + "";
                        sendToServer(messageToSend, pwrite);//sending to server
                        allMsgDelivered = true;
                        messageToSendCount = allMessageToSend.length;
                    }
                } else {
                    System.out.println("Did not get <ACK> from server, keep sending <ENQ> to server.");
                    iniCommunication = true;
                }
            } else {
                System.out.println("Did not get <ACK> from server, keep sending <ENQ> to server.");
                iniCommunication = true;
            }

        }
    }

    // sending to server
    private static void sendToServer(String input, @NotNull PrintWriter printWriter) {
        printWriter.println(input);
        printWriter.flush();                    // flush the data
    }

    private static String getSendMessage(int messageToSendCount, String[] messageToSend) {

        return messageToSend[messageToSendCount - 1];
    }

    private static String getSendMessage() {
        return "<STX>2H|\\^&|||||||||v8.00.0009|P|1|20080512103151\n" +
                "<CR>P|1|8457|||||19540228|M|<CR>O|1|8702|^5100^2|^^^773|S|||||||||\n" +
                "20080512103151|1|||||||||Modular<CR>\n" +
                "R|1|^^^773|-0.4|||27||F||OPER42^|||E12<CR>L|1|N<CR>\n" +
                "<ETX>CE<CR><LF>";
    }

}

