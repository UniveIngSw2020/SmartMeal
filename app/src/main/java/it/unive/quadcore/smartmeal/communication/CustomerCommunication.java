package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;

import java.io.Serializable;
import java.util.Objects;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class CustomerCommunication extends Communication {
    @NonNull
    private static final String TAG = "CustomerCommunication";

    @Nullable
    private String managerEndpointId;

    @Nullable
    private Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback;

    private boolean connected;
    private boolean isDiscovering;

    @Nullable
    private Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback;

    @Nullable
    private Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback;

    @Nullable
    private Runnable onCloseRoomCallback;

    @Nullable
    private static CustomerCommunication instance;

    public synchronized static CustomerCommunication getInstance() {
        if (instance == null) {
            instance = new CustomerCommunication();
        }

        return instance;
    }

    private CustomerCommunication() {
        this.connected = false;
        this.isDiscovering = false;
    }

    // eventualmente prendere callback con costruttore


    public void joinRoom(@NonNull Activity activity) {
        Objects.requireNonNull(onCloseRoomCallback);

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
                    isDiscovering = true;
                })
                .addOnFailureListener((Exception e) -> Log.e(TAG, "Discovery failed"));
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
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
            public void onEndpointLost(@NonNull String endpointId) {
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
                        break;
                    case FREE_TABLE_LIST:
                        handleFreeTableListResponse(message.getContent());
                        break;
                    case SELECT_TABLE:
                        handleSelectTableResponse(message.getContent());
                        break;
                    case NOTIFY_WAITER:
                        handleNotifyWaiterResponse(message.getContent());
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented yet");
                }
            }
        };

        return new ConnectionListener(activity, payloadCallback) {
            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                Nearby.getConnectionsClient(activity).stopDiscovery();
                isDiscovering = false;

                managerEndpointId = endpointId;
                sendName();
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                super.onDisconnected(endpointId);
                Objects.requireNonNull(onCloseRoomCallback);

                connected = false;
                onCloseRoomCallback.run();
            }
        };
    }


    private void handleNotifyWaiterResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        @SuppressWarnings("unchecked")
        Confirmation<WaiterNotificationException> confirmation = (Confirmation<WaiterNotificationException>) content;
        onNotifyWaiterConfirmationCallback.accept(confirmation);
    }

    private void handleSelectTableResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        @SuppressWarnings("unchecked")
        Confirmation<TableException> confirmation = (Confirmation<TableException>) content;
        onSelectTableConfirmationCallback.accept(confirmation);
    }

    private void sendName() {
        sendMessage(managerEndpointId, new Message(RequestType.CUSTOMER_NAME, CustomerStorage.getName()));
    }

    protected void handleCustomerNameConfirmation(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        @SuppressWarnings("unchecked")
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

        @SuppressWarnings("unchecked")
        Response<TreeSet<Table>, TableException> response = (Response<TreeSet<Table>, TableException>) content;
        freeTableListCallback.accept(response);
    }


    public void notifyWaiter(@NonNull Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback) {
        Objects.requireNonNull(onNotifyWaiterConfirmationCallback);
        ensureConnection();

        this.onNotifyWaiterConfirmationCallback = onNotifyWaiterConfirmationCallback;

        sendMessage(managerEndpointId, new Message(RequestType.NOTIFY_WAITER, null));
    }

    public void selectTable(@NonNull Table table, @NonNull Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(onSelectTableConfirmationCallback);
        ensureConnection();

        this.onSelectTableConfirmationCallback = onSelectTableConfirmationCallback;

        sendMessage(managerEndpointId, new Message(RequestType.SELECT_TABLE, table));
    }

    public void requestFreeTableList(@NonNull Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback) {
        Objects.requireNonNull(freeTableListCallback);
        ensureConnection();

        this.freeTableListCallback = freeTableListCallback;

        sendMessage(managerEndpointId, new Message(RequestType.FREE_TABLE_LIST, null)); //TODO content
    }

    private void ensureConnection() {
        if (!isConnected()) {
            throw new IllegalStateException("Not connected with local");
        }
    }


    /**
     * La callback `onCloseRoomCallback` verrà chiamata nel caso il gestore chiuda la stanza
     * e il cliente sia ancora connesso ad essa.
     *
     * @param onCloseRoomCallback callback che implementa la logica da attuare quando il
     *                            gestore chiude la stanza
     */
    public void onCloseRoom(@NonNull Runnable onCloseRoomCallback) {
        Objects.requireNonNull(onCloseRoomCallback);
        this.onCloseRoomCallback = onCloseRoomCallback;
    }


    /**
     * Disconnette il cliente dalla stanza del gestore.
     */
    public void leaveRoom() {
        if (connected) {
            Nearby.getConnectionsClient(activity).disconnectFromEndpoint(managerEndpointId);
            connected = false;
        }
        if (isDiscovering) {
            Nearby.getConnectionsClient(activity).stopDiscovery();
            isDiscovering = false;
        }
        activity = null;
    }


    /**
     * Verifica se il cliente è connesso alla stanza del gestore.
     *
     * @return true se il cliente è connesso alla stanza del gestore, false altrimenti
     */
    public boolean isConnected() {
        return connected;
    }
}
