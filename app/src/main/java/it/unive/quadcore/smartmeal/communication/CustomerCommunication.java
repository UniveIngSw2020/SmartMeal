package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;

import java.io.Serializable;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.storage.CustomerStorage;

public class CustomerCommunication extends Communication {

    private enum ConnectionState{
        DISCONNECTED,   // <==>   managerEndPointID == null
        CONNECTING,
        CONNECTED
    }

    @NonNull
    private static final String TAG = "CustomerCommunication";

    @Nullable
    private String managerEndpointId;

    @Nullable
    private Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback;

    @NonNull
    private ConnectionState connectionState;
    // default: DISCONNECTED
    // set to CONNECTED: handleCustomerNameConfirmation()
    // set to DISCONNECTED: disconnect()
    // set to CONNECTING: onConnectionInitiated() in ConnectionListener settato connectionLifecycleCallback()
    // method wrapper isConnected()

    private boolean isDiscovering;
    // default: false
    // set to true: onSuccessListener di Nearby settato in joinRoom()
    // set to false: stopDiscovery()

    // private boolean insideTheRoom;
    // default: false
    // set to true: joinRoom()
    // set to false: leaveRoom()
    // dipendenze: activity == null   <==>   insideTheRoom == false
    // dipendenze: insideTheRoom == false   ==>   isDiscovering == false && connectionState == CONNECTED

    @Nullable
    private Runnable onConnectionSuccessCallback;

    @Nullable
    private Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback;

    @Nullable
    private Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback;

    @Nullable
    private Consumer<Table> onTableChangedCallback;

