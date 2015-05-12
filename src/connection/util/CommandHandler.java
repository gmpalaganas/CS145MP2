package connection.util;

//Handles Extraction of commands and parameters given a message sent by server or client
public static class CommandHandler{

    public static String getCommand(String msg){
        
        return msg.split(" ")[0];

    }
    
    public static String[] getParameters(String command, String msg){
        
        msg = msg.replace(command + " ", "");
        return msg.split(" ");
        
    }
    
}
