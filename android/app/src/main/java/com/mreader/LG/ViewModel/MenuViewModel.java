package com.mreader.LG.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mreader.LG.Common.SettingStorage;
import com.mreader.LG.Service.SettingService;

public class MenuViewModel extends ViewModel {
    private MutableLiveData<Boolean> _isMenuOpen = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> readMode;
    private static volatile MenuViewModel instance;
    private SettingStorage settingStorage;



    private MenuViewModel() {
        settingStorage = SettingStorage.getInstance();
        readMode = new MutableLiveData<>(settingStorage.getReadMode());

    }

    public static MenuViewModel getInstance() {
        if (instance == null) {
            synchronized (MenuViewModel.class) {
                if (instance == null) {
                    instance = new MenuViewModel();
                }
            }
        }
        return instance;
    }

    public boolean getIsMenuOpen() {
        return _isMenuOpen.getValue();
    }

    public void setIsMenuOpen(boolean val) {
        _isMenuOpen.setValue(val);
    }

    public MutableLiveData<Boolean> isMenuOpen() {
        return _isMenuOpen;
    }

    public void toggleMenu() {
        _isMenuOpen.setValue(!_isMenuOpen.getValue());
    }

    public void toggleReadMode(){
        readMode.setValue(!readMode.getValue());
        settingStorage.setReadMode(readMode.getValue());
    }
    public MutableLiveData<Boolean> getReadMode(){
        return readMode;
    }
}
