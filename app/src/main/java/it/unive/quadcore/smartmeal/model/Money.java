package it.unive.quadcore.smartmeal.model;

import android.content.Context;

import androidx.annotation.NonNull;

import it.unive.quadcore.smartmeal.R;

public class Money {
    private final int value;

    public Money(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getEuroString() {
        return String.format("%d", getValue() / 100);
    }

    public String getCentString() {
        int cent = getValue() % 100;
        return cent <= 9 ? String.format("0%d", cent) : String.format("%d", cent);
    }

    @NonNull
    @Override
    public String toString() {
        return "Money{" +
                "value=" + value +
                '}';
    }
}
