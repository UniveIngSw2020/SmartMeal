package it.unive.quadcore.smartmeal.local;

// CLasse che rappresenta le eccezioni legate alle waiter notifications. E' un tipo di eccezione a controllo obbligatorio.

public class WaiterNotificationException extends Exception{

    WaiterNotificationException(String message){
        super(message);
    }

}
