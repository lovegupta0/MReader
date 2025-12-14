package com.mreader.LG.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MenuViewModel extends ViewModel {
    private MutableLiveData<Boolean> _isMenuOpen = new MutableLiveData<>(false);
    private static volatile MenuViewModel instance;

    private MenuViewModel() {

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
}
