package it.unive.quadcore.smartmeal.ui.customer;

import android.Manifest;
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

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.sensor.Sensor;
import it.unive.quadcore.smartmeal.storage.ApplicationMode;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;
import it.unive.quadcore.smartmeal.ui.SelectAppModeActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.callback.SendWelcomeNotificationCallback;
import it.unive.quadcore.smartmeal.util.PermissionHandler;

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

        notificationsSwitch = root.findViewById(R.id.customer_notifications_switch);
        sensorsSwitch = root.findViewById(R.id.customer_sensors_switch);
        changeNameTextView = root.findViewById(R.id.customer_change_name_text_view);
        logoutTextView = root.findViewById(R.id.customer_logout_text_view);
        aboutTextView = root.findViewById(R.id.customer_about_text_view);

        boolean notificationsEnabled = CustomerStorage.getNotificationMode();
        notificationsSwitch.setChecked(notificationsEnabled);
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                if (!PermissionHandler.hasNotificationsPermissions(getContext())) {
//                    ActivityResultLauncher<String> requestPermissionLauncher =
//                            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
//                                if (isGranted) {
//                                    CustomerStorage.setNotificationMode(isChecked);
//                                } else {
//                                    notificationsSwitch.setChecked(false);
//                                    CustomerStorage.setNotificationMode(false);
//                                }
//                            });
//                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
//                } else {
//                    CustomerStorage.setNotificationMode(isChecked);
//                }
//            } else {
//                CustomerStorage.setNotificationMode(isChecked);
//            }


            Sensor sensor = Sensor.getInstance();

            if (isChecked && !PermissionHandler.hasNotificationsPermissions(getContext())) {
                // utente ha attivato notifiche ma non ha i permessi per farlo

                notificationsSwitch.setChecked(false);
                sensor.endEntranceDetection();

                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.field_required_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            } else if (isChecked) {         // utente ha attivato notifiche e ha i permessi per farlo
                sensor.startEntranceDetection(new SendWelcomeNotificationCallback(getActivity()));
                CustomerStorage.setNotificationMode(true);
            } else {                        // utente ha disattivato notifiche
                sensor.endEntranceDetection();
                CustomerStorage.setNotificationMode(false);
            }

            Log.i(TAG, "Customer changed notifications settings: " + isChecked);
        });

        boolean sensorsEnabled = CustomerStorage.getSensorMode();
        sensorsSwitch.setChecked(sensorsEnabled);
        sensorsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked && !PermissionHandler.hasSensorsPermissions(getContext())) {
                sensorsSwitch.setChecked(false);
                Snackbar.make(
                        getActivity().findViewById(android.R.id.content),
                        R.string.field_required_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
                return;
            }

            CustomerStorage.setSensorMode(isChecked);

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
