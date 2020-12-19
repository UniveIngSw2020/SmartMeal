package it.unive.quadcore.smartmeal.communication;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.core.util.Consumer;
import androidx.core.util.Supplier;

import com.google.android.gms.common.util.BiConsumer;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
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
import java.util.TreeSet;
import java.util.Objects;
import java.util.function.BiFunction;

import it.unive.quadcore.smartmeal.communication.response.Response;
import it.unive.quadcore.smartmeal.local.TableException;
import it.unive.quadcore.smartmeal.local.WaiterNotificationException;
import it.unive.quadcore.smartmeal.model.Customer;
import it.unive.quadcore.smartmeal.model.Table;
import it.unive.quadcore.smartmeal.model.WaiterNotification;
import it.unive.quadcore.smartmeal.storage.ManagerStorage;

import static it.unive.quadcore.smartmeal.communication.RequestType.FREE_TABLE_LIST;

public abstract class ManagerCommunication extends Communication {
    private static final String TAG = "ManagerCommunication";

    @Nullable
    private Supplier<Response<TreeSet<? extends Table>, ? extends TableException>> onRequestFreeTableListCallback;


    // TODO
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
                            handleFreeTableListRequest(endpointId);
                            break;
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
