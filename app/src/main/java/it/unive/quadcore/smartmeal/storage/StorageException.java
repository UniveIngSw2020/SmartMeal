package it.unive.quadcore.smartmeal.storage;

// Possibilità: farla a controllo obbligatorio? Forse meglio di no
public class StorageException extends RuntimeException { // unchecked
    public StorageException(String s) {
        super(s);
    }
}
