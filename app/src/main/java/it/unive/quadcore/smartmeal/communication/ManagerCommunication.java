package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.PayloadCallback;

import java.io.Serializable;
import java.util.TreeSet;
import java.util.Objects;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.confirmation.ConfirmationDenied;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.ManagerTable;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;
import it.unive.quadcore.smartmeal.util.BiFunction;

import static it.unive.quadcore.smartmeal.communication.CustomerHandler.Customer;
import static it.unive.quadcore.smartmeal.communication.RequestType.CUSTOMER_NAME;
import static it.unive.quadcore.smartmeal.communication.RequestType.FREE_TABLE_LIST;
import static it.unive.quadcore.smartmeal.communication.RequestType.NOTIFY_WAITER;


public class ManagerCommunication extends Communication {
    @NonNull
    private static final String TAG = "ManagerCommunication";

    @NonNull
    private final CustomerHandler customerHandler;

    @Nullable
    private Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback;
    @Nullable
    private Function<WaiterNotification, Confirmation<? extends WaiterNotificationException>> onNotifyWaiterCallback;
    @Nullable
    private BiFunction<Customer, Table, Confirmation<? extends TableException>> onSelectTableCallback;
    @Nullable
    private Consumer<Customer> onCustomerLeftRoomCallback;
    private boolean roomStarted;

    @Nullable
    private static ManagerCommunication instance;

    public synchronized static ManagerCommunication getInstance() {
        if (instance == null) {
            instance = new ManagerCommunication();
        }

        return instance;
    }

    private ManagerCommunication() {
        roomStarted = false;
        customerHandler = CustomerHandler.getInstance();
    }


    // TODO
    private ConnectionLifecycleCallback connectionLifecycleCallback() {

        final PayloadCallback payloadCallback = new MessageListener() {

            @Override
            protected void onMessageReceived(@NonNull String endpointId, @NonNull Message message) {
                Objects.requireNonNull(endpointId);
                Objects.requireNonNull(message);

                RequestType requestType = message.getRequestType();

                if (requestType == RequestType.CUSTOMER_NAME) {
                    handleCustomerNameMessage(endpointId, message.getContent());
                    return;
                }

                else if (!customerHandler.containsCustomer(endpointId)) {
                    handleCustomerNotRecognized(endpointId);
                    return;
                }

                // TODO continuare
                switch (message.getRequestType()) {
                    case FREE_TABLE_LIST:
                        handleFreeTableListRequest(endpointId);
                        break;
                    case SELECT_TABLE:
                        handleSelectTableRequest(endpointId, message.getContent());
                        break;
                    case NOTIFY_WAITER:
                        handleNotifyWaiterRequest(endpointId);
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented yet");
                }


            }
        };


        return new ConnectionListener(activity, payloadCallback) {

            @Override
            protected void onConnectionSuccess(@NonNull String endpointId) {
                // TODO
            }

            @Override
            public void onDisconnected(@NonNull String endpointId) {
                super.onDisconnected(endpointId);
                Objects.requireNonNull(onCustomerLeftRoomCallback);

                onCustomerLeftRoomCallback.accept(customerHandler.getCustomer(endpointId));
            }
        };
    }

    private void handleNotifyWaiterRequest(@NonNull String endpointId) {
        Objects.requireNonNull(onNotifyWaiterCallback);
        WaiterNotification waiterNotification = new WaiterNotification(customerHandler.getCustomer(endpointId));
        Confirmation<? extends WaiterNotificationException> confirmation = onNotifyWaiterCallback.apply(waiterNotification);
        sendMessage(endpointId, new Message(NOTIFY_WAITER, confirmation));
    }

    private void handleSelectTableRequest(@NonNull String endpointId, @NonNull Serializable content) {
        Objects.requireNonNull(onSelectTableCallback);
        Table selectedTable = (Table) content;
        Confirmation<? extends TableException> confirmation = onSelectTableCallback.apply(customerHandler.getCustomer(endpointId), selectedTable);
        sendMessage(endpointId, new Message(NOTIFY_WAITER, confirmation));
    }

