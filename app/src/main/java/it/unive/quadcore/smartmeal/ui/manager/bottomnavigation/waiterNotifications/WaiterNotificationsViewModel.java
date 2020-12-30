package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.waiterNotifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WaiterNotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public WaiterNotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}