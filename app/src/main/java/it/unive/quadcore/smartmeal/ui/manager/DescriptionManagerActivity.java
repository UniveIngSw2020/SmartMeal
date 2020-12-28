package it.unive.quadcore.smartmeal.ui.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

public class DescriptionManagerActivity extends AppCompatActivity {

    private TextView localNameTextView;
    private TextView descriptionTextView;
    private ImageView localImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_manager);

        localNameTextView = findViewById(R.id.local_name_text_view_manager);
        descriptionTextView = findViewById(R.id.description_text_view_manager);
        localImageView = findViewById(R.id.local_image_view_manager);

        // mostra la descrizione del locale a schermo
        LocalDescription localDescription = ManagerStorage.getLocalDescription();
        localNameTextView.setText(localDescription.getName());
        descriptionTextView.setText(localDescription.getPresentation());
        localImageView.setImageResource(R.drawable.localpicture);
    }
}