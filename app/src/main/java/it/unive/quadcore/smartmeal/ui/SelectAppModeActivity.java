package it.unive.quadcore.smartmeal.ui;

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

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.customer.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.InsertPersonalDataActivity;

public class SelectAppModeActivity extends AppCompatActivity {

    private static final String TAG = "SelectAppModeActivity";

    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

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
            if (!hasPermission(this, REQUIRED_PERMISSIONS)) {
                requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
            }
        }
    }


// TODO la tengo in caso serva in futuro

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode != REQUEST_CODE_REQUIRED_PERMISSIONS) {
//            return;
//        }
//
//        for (int grantResult : grantResults) {
//            if (grantResult == PackageManager.PERMISSION_DENIED) {
//                Snackbar.make(
//                        findViewById(android.R.id.content),
//                        "Required",
//                        BaseTransientBottomBar.LENGTH_LONG
//                ).show();
//            }
//        }
//    }

    private static boolean hasPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
