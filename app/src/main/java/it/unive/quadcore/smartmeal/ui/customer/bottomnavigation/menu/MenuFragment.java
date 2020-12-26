package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.model.Menu;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class MenuFragment extends Fragment {
    // TODO autogenerato

    private MenuViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        Menu menu = CustomerStorage.getLocalDescription().getMenu();


//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}
