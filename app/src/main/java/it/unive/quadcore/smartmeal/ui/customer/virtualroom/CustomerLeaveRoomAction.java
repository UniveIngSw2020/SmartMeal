package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import java.util.Objects;

import it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.CustomerBottomNavigationActivity;

/**
 * Classe che implementa la callback da eseguire quando una richiesta
 * Nearby va in timeout.
 */
public class CustomerLeaveRoomAction implements Runnable {
    @NonNull
    private final Activity activity;

    @NonNull
    private final String snackbarMessage;

    public CustomerLeaveRoomAction(@NonNull Activity activity, @NonNull String snackbarMessage) {
        Objects.requireNonNull(activity);
        this.activity = activity;
        this.snackbarMessage = snackbarMessage;
    }

    @Override
    public void run() {
//        Snackbar.make(
//                activity.findViewById(android.R.id.content),
//                R.string.timeout_error,
//                BaseTransientBottomBar.LENGTH_LONG
//        ).show();

        Intent intent = new Intent(activity, CustomerBottomNavigationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CustomerBottomNavigationActivity.SHOW_SNACKBAR, snackbarMessage);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }
}
