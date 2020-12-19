package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class Communication {
    private static final String TAG = "Communication";

    protected static final Strategy STRATEGY = Strategy.P2P_STAR;
    protected static final String SERVICE_ID = "it.unive.quadcore.smartmeal";

    protected Activity activity;                  // TODO assegnare valore


    protected void sendMessage(String toEndpointId, Message response) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(response);
            Payload filePayload = Payload.fromBytes(outputStream.toByteArray());
            Nearby.getConnectionsClient(activity).sendPayload(toEndpointId, filePayload);
        } catch (IOException e) {
            Log.wtf(TAG, "Unexpected output IOException: " + e);
            throw new AssertionError("Unexpected output IOException");
        }
    }

}
