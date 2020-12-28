package it.unive.quadcore.smartmeal.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.customer.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.InsertPersonalDataActivity;

public class SelectAppModeActivity extends AppCompatActivity {

    private Button customerButton;
    private Button managerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // inizializza Storage se non lo è già
        if (!Storage.isInitialized()) {
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
}
