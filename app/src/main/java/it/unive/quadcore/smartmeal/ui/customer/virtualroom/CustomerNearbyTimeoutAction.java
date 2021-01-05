package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;

/**
 * Classe che implementa la callback da eseguire quando una richiesta
 * Nearby va in timeout.
 */
public class CustomerNearbyTimeoutAction implements Runnable {
    @NonNull
    private final Activity activity;

    public CustomerNearbyTimeoutAction(@NonNull Activity activity) {
        Objects.requireNonNull(activity);
        this.activity = activity;
    }

    @Override
    public void run() {
        Snackbar.make(
                activity.findViewById(android.R.id.content),
                R.string.timeout_error,
                BaseTransientBottomBar.LENGTH_LONG
        ).show();

        Intent intent = new Intent(activity, CustomerBottomNavigationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CustomerBottomNavigationActivity.NEARBY_TIMEOUT_ARG, true);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