    private void handleCustomerNotRecognized(@NonNull String endpointId) {
        Message confirmationMessage = new Message(CUSTOMER_NAME, new ConfirmationDenied<>(new CustomerNotRecognizedException()));
        sendMessage(endpointId, confirmationMessage);
    }

    private void handleCustomerNameMessage(@NonNull String endpointId, @NonNull Serializable content) {
        // TODO check exception instead of if & else
        Objects.requireNonNull(content);
        String name = (String) content;
        if(customerHandler.containsCustomer(endpointId)){
            Log.i(TAG, "Customer " + endpointId + " was already recognized in Local");
        }
        else {
            customerHandler.addCustomer(endpointId, name);
            Message confirmationMessage = new Message(CUSTOMER_NAME, new Confirmation<CustomerNotRecognizedException>());
            sendMessage(endpointId, confirmationMessage);
        }

    }

    private void handleFreeTableListRequest(@NonNull String toEndpointId) {
        Objects.requireNonNull(onRequestFreeTableListCallback);
        Message response = new Message(FREE_TABLE_LIST, onRequestFreeTableListCallback.get());
        sendMessage(toEndpointId, response);
    }




    // eventualmente prendere callback con costruttore

    // TODO probabilmente andranno aggiunte callback onSuccess e onFail
    public void startRoom(@NonNull Activity activity) {
        if(isRoomStarted()) {
            throw new IllegalStateException("Room has been already started");
        }

        Objects.requireNonNull(onCustomerLeftRoomCallback);

        Objects.requireNonNull(onSelectTableCallback);
        Objects.requireNonNull(onNotifyWaiterCallback);
        Objects.requireNonNull(onRequestFreeTableListCallback);

        this.activity = activity;

        final ConnectionLifecycleCallback connectionLifecycleCallback = connectionLifecycleCallback();

        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().setStrategy(STRATEGY).build();
        Nearby.getConnectionsClient(activity)
                .startAdvertising(
                        ManagerStorage.getName(),       // TODO forse da passare come parametro in crea stanza
                        SERVICE_ID,
                        connectionLifecycleCallback,
                        advertisingOptions
                )
                .addOnSuccessListener((Void unused) -> Log.i(TAG, "Successfully started advertising"))
                .addOnFailureListener((Exception e) -> Log.e(TAG, "Advertising failed"));
    }

    public void onNotifyWaiter(@NonNull Function<WaiterNotification, Confirmation<? extends WaiterNotificationException>> onNotifyWaiterCallback) {
        Objects.requireNonNull(onNotifyWaiterCallback);

        this.onNotifyWaiterCallback = onNotifyWaiterCallback;
    }

    public void onSelectTable(@NonNull BiFunction<Customer, Table, Confirmation<? extends TableException>> onSelectTableCallback) {
        Objects.requireNonNull(onSelectTableCallback);

        this.onSelectTableCallback = onSelectTableCallback;
    }

    public void onRequestFreeTableList(@NonNull Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback) {
        Objects.requireNonNull(onRequestFreeTableListCallback);

        this.onRequestFreeTableListCallback = onRequestFreeTableListCallback;
    }


    /**
     * La callback `onCustomerLeftRoomCallback` verrà chiamata nel caso un cliente
     * si disconnetta dalla stanza, con parametro l'oggetto della classe Customer
     * che lo rappresenta.
     *
     * @param onCustomerLeftRoomCallback callback che implementa la logica da attuare quando un
     *                                   cliente si disconnette dalla stanza
     */
    public void onCustomerLeftRoom(@NonNull Consumer<Customer> onCustomerLeftRoomCallback) {
        Objects.requireNonNull(onCustomerLeftRoomCallback);

        this.onCustomerLeftRoomCallback = onCustomerLeftRoomCallback;
    }


    /**
     * Chiude la stanza e disconnette tutti i clienti ad essa collegati.
     */
    public void closeRoom() {
        if (!isRoomStarted()) {
            throw new IllegalStateException("The room has not been started");
        }

        Nearby.getConnectionsClient(activity).stopAllEndpoints();
        Nearby.getConnectionsClient(activity).stopAdvertising();
        roomStarted = false;
        activity = null;
    }

    public boolean isRoomStarted() {
        return roomStarted;
    }
}
