package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.LocalDescription;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;

    private TextView localNameTextView;
    private TextView descriptionTextView;
    private ImageView localImageView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_customer_home, container, false);

        localNameTextView = root.findViewById(R.id.local_name_text_view);
        descriptionTextView = root.findViewById(R.id.description_text_view);
        localImageView = root.findViewById(R.id.local_image_view);

        // mostra la descrizione del locale a schermo
        LocalDescription localDescription = CustomerStorage.getLocalDescription();
        localNameTextView.setText(localDescription.getName());
        descriptionTextView.setText(localDescription.getPresentation());
        localImageView.setImageResource(R.drawable.localpicture);

        return root;
    }
}