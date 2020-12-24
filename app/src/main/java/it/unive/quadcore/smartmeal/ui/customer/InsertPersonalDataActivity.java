package it.unive.quadcore.smartmeal.ui.customer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
            String customerName = nameEditText.getText().toString();

            // TODO verificare che customerName non sia vuoto. (possibilmente disabilitare Button)

            CustomerStorage.setName(customerName);
            CustomerStorage.setApplicationMode(ApplicationMode.CUSTOMER);

            Log.i(TAG, "Customer name stored: " + customerName);

            // avvia l'activity principale del Cliente
            startActivity(new Intent(InsertPersonalDataActivity.this, CustomerHomeActivity.class));
            // TODO Gestire tasto indietro (deve far uscire dall'app)
        });
    }
}
