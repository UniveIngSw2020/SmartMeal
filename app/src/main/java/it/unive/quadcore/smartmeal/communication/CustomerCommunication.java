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
        DISCONNECTED,
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

    private boolean insideTheRoom;
    // default: false
    // set to true: joinRoom()
    // set to false: leaveRoom()
    // dipendenze: activity == null   <==>   insideTheRoom == false
    // dipendenze: insideTheRoom == false   ==>   isDiscovering == false && connectionState == CONNECTED

    @Nullable
    private Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback;

    @Nullable
    private Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback;

    @Nullable
    private Runnable onConnectionSuccessCallback;

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
        this.insideTheRoom = false;
    }

    // eventualmente prendere callback con costruttore


    public synchronized void joinRoom(@NonNull Activity activity, @NonNull Runnable onConnectionSuccessCallback,
                                      @NonNull Runnable onConnectionFailureCallback) {
        if (insideTheRoom) {
            Log.w(TAG, "joinRoom called, but already inside the room");
            return;
        }
        insideTheRoom = true;
        Objects.requireNonNull(onConnectionSuccessCallback);
        Objects.requireNonNull(onConnectionFailureCallback);
        Objects.requireNonNull(onCloseRoomCallback);

        this.activity = activity;
        this.onConnectionSuccessCallback = onConnectionSuccessCallback;

        final EndpointDiscoveryCallback endpointDiscoveryCallback = endpointDiscoveryCallback();

        nearbyTimer(() -> {
            synchronized (CustomerCommunication.this) {
                if (isNotConnected() && insideTheRoom) {
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
                assert activity != null; //TODO: penso abbia senso, altrimenti sarebbe stato bloccato prima ma probabilemente va sincronizzato
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

        //TODO: probabilmente bisogna controllare che si sia stillOnTheRoom per garantire che activity sia nonNull prima di procedere
        //(ha senso questo controllo per tutto il metodo
        return new ConnectionListener(Objects.requireNonNull(activity), payloadCallback) {

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


    private void handleNotifyWaiterResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onNotifyWaiterConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<WaiterNotificationException> confirmation = (Confirmation<WaiterNotificationException>) content;
        onNotifyWaiterConfirmationCallback.accept(confirmation);
    }

    private void handleSelectTableResponse(@NonNull Serializable content) {
        Objects.requireNonNull(content);
        assert onSelectTableConfirmationCallback != null;

        @SuppressWarnings("unchecked")
        Confirmation<? extends TableException> confirmation = (Confirmation<? extends TableException>) content;
        onSelectTableConfirmationCallback.accept(confirmation);
    }

    private void sendName() {
        sendMessage(managerEndpointId, new Message(RequestType.CUSTOMER_NAME, CustomerStorage.getName()));
    }

    protected synchronized void handleCustomerNameConfirmation(@NonNull Serializable content) {
        Objects.requireNonNull(content);

        if(insideTheRoom) {
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

    private void handleFreeTableListResponse(@NonNull Serializable content) {
        //TODO: probabilmente dovremo sincronizzare più cose perché il requireNonNull è soggetto alla concorrenza
        Objects.requireNonNull(content);
        assert freeTableListCallback != null;
        // if (content instanceof SortedSet) TODO pensarci

        @SuppressWarnings("unchecked")
        Response<TreeSet<Table>, TableException> response = (Response<TreeSet<Table>, TableException>) content;
        freeTableListCallback.accept(response);
    }


    public void notifyWaiter(@NonNull Consumer<Confirmation<? extends WaiterNotificationException>> onNotifyWaiterConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
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

    public void selectTable(@NonNull Table table, @NonNull Consumer<Confirmation<? extends TableException>> onSelectTableConfirmationCallback, @NonNull Runnable onTimeoutCallback) {
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

    public void requestFreeTableList(@NonNull Consumer<Response<TreeSet<Table>, ? extends TableException>> freeTableListCallback,
                                     @NonNull Runnable onTimeoutCallback) {
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

    private Timer nearbyTimer(Runnable onTimeoutCallback) {
        final long NEARBY_TIMEOUT = 20 * 1000;      // 20 secondi

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onTimeoutCallback.run();
            }
        }, NEARBY_TIMEOUT);

        return timer;
    }

    private void ensureConnection() {
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
    public void onCloseRoom(@NonNull Runnable onCloseRoomCallback) {
        Objects.requireNonNull(onCloseRoomCallback);
        this.onCloseRoomCallback = onCloseRoomCallback;
    }


    /**
     * Disconnette il cliente dalla stanza del gestore.
     */
    public synchronized void leaveRoom() {
        if(!insideTheRoom) {
            Log.w(TAG, "trying to leave the Room while not in the Room");
        }
        insideTheRoom = false;
        stopDiscovery();
        disconnect();
        activity = null;
    }

    private synchronized void disconnect() {
        if(connectionState != ConnectionState.DISCONNECTED) {
            assert managerEndpointId != null;
            assert activity != null;
            Nearby.getConnectionsClient(activity).disconnectFromEndpoint(managerEndpointId);
            connectionState = ConnectionState.DISCONNECTED;
        }
    }

    private synchronized void stopDiscovery() {
        if(isDiscovering) {
            assert activity != null;
            Nearby.getConnectionsClient(activity).stopDiscovery();
            isDiscovering = false;
        }
    }


    /**
     * Verifica se il cliente è connesso alla stanza del gestore.
     *
     * @return true se il cliente è connesso alla stanza del gestore, false altrimenti
     */
    public synchronized boolean isNotConnected() {
        return connectionState != ConnectionState.CONNECTED;
    }
}