    @Nullable
    private Runnable onTableRemovedCallback;

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
        this.connectionState = ConnectionState.DISCONNECTED;
        this.isDiscovering = false;
    }

    @Override
    protected synchronized void sendMessage(String toEndpointId, Message response) {
        if (!isInsideTheRoom()) {
            Log.w(TAG, "trying to send a message while not in the room");
            return;
        }
        super.sendMessage(toEndpointId, response);
    }

    // eventualmente prendere callback con costruttore


    public synchronized void joinRoom(@NonNull Activity activity, @NonNull Runnable onConnectionSuccessCallback,
                                      @NonNull Runnable onConnectionFailureCallback) {
        if (isInsideTheRoom()) {
            Log.w(TAG, "joinRoom called, but already inside the room");
            return;
        }
        Objects.requireNonNull(onConnectionSuccessCallback);
        Objects.requireNonNull(onConnectionFailureCallback);
        Objects.requireNonNull(onCloseRoomCallback);

        this.activity = activity;
        this.onConnectionSuccessCallback = onConnectionSuccessCallback;

        final EndpointDiscoveryCallback endpointDiscoveryCallback = endpointDiscoveryCallback();

        nearbyTimer(() -> {
            synchronized (CustomerCommunication.this) {
                if (isNotConnected() && isInsideTheRoom()) {
                    leaveRoom();
                    onConnectionFailureCallback.run();
                    Log.i(TAG, "joinRoom failed for timeout");
                }
            }
        });

        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(activity)
                .startDiscovery(
                        SERVICE_ID,
                        endpointDiscoveryCallback,
                        discoveryOptions
                )
                .addOnSuccessListener((Void unused) -> {
                    Log.i(TAG, "Successfully started discovery");
                    synchronized (CustomerCommunication.this) {
                        isDiscovering = true;
                    }
                })
                .addOnFailureListener((Exception e) -> Log.e(TAG, "Discovery failed"));
    }

    private EndpointDiscoveryCallback endpointDiscoveryCallback() {
        return new EndpointDiscoveryCallback() {
            @Override
            public void onEndpointFound(@NonNull String endpointId, @NonNull DiscoveredEndpointInfo info) {
                // An endpoint was found. We request a connection to it.
                synchronized (CustomerCommunication.this) {
                    if (isInsideTheRoom()) {
                        assert activity != null; // già controllato dall'if
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
                }
            }

            @Override
            public void onEndpointLost(@NonNull String endpointId) {
                // A previously discovered endpoint has gone away.
                synchronized (CustomerCommunication.this) {
                    if(isInsideTheRoom()) {
                        Objects.requireNonNull(onCloseRoomCallback);
                        leaveRoom();
                        onCloseRoomCallback.run();
                    }
                }
            }
        };
    }

    private synchronized ConnectionLifecycleCallback connectionLifecycleCallback() {

        if(!isInsideTheRoom()) {
            throw new IllegalStateException("connectionLifeCycleCallback should be called only when inside the room");
        }

        final PayloadCallback payloadCallback = new MessageListener() {
            @Override
            protected void onMessageReceived(@NonNull String endpointId, @NonNull Message message) {
                synchronized (CustomerCommunication.this) {

                    if (isInsideTheRoom()) {
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
                            case TABLE_CHANGED:
                                handleChangedTableMessage(message.getContent());
                                break;
                            case TABLE_REMOVED:
                                handleRemovedTableMessage();
                            default:
                                throw new UnsupportedOperationException("Not implemented yet");
                        }
                    }
                }
            }
        };

        return new ConnectionListener(activity, payloadCallback) {

            @Override
            public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
                synchronized (CustomerCommunication.this) {
                    super.onConnectionInitiated(endpointId, connectionInfo);
                    managerEndpointId = endpointId;
                    connectionState = ConnectionState.CONNECTING;
                }
            }

            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                synchronized (CustomerCommunication.this) {
                    stopDiscovery();
                    sendName();
                }
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                synchronized (CustomerCommunication.this) {
                    super.onDisconnected(endpointId);
                    Objects.requireNonNull(onCloseRoomCallback);

                    leaveRoom();
                    onCloseRoomCallback.run();
                }
            }
        };
    }

    private void handleRemovedTableMessage() {
        assert onTableRemovedCallback != null;
        onTableRemovedCallback.run();
    }

    private synchronized void handleChangedTableMessage(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onTableChangedCallback != null;

        Table table = (Table) content;
        onTableChangedCallback.accept(table);
    }


    private synchronized void handleNotifyWaiterResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onNotifyWaiterConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<WaiterNotificationException> confirmation = (Confirmation<WaiterNotificationException>) content;
        onNotifyWaiterConfirmationCallback.accept(confirmation);
    }

    private synchronized void handleSelectTableResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onSelectTableConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<? extends TableException> confirmation = (Confirmation<? extends TableException>) content;
        onSelectTableConfirmationCallback.accept(confirmation);
    }

    private synchronized void sendName() {
        if(!isInsideTheRoom()){
            Log.i(TAG, "trying to send name while not in the room");
            return;
        }
        sendMessage(managerEndpointId, new Message(RequestType.CUSTOMER_NAME, CustomerStorage.getName()));
    }

    protected synchronized void handleCustomerNameConfirmation(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        if(isInsideTheRoom()) {
            assert onConnectionSuccessCallback != null;

            @SuppressWarnings("unchecked")
            Confirmation<CustomerNotRecognizedException> confirmation = (Confirmation<CustomerNotRecognizedException>) content;
            try {
                confirmation.obtain();
                connectionState = ConnectionState.CONNECTED;
                onConnectionSuccessCallback.run();

                // TODO eventuale stopDiscovery()

                Log.i(TAG, "Connection confirmed");
            } catch (CustomerNotRecognizedException e) {
                Log.e(TAG, "Connection not confirmed");
                sendName();

                // TODO eventualmente limitare i tentativi di connessione
            }
        }
    }

    private synchronized void handleFreeTableListResponse(@NonNull Serializable content) {
        //TODO: probabilmente dovremo sincronizzare più cose perché il requireNonNull è soggetto alla concorrenza
        Objects.requireNonNull(content);
        assert freeTableListCallback != null;
        // if (content instanceof SortedSet) TODO pensarci

        @SuppressWarnings("unchecked")
        Response<TreeSet<Table>, TableException> response = (Response<TreeSet<Table>, TableException>) content;
        freeTableListCallback.accept(response);
    }


    public synchronized void notifyWaiter(@NonNull Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
        if (!isInsideTheRoom()) {
            Log.w(TAG, "trying to notify the waiter while not in the room");
            return;
        }

        Objects.requireNonNull(onNotifyWaiterConfirmationCallback);
        Objects.requireNonNull(onTimeoutCallback);
        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.onNotifyWaiterConfirmationCallback = response -> {
            timer.cancel();
            onNotifyWaiterConfirmationCallback.accept(response);
        };

        sendMessage(managerEndpointId, new Message(RequestType.NOTIFY_WAITER, null));
    }

    public synchronized void selectTable(@NonNull Table table, @NonNull Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
        if (!isInsideTheRoom()) {
            Log.w(TAG, "trying to select table while not in the room");
            return;
        }

        Objects.requireNonNull(table);
        Objects.requireNonNull(onSelectTableConfirmationCallback);
        Objects.requireNonNull(onTimeoutCallback);
        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.onSelectTableConfirmationCallback = response -> {
            timer.cancel();
            onSelectTableConfirmationCallback.accept(response);
        };

        sendMessage(managerEndpointId, new Message(RequestType.SELECT_TABLE, table));
    }

    public synchronized void requestFreeTableList(@NonNull Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback,
                                     @NonNull Runnable onTimeoutCallback) {
        if(!isInsideTheRoom()){
            Log.i(TAG, "trying to request free table list while not in the room");
            return;
        }

        Objects.requireNonNull(freeTableListCallback);
        Objects.requireNonNull(onTimeoutCallback);
        ensureConnection();

        Timer timer = nearbyTimer(onTimeoutCallback);
        this.freeTableListCallback = response -> {
            timer.cancel();
            freeTableListCallback.accept(response);
        };

        sendMessage(managerEndpointId, new Message(RequestType.FREE_TABLE_LIST, null)); //TODO content
    }

    public synchronized void onTableChanged(@NonNull Consumer<Table> onTableChangedCallback) {
        Objects.requireNonNull(onTableChangedCallback);
        this.onTableChangedCallback = onTableChangedCallback;
    }

    public synchronized void onTableRemoved(@NonNull Runnable onTableRemovedCallback) {
        Objects.requireNonNull(onTableRemovedCallback);
        this.onTableRemovedCallback = onTableRemovedCallback;
    }

    private Timer nearbyTimer(Runnable onTimeoutCallback) {
        final long NEARBY_TIMEOUT = 60 * 1000;      // 60 secondi

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeoutCallback.run();
            }
        }, NEARBY_TIMEOUT);

        return timer;
    }

    private synchronized void ensureConnection() {
        if (isNotConnected()) {
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
    public synchronized void onCloseRoom(@NonNull Runnable onCloseRoomCallback) {
        Objects.requireNonNull(onCloseRoomCallback);
        this.onCloseRoomCallback = onCloseRoomCallback;
    }


    /**
     * Disconnette il cliente dalla stanza del gestore.
     */
    public synchronized void leaveRoom() {
        if(!isInsideTheRoom()) {
            Log.w(TAG, "trying to leave the Room while not in the Room");
        }
        stopDiscovery();
        disconnect();
        activity = null;
    }

    private synchronized void disconnect() {
        if(connectionState() != ConnectionState.DISCONNECTED) {
            assert managerEndpointId != null;
            assert activity != null;
            Nearby.getConnectionsClient(activity).disconnectFromEndpoint(managerEndpointId);
            connectionState = ConnectionState.DISCONNECTED;
            managerEndpointId = null;
        }
    }

    private synchronized void stopDiscovery() {
        if(isDiscovering) {
            assert activity != null;
            Nearby.getConnectionsClient(activity).stopDiscovery();
            isDiscovering = false;
        }
    }

    private synchronized boolean isInsideTheRoom() {
        return activity != null;
    }

    private synchronized ConnectionState connectionState() {
        return connectionState;
    }

    /**
     * Verifica se il cliente è connesso alla stanza del gestore.
     *
     * @return true se il cliente è connesso alla stanza del gestore, false altrimenti
     */
    public synchronized boolean isNotConnected() {
        return connectionState() != ConnectionState.CONNECTED;
    }
}
