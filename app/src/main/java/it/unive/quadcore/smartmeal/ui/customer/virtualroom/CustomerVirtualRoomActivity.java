package it.unive.quadcore.smartmeal.ui.customer.virtualroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import it.unive.quadcore.smartmeal.R;

public class CustomerVirtualRoomActivity extends AppCompatActivity {

    private static final String TAG = "CustomerVirtualRoomAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_virtual_room);

        setFragment(new ChooseTableFragment());
    }

    private void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.customer_room_fragment_container, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {

        // mostra un Dialog di conferma
        TextView confirmTextView = new TextView(this);
        String leaveConfirmationText = getString(R.string.leave_virtual_room_dialog_text);

        confirmTextView.setText(leaveConfirmationText);
        confirmTextView.setPadding(48, 0, 48, 0);

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_table)
                .setView(confirmTextView)
                .setPositiveButton(
                        R.string.confirmation_button_text,
                        (dialog, which) -> {
                            Log.i(TAG, "Leave virtual room confirmed:");
                            finish();
                        }
                )
                .setNegativeButton(
                        R.string.cancellation_button_text,
                        (dialog, which) -> dialog.cancel()
                )
                .show();
    }
}
