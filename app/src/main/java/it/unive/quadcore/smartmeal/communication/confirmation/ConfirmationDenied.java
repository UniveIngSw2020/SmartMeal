package it.unive.quadcore.smartmeal.communication.confirmation;

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
