package it.unive.quadcore.smartmeal.ui.customer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.SelectAppModeActivity;

public class CustomerSettingsFragment extends Fragment {

    private static final String TAG = "CustomerSettingsFrag";

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

        boolean notificationsEnabled = CustomerStorage.getNotificationMode();
        notificationsSwitch.setChecked(notificationsEnabled);
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CustomerStorage.setNotificationMode(isChecked);
            // TODO abilitare notifica tramite android (GPS + notifica)
            // TODO capire discorso permessi

            Log.i(TAG, "Customer changed notifications settings: " + isChecked);
        });

        boolean sensorsEnabled = CustomerStorage.getSensorMode();
        sensorsSwitch.setChecked(sensorsEnabled);
        sensorsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            CustomerStorage.setSensorMode(isChecked);
            // TODO abilitare sensore movimento
            // TODO capire discorso permessi

            Log.i(TAG, "Customer changed sensors settings: " + isChecked);
        });

        changeNameTextView.setOnClickListener(v -> {
            EditText nameEditText = new EditText(getContext());
            nameEditText.setText(CustomerStorage.getName());

            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.change_name_settings)
                    .setView(nameEditText)
                    .setPositiveButton(
                            R.string.confirmation_button_text,
                            (dialog, which) -> {
                                // preleva il nuovo nome dall'EditText
                                String newName = nameEditText.getText().toString().trim();
                                // aggiorna il nome in memoria
                                CustomerStorage.setName(newName);
                                Log.i(TAG, "Customer changed the name: " + newName);
                            }
                    )
                    .setNegativeButton(
                            R.string.cancellation_button_text,
                            (dialog, which) -> dialog.cancel()
                    )
                    .show();
        });

        logoutTextView.setOnClickListener(v -> {
            // imposta modalità applicazione predefinita
            CustomerStorage.setApplicationMode(ApplicationMode.UNDEFINED);

            // ritorna alla pagina di selezione modalità
            Intent intent = new Intent(getContext(), SelectAppModeActivity.class);
            // svuota il backstack
            intent.addFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK
            );
            startActivity(intent);
            getActivity().finish();
        });

        aboutTextView.setOnClickListener(v -> {
            TextView innerAboutTextView = new TextView(getContext());
            innerAboutTextView.setPadding(48, 0, 48, 0);
            // TODO miglior testo di about
            innerAboutTextView.setText("\nSmartMeal\nDesigned by Quadcore\n");

            new AlertDialog.Builder(getContext())
                    .setTitle(R.string.about_settings)
                    .setView(innerAboutTextView)
                    .show();
        });

        return root;
    }
}
