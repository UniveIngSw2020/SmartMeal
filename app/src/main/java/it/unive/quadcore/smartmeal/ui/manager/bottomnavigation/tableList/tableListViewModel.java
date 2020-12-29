package it.unive.quadcore.smartmeal.ui.manager.bottomnavigation.tableList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class tableListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public tableListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}