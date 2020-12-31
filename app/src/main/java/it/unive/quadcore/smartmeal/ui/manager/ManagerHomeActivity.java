package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
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

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_manager_home);

        roomButton = findViewById(R.id.button_home_manager_room);
        menuButton = findViewById(R.id.button_home_manager_menu);
        descriptionButton = findViewById(R.id.button_home_manager_description);

        roomButton.setOnClickListener(v -> {

            // Avvio la stanza virtuale
            try {
                Local.getInstance().createRoom(this);

                Local.getInstance().testingUI(); // TODO : togliere. SOlo per testing

                // avvia l'activity stanza virtuale gestore
                startActivity(new Intent(ManagerHomeActivity.this, ManagerRoomBottomNavigationActivity.class));
            } catch (RoomStateException e) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.error_manager_room_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
        });

        menuButton.setOnClickListener(v -> {
            // avvia l'activity che mostra menu

            startActivity(new Intent(ManagerHomeActivity.this, MenuManagerActivity.class));
        });

        descriptionButton.setOnClickListener(v -> {
            // avvia l'activity che mostra descrizione

            startActivity(new Intent(ManagerHomeActivity.this, DescriptionManagerActivity.class));
        });

        // TODO : impostazioni

    }

 }