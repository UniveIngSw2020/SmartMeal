package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.PayloadCallback;

public abstract class ConnectionListener extends ConnectionLifecycleCallback {
    private static final String TAG = "ConnectionListener";

    private Activity activity;
    private PayloadCallback payloadCallback;

    public ConnectionListener(Activity activity, PayloadCallback payloadCallback) {
        this.activity = activity;
        this.payloadCallback = payloadCallback;
    }

    @Override
    public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
        Log.d(TAG, "onConnectionInitiated");
        // Accetta automaticamente la connessione
        Nearby.getConnectionsClient(activity).acceptConnection(endpointId, payloadCallback);
    }

    @Override
    public void onConnectionResult(String endpointId, ConnectionResolution result) {
        // tutti i possibili stati
        // https://developers.google.com/android/reference/com/google/android/gms/nearby/connection/ConnectionsStatusCodes
        switch (result.getStatus().getStatusCode()) {
            case ConnectionsStatusCodes.STATUS_OK:
                // We're connected! Can now start sending and receiving data.
                Log.i(TAG, "onConnectionResult: STATUS_OK");

                onConnectionSuccess(endpointId);
                break;
            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                // The connection was rejected by one or both sides.
                Log.i(TAG, "onConnectionResult: STATUS_CONNECTION_REJECTED");
                break;
            case ConnectionsStatusCodes.STATUS_ERROR:
                // The connection broke before it was able to be accepted.
                Log.e(TAG, "onConnectionResult: STATUS_ERROR");
                break;
            default:
                // Unknown status code
                Log.e(TAG, "Unknown status code: " + result.getStatus().getStatusCode());
        }
    }

    @Override
    public void onDisconnected(String endpointId) {
        // We've been disconnected from this endpoint. No more data can be
        // sent or received.
        Log.d(TAG, "onDisconnected");
    }

    protected abstract void onConnectionSuccess(String endpointId);
}
