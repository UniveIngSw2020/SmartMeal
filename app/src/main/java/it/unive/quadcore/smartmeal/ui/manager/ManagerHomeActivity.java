package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.ui.SettingsActivity;

public class ManagerHomeActivity extends AppCompatActivity {

    private Button roomButton;
    private Button menuButton;
    private Button descriptionButton;
    private ImageView settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_home);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_manager_home);

        this.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.manager_app_bar);
        //getSupportActionBar().setElevation(0);
        View view = getSupportActionBar().getCustomView();
        settings = view.findViewById(R.id.toolbar_settings);

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
        settings.setOnClickListener(v -> {
            startActivity(new Intent(ManagerHomeActivity.this, SettingsActivity.class));
        });
    }

 }