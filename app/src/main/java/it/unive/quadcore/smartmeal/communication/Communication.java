package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

abstract class Communication {
    @NonNull
    private static final String TAG = "Communication";

    @NonNull
    protected static final Strategy STRATEGY = Strategy.P2P_STAR;
    @NonNull
    protected static final String SERVICE_ID = "it.unive.quadcore.smartmeal";

    @Nullable
    protected Activity activity;                  // TODO assegnare valore


    protected void sendMessage(String toEndpointId, Message response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(response);
            Payload filePayload = Payload.fromBytes(outputStream.toByteArray());
            assert activity != null;
            Nearby.getConnectionsClient(activity).sendPayload(toEndpointId, filePayload);
        } catch (IOException e) {
            Log.wtf(TAG, "Unexpected output IOException: " + e);
            throw new AssertionError("Unexpected output IOException");
        }
    }

}
