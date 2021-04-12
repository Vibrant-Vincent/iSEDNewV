package socket.record;

public class  FrameFunctions {
    public LinkCommand parseLinkCommand(String messageReceived) {

        CTRL_CHAR CC = new CTRL_CHAR();

        //return_message[2]
        //[0]: original message
        //[1]: translated message
        //[2]: message type check result("1":link command/"0": not link command)
//        String[] return_message = new String[3];
//        return_message[0] = receive_message;
//        return_message[1] = "";
        LinkCommand returnedMessage = new LinkCommand("","",false);
        returnedMessage.setOriginalMessage(messageReceived);
        returnedMessage.setLinkCommand(false);

        //check if receive_message length is 1
        if (messageReceived.length() == 1) {
            char char_i = messageReceived.charAt(0);
//            && ((int)char_i == 4 || (int)char_i == 5
            if (Character.isISOControl(char_i)  ) {//EOT:4  ENQ:5
                returnedMessage.setParsedMessage(returnedMessage.getParsedMessage() + CC.CTRL_CHAR[(int) char_i][1]);
                returnedMessage.setLinkCommand(true);
            } else {
                returnedMessage.setParsedMessage(returnedMessage.getParsedMessage() + char_i);
                returnedMessage.setLinkCommand(false);
            }

        } else {
            returnedMessage.setLinkCommand(false);
        }

        return returnedMessage;

    }
//<STX>+FN+Message_Section + <ETB>/<ETX>+CL+<CR>+<CF>
    /***********NOTE: CHECK SUM FAIL!!!!************/
    public ParsedFrame parseFrame(String receivedMessage) {
        CTRL_CHAR CC = new CTRL_CHAR();
        int char_sum = 0;
        String CL = "";
        ParsedFrame parsedFrame = new ParsedFrame(true, true,"", "","");
        char char_0 = receivedMessage.charAt(0);
        if ((int) char_0 == 2) {//check if 1st char is <STX>
            parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CC.CTRL_CHAR[(int) char_0][1]);
            //<STX>+FN...
            //Get frame number
            parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + receivedMessage.charAt(1));
            parsedFrame.setFrameNumber(receivedMessage.substring(1,2));
            char_sum = char_sum + (int) receivedMessage.charAt(1);//sum character ASCII value
//...Message Section + <ETB>/<ETX>
            int index_section_1 = -2;
            for (int i = 2; i <= receivedMessage.length() - 1; i++) {
                char char_i = receivedMessage.charAt(i);
                if (!((int)(char_i)==3 || (int)(char_i)==23)) {//Message Section
                    if (Character.isISOControl(char_i)) {
                        parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CC.CTRL_CHAR[(int)char_i][1]);

                        parsedFrame.setMessageSection(parsedFrame.getMessageSection());

                    } else {
                        parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + char_i);

                        parsedFrame.setMessageSection(parsedFrame.getMessageSection() + char_i);


                    }
//                    parsedFrame.setMessageSection(parsedFrame.getMessageSection() + char_i);
//                    System.out.println("....................>>>>>" + char_i);
//                    System.out.println("............>>>>>>>>>>>>" + parsedFrame.getMessageSection());

                } else {//Find <ETB>/<ETX>
                    parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CC.CTRL_CHAR[(int) char_i][1]);
                    index_section_1 = i;
                    //Check <ETB>/<ETX>
                    if ((int) char_i == 3) {
                        parsedFrame.setEndFrame("true");
                    } else if ((int) char_i == 23) {//ETB
                        parsedFrame.setEndFrame("false");
                    } else {
                        parsedFrame.setEndFrame("UNKNOW");
                    }
                    char_sum = char_sum + (int)char_i;//sum character ASCII value
                    break;//once reach <ETB>/<ETX>, exit for loop
                }
                char_sum = char_sum + (int)char_i;//sum character ASCII value
            }
            //...CL+<CR>+<CF>
            if (receivedMessage.substring(index_section_1 + 1).length() == 4) {//check the message tail length, must be 4
                CL = CL + receivedMessage.substring(index_section_1 + 1, index_section_1 + 3);
                parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CL);
                if ((int) receivedMessage.charAt(index_section_1 + 3) == 13) {
                    parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CC.CTRL_CHAR[(int) receivedMessage.charAt(index_section_1 + 3)][1]);
                } else {
                    System.out.println("Error:Fail <CR> check");
                    parsedFrame.setMessageFrame(false);//Fail <CR> check
                }
                if ((int) receivedMessage.charAt(index_section_1 + 4) == 10) {
                    parsedFrame.setTranslatedMessage(parsedFrame.getTranslatedMessage() + CC.CTRL_CHAR[(int) receivedMessage.charAt(index_section_1 + 4)][1]);
                } else {
                    System.out.println("Error:Fail <LF> check");
                    parsedFrame.setMessageFrame(false); //Fail <LF> check
                }
                //...check char_sum
                int CL_decimal = Integer.parseInt(CL, 16);
                String char_sum_string = Integer.toHexString(char_sum);
                if (char_sum_string.length() % 2 == 1) {//If the length of char_sum_string is odd, add '0' before it
                    char_sum_string = "0" + char_sum_string;
                }
                int char_sum_string_length = char_sum_string.length();
                String char_sum_CL = char_sum_string.substring(char_sum_string_length - 2);
                if (CL_decimal == Integer.parseInt(char_sum_CL, 16)) {
                    parsedFrame.setPassChecksum(true);//pass character sum check
                } else {
                    parsedFrame.setPassChecksum(false);//fail character sum check
                }
            } else {
                System.out.println("Error:Fail message tail length check(!=4)");
                parsedFrame.setMessageFrame(false);//Fail message tail length check(!=4)
            }
        } else {
            parsedFrame.setMessageFrame(false);//Fail message tail length check(!=4)
        }
        return parsedFrame;
    }
}
