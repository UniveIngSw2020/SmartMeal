package it.unive.quadcore.smartmeal.communication.confirmation;

import java.io.Serializable;

/**
 * Classe immutable.
 * Confirmation è concettualmente simile a Response, nello stesso modo in cui un Supplier è
 * concettualmente simile a una Function.
 * Un oggetto di tipo Confirmation è usato nella comunicazione per confermare qualcosa.
 * @param <E> possibile tipo di eccezione, nel caso la conferma sia negata
 */
public class Confirmation<E extends Exception> implements Serializable {
    public Confirmation() {}

    public void obtain() throws E {}
}
