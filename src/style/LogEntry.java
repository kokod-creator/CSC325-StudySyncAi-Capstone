package style;

import javafx.beans.property.SimpleStringProperty;

public class LogEntry {

    private final SimpleStringProperty message;

    public LogEntry(String message){
        this.message = new SimpleStringProperty(message);
    }

    public String getMessage(){
        return message.get();
    }
}