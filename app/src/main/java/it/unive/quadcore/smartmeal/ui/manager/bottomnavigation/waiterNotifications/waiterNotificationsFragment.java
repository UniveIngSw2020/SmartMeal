package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.waiterNotifications;

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


public class waiterNotificationsFragment extends Fragment {

    private waiterNotificationsViewModel waiterNotificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        waiterNotificationsViewModel =
                new ViewModelProvider(this).get(waiterNotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_waiter_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        waiterNotificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}