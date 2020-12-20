package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
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
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public abstract class CustomerCommunication extends Communication {
    private static final String TAG = "CustomerCommunication";

    @Nullable
    private String managerEndpointId;

    @Nullable
    private Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback;
    private boolean connected;

    public static CustomerCommunication getInstance() {
        // TODO
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore


    public void joinRoom(Activity activity) {
        this.activity = activity;

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

        final PayloadCallback payloadCallback = new MessageListener() {
            @Override
            protected void onMessageReceived(@NonNull String endpointId, @NonNull Message message) {
                Objects.requireNonNull(endpointId);
                Objects.requireNonNull(message);

                // TODO continuare
                switch (message.getRequestType()) {
                    case CUSTOMER_NAME:
                        handleCustomerNameConfirmation(message.getContent());
                    case FREE_TABLE_LIST:
                        handleFreeTableListResponse(message.getContent());
                    default:
                        throw new UnsupportedOperationException("Not implemented yet");
                }
            }
        };

        return new ConnectionListener(activity, payloadCallback) {
            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                managerEndpointId = endpointId;
                sendName();
            }
        };
    }

    private void sendName() {
        sendMessage(managerEndpointId, new Message(RequestType.CUSTOMER_NAME, CustomerStorage.getName()));
    }

    protected void handleCustomerNameConfirmation(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        Confirmation<CustomerNotRecognizedException> confirmation = (Confirmation<CustomerNotRecognizedException>) content;
        try {
            confirmation.obtain();
            connected = true;
            Log.i(TAG, "Connection confirmed");
        } catch (CustomerNotRecognizedException e) {
            Log.e(TAG, "Connection not confirmed");
            sendName();

            // TODO eventualmente limitare i tentativi di connessione
        }
    }

    private void handleFreeTableListResponse(Serializable content) {
        // if (content instanceof SortedSet) TODO pensarci
        freeTableListCallback.accept((Response<TreeSet<Table>, TableException>) content);
    }


    public abstract void notifyWaiter(Consumer<Confirmation<? extends WaiterNotificationException>> onResponse);
    public abstract void selectTable(Table table, Consumer<Confirmation<? extends TableException>> onResponse);

    public void requestFreeTableList(Consumer<Response<TreeSet<Table>, ? extends TableException>> consumer){
        freeTableListCallback = consumer;
        sendMessage(managerEndpointId, new Message(RequestType.FREE_TABLE_LIST, null)); //TODO content
    }


    /**
     * La callback `onCloseRoomCallback` verrà chiamata nel caso il gestore chiuda la stanza
     * e il cliente sia ancora connesso ad essa.
     *
     * @param onCloseRoomCallback callback che implementa la logica da attuare quando il
     *                            gestore chiude la stanza
     */
    public abstract void onCloseRoom(Runnable onCloseRoomCallback);


    /**
     * Disconnette il cliente dalla stanza del gestore.
     */
    public abstract void leaveRoom();


    /**
     * Verifica se il cliente è connesso alla stanza del gestore.
     *
     * @return true se il cliente è connesso alla stanza del gestore, false altrimenti
     */
    public abstract boolean isConnected();
}
