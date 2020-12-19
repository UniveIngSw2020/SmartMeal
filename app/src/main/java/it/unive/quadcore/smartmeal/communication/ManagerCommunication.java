package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

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
import java.util.function.BiFunction;

import it.unive.quadcore.smartmeal.communication.confirmation.Confirmation;
import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

import static it.unive.quadcore.smartmeal.communication.CustomerHandler.Customer;
import static it.unive.quadcore.smartmeal.communication.RequestType.CUSTOMER_NAME;
import static it.unive.quadcore.smartmeal.communication.RequestType.FREE_TABLE_LIST;


public abstract class ManagerCommunication extends Communication {
    private static final String TAG = "ManagerCommunication";

    private final CustomerHandler customerHandler;

    @Nullable
    private Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback;

    private ManagerCommunication() {
        customerHandler = CustomerHandler.getInstance();
    }


    // TODO
    private ConnectionLifecycleCallback connectionLifecycleCallback() {

        final PayloadCallback payloadCallback = new MessageListener() {

            @Override
            protected void onMessageReceived(String endpointId, Message message) {
                RequestType requestType = message.getRequestType();


                if (requestType == RequestType.CUSTOMER_NAME) {
                    handleCustomerNameMessage(message);
                    return;
                }

                if (!customerHandler.containsCustomer(endpointId)) {
                    new Message(CUSTOMER_NAME, false);
                }

                // TODO continuare
                switch (message.getRequestType()) {
                    case FREE_TABLE_LIST:
                        handleFreeTableListRequest(endpointId);
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented yet");
                }


            }
        };


        return new ConnectionListener(activity, payloadCallback) {

            @Override
            protected void onConnectionSuccess(String endpointId) {
                // TODO
            }
        };
    }

    private void handleCustomerNameMessage(Message message) {
        // TODO
        //customerHandler.addCustomer(endpointId, name);
        // TODO avvisare del buon esito

    }

    private void handleFreeTableListRequest(String toEndpointId) {
        Objects.requireNonNull(onRequestFreeTableListCallback);
        Message response = new Message(FREE_TABLE_LIST, onRequestFreeTableListCallback.get());
        sendMessage(toEndpointId, response);
    }


    public static ManagerCommunication getInstance() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // eventualmente prendere callback con costruttore

    // TODO probabilmente andranno aggiunte callback onSuccess e onFail
    public void startRoom(Activity activity) {
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
                .addOnSuccessListener((Void unused) -> {
                    Log.i(TAG, "Successfully started advertising");
                })
                .addOnFailureListener((Exception e) -> {
                    Log.e(TAG, "Advertising failed");
                });
    }

    public abstract void onNotifyWaiter(Function<WaiterNotification, Response<Serializable, ? extends WaiterNotificationException>> consumer);
    public abstract void onSelectTable(BiFunction<Customer, Table, Response<Serializable, ? extends TableException>> consumer);
    public void onRequestFreeTableList(Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> supplier) {
        onRequestFreeTableListCallback = supplier;
    }


    /**
     * La callback `onCustomerLeftRoomCallback` verrà chiamata nel caso un cliente
     * si disconnetta dalla stanza, con parametro l'oggetto della classe Customer
     * che lo rappresenta.
     *
     * @param onCustomerLeftRoomCallback allback che implementa la logica da attuare quando un
     *                                   cliente si disconnette dalla stanza
     */
    public abstract void onCustomerLeftRoom(Consumer<Customer> onCustomerLeftRoomCallback);


    /**
     * Chiude la stanza e disconnette tutti i clienti ad essa collegati.
     */
    public abstract void closeRoom();
}
