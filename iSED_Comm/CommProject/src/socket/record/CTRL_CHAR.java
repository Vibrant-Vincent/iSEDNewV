package socket.record;


import java.util.ArrayList;
import java.util.List;

public class CTRL_CHAR {
    //CTRL_CHAR contains ASCII Control Characters
    //String matrix[A][B]
    //A: ASCII decimal code
    //B: 1. ASCII hexadecimal code 2. ASCII character symbol
    String[][] CTRL_CHAR = {
            {"00", "<NUL>"},
            {"01", "<SOH>"},
            {"02", "<STX>"},
            {"03", "<ETX>"},
            {"04", "<EOT>"},
            {"05", "<ENQ>"},
            {"06", "<ACK>"},
            {"07", "<BEL>"},
            {"08", "<BS>"},
            {"09", "<HT>"},
            {"0A", "<LF>"},
            {"0B", "<VT>"},
            {"0C", "<FF>"},
            {"0D", "<CR>"},
            {"0E", "<SO>"},
            {"0F", "<SI>"},
            {"10", "<DLE>"},
            {"11", "<DC1>"},
            {"12", "<DC2>"},
            {"13", "<DC3>"},
            {"14", "<DC4>"},
            {"15", "<NAK>"},
            {"16", "<SYN>"},
            {"17", "<ETB>"},
            {"18", "<CAN>"},
            {"19", "<EM>"},
            {"1A", "<SUB>"},
            {"1B", "<ESC>"},
            {"1C", "<FS>"},
            {"1D", "<GS>"},
            {"1E", "<RS>"},
            {"1F", "<US>"}
    };
}
