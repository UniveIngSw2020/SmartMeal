package it.unive.quadcore.smartmeal.model;

import androidx.annotation.NonNull;

public class Money {
    private final int value;

    public Money(int value){
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
