package it.unive.quadcore.smartmeal.ui.manager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import it.unive.quadcore.smartmeal.R;
import it.unive.quadcore.smartmeal.local.Local;
import it.unive.quadcore.smartmeal.local.RoomStateException;
import it.unive.quadcore.smartmeal.sensor.Sensor;
import it.unive.quadcore.smartmeal.ui.SettingsActivity;

import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

// Activity home page gestore
public class ManagerHomeActivity extends AppCompatActivity {

    private Button roomButton;
    private Button menuButton;
    private Button descriptionButton;
    private Sensor s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_manager_home);

        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.title_manager_home);

        roomButton = findViewById(R.id.button_home_manager_room);
        menuButton = findViewById(R.id.button_home_manager_menu);
        descriptionButton = findViewById(R.id.button_home_manager_description);


        // TODO : rimuovere. Solo testing

        s = Sensor.getInstance();
        s.startEntranceDetection(()->{
            Snackbar.make(
                    findViewById(android.R.id.content),
                    R.string.geofence_entrance,
                    BaseTransientBottomBar.LENGTH_LONG
            ).show();

           // Mostra una notifica
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                    .setContentTitle("Notifica")
                    .setContentText("Testo notifica")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(1234, builder.build());

        },this);

        /*  FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> location = fusedLocationProviderClient.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                new CancellationTokenSource().getToken()
        );
        // Task<Location> location = fusedLocationProviderClient.getLastLocation();

        location.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                System.out.println("Device's location is: " + location.getResult().getLatitude() + "," + location.getResult().getLongitude());
            }
        }); */


        // Fine testing

        roomButton.setOnClickListener(v -> { // Si vuole accedere alla stanza virtuale

            // Avvio la stanza virtuale
            try {
                Local.getInstance().createRoom(this);

                // Local.getInstance().testingUI(); // Testing

                // avvia l'activity stanza virtuale gestore
                startActivity(new Intent(ManagerHomeActivity.this, ManagerRoomBottomNavigationActivity.class));
            } catch (RoomStateException e) { // Errore
                Snackbar.make(
                        findViewById(android.R.id.content),
                        R.string.error_manager_room_snackbar,
                        BaseTransientBottomBar.LENGTH_LONG
                ).show();
            }
        });

        menuButton.setOnClickListener(v -> {
            // avvia l'activity che mostra menu

            startActivity(new Intent(ManagerHomeActivity.this, MenuManagerActivity.class));
        });

        descriptionButton.setOnClickListener(v -> {
            // avvia l'activity che mostra descrizione

            startActivity(new Intent(ManagerHomeActivity.this, DescriptionManagerActivity.class));
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
                    ManagerHomeActivity.this,
                    SettingsActivity.class
            ));
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO : rimuovere, solo testing
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
        s.startEntranceDetection(()->{
            startForegroundService( new Intent( this, NotificationService.class ));
        },this);
    }
}