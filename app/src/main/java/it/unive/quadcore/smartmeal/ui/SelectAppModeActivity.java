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
import it.unive.quadcore.smartmeal.ui.manager.InsertPasswordActivity;
import it.unive.quadcore.smartmeal.ui.manager.ManagerHomeActivity;

public class SelectAppModeActivity extends AppCompatActivity {

    private Button customerButton;
    private Button managerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO attenzione fa crashre l'app al secondo avvio!
        // TODO probabilmente aggiungere metodo isInitialized() in Storage
        // TODO oppure chiedere un'activity ogni volta che si usa un metodo di Storage
        try {
            Storage.initializeStorage(this);
        } catch (RuntimeException e) {

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
                  startActivity(new Intent(this, ManagerHomeActivity.class));
                  finish();
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

            startActivity(new Intent(SelectAppModeActivity.this, InsertPasswordActivity.class));
        });

    }
}
