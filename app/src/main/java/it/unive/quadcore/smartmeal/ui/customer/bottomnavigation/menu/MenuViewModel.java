package it.unive.quadcore.smartmeal.ui.customer.bottomnavigation.menu;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuViewModel extends ViewModel {
    // TODO autogenerato

    private MutableLiveData<String> mText;

    public MenuViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
