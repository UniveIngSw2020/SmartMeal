package it.unive.quadcore.smartmeal.communication.confirmation;

/**
 * Classe immutable.
 * Sottoclasse di Confirmation, un oggetto di tipo ConfirmationDenied è creato quando si vuole
 * comunicare che una richiesta è fallita e quindi la Conferma richiesta dev'essere negata.
 * @param <E> tipo del parametro dell'eccezione che si vuole mandare quando qualcuno prova a
 *           ottenere la richiesta (passata direttamente al momento della costruzione)
 */
public class ConfirmationDenied<E extends Exception> extends Confirmation<E> {
    private final E exception;

    public ConfirmationDenied(E exception) {
        this.exception = exception;
    }

    @Override
    public void obtain() throws E {
        throw exception;
    }
}
