package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.Storage;
import it.unive.quadcore.smartmeal.ui.SelectAppModeActivity;
import it.unive.quadcore.smartmeal.ui.customer.CustomerBottomNavigationActivity;
import it.unive.quadcore.smartmeal.ui.customer.InsertPersonalDataActivity;

public class ManagerHomeActivity extends AppCompatActivity {

    private Button roomButton;
    private Button menuButton;
    private Button descriptionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_home);

        roomButton = findViewById(R.id.button_home_manager_room);
        menuButton = findViewById(R.id.button_home_manager_menu);
        descriptionButton = findViewById(R.id.button_home_manager_description);

        roomButton.setOnClickListener(v -> {
            // avvia l'activity che richiede i dati dell'utente
            startActivity(new Intent(ManagerHomeActivity.this, InsertPersonalDataActivity.class));
        });

        menuButton.setOnClickListener(v -> {
            // avvia l'activity che richiede la password per accedere alla modalità MANAGER

            startActivity(new Intent(ManagerHomeActivity.this, InsertPasswordActivity.class));
        });

        descriptionButton.setOnClickListener(v -> {
            // avvia l'activity che richiede la password per accedere alla modalità MANAGER

            startActivity(new Intent(ManagerHomeActivity.this, InsertPasswordActivity.class));
        });

    }
}