package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public abstract class CustomerCommunication {
    private static final String TAG = "CustomerCommunication";

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    private static final String SERVICE_ID = "it.unive.quadcore.smartmeal";

    @Nullable
    private String managerEndpointId;
    @Nullable
    private Activity activity;                  // TODO assegnare valore
    @Nullable
    private Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback;

    public static CustomerCommunication getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore


    public void joinRoom() {

        final EndpointDiscoveryCallback endpointDiscoveryCallback = endpointDiscoveryCallback();

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(activity)
                .startDiscovery(
                        SERVICE_ID,
                        endpointDiscoveryCallback,
                        discoveryOptions
                )
                .addOnSuccessListener((Void unused) -> {
                    Log.i(TAG, "Successfully started discovery");
                })
                .addOnFailureListener((Exception e) -> {
                    Log.e(TAG, "Discovery failed");
                });
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
                // An endpoint was found. We request a connection to it.
                Nearby.getConnectionsClient(activity)
                        .requestConnection(CustomerStorage.getName(), endpointId, connectionLifecycleCallback())
                        .addOnSuccessListener(
                                (Void unused) -> {
                                    // We successfully requested a connection. Now both sides
                                    // must accept before the connection is established.
                                })
                        .addOnFailureListener(
                                (Exception e) -> {
                                    // Nearby Connections failed to request the connection.
                                });
            }

            @Override
            public void onEndpointLost(String endpointId) {
                // A previously discovered endpoint has gone away.
            }
        };
    }

    private ConnectionLifecycleCallback connectionLifecycleCallback() {

        final PayloadCallback payloadCallback = new PayloadCallback() {
            @Override
            public void onPayloadReceived(String endpointId, Payload payload) {
                if (payload.getType() != Payload.Type.BYTES) {
                    Log.wtf(TAG, "Received a non byte Payload");
                    return;
                }

                // This always gets the full data of the payload. Will be null if it's not a BYTES
                // payload. You can check the payload type with payload.getType().
                try {
                    final byte[] receivedBytes = payload.asBytes();
                    ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(receivedBytes));

                    Message message = (Message) objectInputStream.readObject();
                    RequestType requestType = message.getRequestType();
                    Log.i(TAG, "Message received: " + requestType);

                    // TODO continuare
                    switch (requestType) {
                        case FREE_TABLE_LIST:
                            handleFreeTableListResponse(message.getContent());
                        default:
                            throw new UnsupportedOperationException("Not implemented yet");
                    }

                } catch (IOException e) {
                    Log.wtf(TAG, "Unexpected input IOException: " + e);
                    throw new AssertionError("Unexpected input IOException");
                } catch (ClassNotFoundException | ClassCastException e) {
                    Log.wtf(TAG, "Payload was not a Message: " + e);
                    throw new AssertionError("Payload was not a Message");
                }
            }

            @Override
            public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {
                // Bytes payloads are sent as a single chunk, so you'll receive a SUCCESS update immediately
                // after the call to onPayloadReceived().
            }
        };

        return new ConnectionLifecycleCallback() {
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
                        managerEndpointId = endpointId;
                        // We're connected! Can now start sending and receiving data.
                        Log.i(TAG, "onConnectionResult: STATUS_OK");
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
        };
    }

    private void handleFreeTableListResponse(Serializable content) {
        // if (content instanceof SortedSet) TODO pensarci
        freeTableListCallback.accept((Response<TreeSet<Table>, TableException>) content);
    }

    private void sendMessage(String toEndpointId, Message response) {
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


    public abstract void notifyWaiter(Consumer<Response<Serializable, ? extends WaiterNotificationException>> onResponse);
    public abstract void selectTable(Table table, Consumer<Response<Serializable, ? extends TableException>> onResponse);

    public void requestFreeTableList(Consumer<Response<TreeSet<Table>, ? extends TableException>> consumer){
        freeTableListCallback = consumer;
        sendMessage(managerEndpointId, new Message(RequestType.FREE_TABLE_LIST, null)); //TODO content
    }


    // TODO onCloseRoom(callback) per gestire evento generato dal closeRoom gestore

    public abstract void leaveRoom();
    public abstract boolean isConnected();
}
