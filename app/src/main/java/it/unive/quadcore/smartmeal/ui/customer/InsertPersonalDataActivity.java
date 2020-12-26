package it.unive.quadcore.smartmeal.ui.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class InsertPersonalDataActivity extends AppCompatActivity {
    private static final String TAG = "InsertPersonalDataAct";

    private EditText nameEditText;
    private Button confirmationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_personal_data);

        nameEditText = findViewById(R.id.name_edit_text);
        confirmationButton = findViewById(R.id.confirmation_button);

        confirmationButton.setOnClickListener(v -> {
            // TODO eventaulmente aggiungere controlli validità nome

            String customerName = nameEditText.getText().toString().trim();

            if (customerName.isEmpty()) {
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.field_required_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            CustomerStorage.setName(customerName);
            CustomerStorage.setApplicationMode(ApplicationMode.CUSTOMER);

            Log.i(TAG, "Customer name stored: " + customerName);

            // avvia l'activity principale del Cliente
            Intent intent = new Intent(InsertPersonalDataActivity.this, CustomerBottomNavigationActivity.class);
            // svuota il backstack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            finish();
        });
    }
}
