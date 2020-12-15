package it.unive.quadcore.smartmeal.local;

// Classe che rappresenta le eccezioni riguardanti lo stato invalido della stanza virtuale.
// E' un tipo di eccezione a controllo obbligatorio.
public class RoomStateException extends Exception{ // possibilità di fare Unchecked exception : sottoclasse di IllegalStateException

    private boolean roomState;

    // Costruttore visiible solo a Local
    RoomStateException(boolean roomState){
        super("This operation is not allowed in a room that is "+(roomState?"opened.":"closed."));
        this.roomState = roomState;
    }

    // TODO : perfezionare classe overraidando o aggiungendo metodi
}
