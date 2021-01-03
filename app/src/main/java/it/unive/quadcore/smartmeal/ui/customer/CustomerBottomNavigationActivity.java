package it.unive.quadcore.smartmeal.ui.customer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.ui.SelectAppModeActivity;
import it.unive.quadcore.smartmeal.ui.SettingsActivity;
import it.unive.quadcore.smartmeal.ui.customer.virtualroom.CustomerVirtualRoomActivity;
import it.unive.quadcore.smartmeal.util.PermissionHandler;

public class CustomerBottomNavigationActivity extends AppCompatActivity {

    private FloatingActionButton startCustomerVirtualRoomFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_bottom_navigation);
        BottomNavigationView navView = findViewById(R.id.customer_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        startCustomerVirtualRoomFab = findViewById(R.id.start_customer_virtual_room_fab);
        startCustomerVirtualRoomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO probabilmente ha senso creare una classe apposita per controllo permessi
                if (!PermissionHandler.hasNearbyPermissions(CustomerBottomNavigationActivity.this)) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            R.string.field_required_snackbar, // TODO cambiare stringa
                            BaseTransientBottomBar.LENGTH_LONG
                    ).show();
                    return;
                }

                startActivity(new Intent(
                        CustomerBottomNavigationActivity.this,
                        CustomerVirtualRoomActivity.class
                ));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            startActivity(new Intent(
                    CustomerBottomNavigationActivity.this,
                    SettingsActivity.class
            ));
        }

        return super.onOptionsItemSelected(item);
    }
}
