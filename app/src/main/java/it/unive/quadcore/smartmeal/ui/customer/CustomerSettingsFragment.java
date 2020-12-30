package it.unive.quadcore.smartmeal.ui.customer;

import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;

public class CustomerSettingsFragment extends Fragment {

    private SwitchCompat notificationsSwitch;
    private SwitchCompat sensorsSwitch;
    private TextView changeNameTextView;
    private TextView logoutTextView;
    private TextView aboutTextView;

    public CustomerSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customer_settings, container, false);

        notificationsSwitch = root.findViewById(R.id.notifications_switch);
        sensorsSwitch = root.findViewById(R.id.sensors_switch);
        changeNameTextView = root.findViewById(R.id.change_name_text_view);
        logoutTextView = root.findViewById(R.id.logout_text_view);
        aboutTextView = root.findViewById(R.id.about_text_view);

        return root;
    }
}
