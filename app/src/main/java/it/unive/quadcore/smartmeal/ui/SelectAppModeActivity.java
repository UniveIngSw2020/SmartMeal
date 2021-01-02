package it.unive.quadcore.smartmeal.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.customer.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.InsertPersonalDataActivity;

public class SelectAppModeActivity extends AppCompatActivity {

    private static final String TAG = "SelectAppModeActivity";

    private static String[] getNearbyRequiredPermissions() {
        return new String[] {
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    private static String[] getNotificationsRequiredPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
            };
        } else {
            return new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
        }
    }

    private static String[] getSensorsRequiredPermissions() {
        return new String[] {
            // TODO capire che permessi servono
        };
    }

    private static String[] getAllRequiredPermissions() {
        ArrayList<String> requiredPermissions = new ArrayList<>();
        requiredPermissions.addAll(Arrays.asList(getNearbyRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getNotificationsRequiredPermissions()));
        requiredPermissions.addAll(Arrays.asList(getSensorsRequiredPermissions()));

        return requiredPermissions.toArray(new String[0]);
    }

    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;

    private Button customerButton;
    private Button managerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inizializza Storage se non lo è già
        if (!Storage.isInitialized()) {
            Log.i(TAG, "Storage initialized");
            Storage.initializeStorage(this);
        }

        // TODO attenzione rimuovere per versione finale
        // commentare / decommentare per poter cambiare modalità app
        Storage.setApplicationMode(ApplicationMode.UNDEFINED);

        switch (Storage.getApplicationMode()) {
            case UNDEFINED:
                setContentView(R.layout.activity_select_app_mode);
                break;
            case CUSTOMER:
                startActivity(new Intent(this, CustomerBottomNavigationActivity.class));
                finish();
                return;
            case MANAGER:
                // TODO rimpiazzare ManagerHomeActivity con il nome dell'Activity del Manager
                // e decommentare
//                startActivity(new Intent(this, ManagerHomeActivity.class));
//                finish();
                return;
            default:
                throw new IllegalStateException("Unexpected: Storage.getApplicationMode() returned null");
        }

        customerButton = findViewById(R.id.customer_button);
        managerButton = findViewById(R.id.manager_button);

        customerButton.setOnClickListener(v -> {
            // avvia l'activity che richiede i dati dell'utente
            startActivity(new Intent(SelectAppModeActivity.this, InsertPersonalDataActivity.class));
        });

        managerButton.setOnClickListener(v -> {
            // avvia l'activity che richiede la password per accedere alla modalità MANAGER
            // TODO rimpiazzare InsertPasswordActivity con il nome dell'Activity corretto

//            startActivity(new Intent(SelectAppModeActivity.this, InsertPasswordActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO testare, in teoria le altre versioni hanno permessi in automatico
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] requiredPermissions = getAllRequiredPermissions();
            if (!hasPermission(this, requiredPermissions)) {
                requestPermissions(requiredPermissions, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
            return;
        }

//        for (int i = 0; i < permissions.length; i++) {
//            if (permissions[i] = P)
//        }
//
//
//        for (int grantResult : grantResults) {
//            if
//
//
//            if (grantResult == PackageManager.PERMISSION_DENIED) {
//
//            }
//        }

        if (hasNotificationsPermissions(this)) {
            CustomerStorage.setNotificationMode(true);
        }

        if (hasSensorsPermissions(this)) {
            CustomerStorage.setNotificationMode(true);
        }
    }

    public static boolean hasNearbyPermissions(Context context) {
        return hasPermission(
                context,
                getNearbyRequiredPermissions()
        );
    }

    private static boolean hasNotificationsPermissions(Context context) {
        return hasPermission(
                context,
                getNotificationsRequiredPermissions()
        );
    }

    private static boolean hasSensorsPermissions(Context context) {
        return hasPermission(
                context,
                getSensorsRequiredPermissions()
        );
    }

    private static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
