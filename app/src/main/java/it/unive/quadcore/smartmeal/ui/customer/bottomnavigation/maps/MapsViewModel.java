package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.maps;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapsViewModel extends ViewModel {
    // TODO autogenerato

    private MutableLiveData<String> mText;

    public MapsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
