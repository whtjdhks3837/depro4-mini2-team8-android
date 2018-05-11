package com.depromeet.donkey.main.Model.callback;

import com.depromeet.donkey.main.data.Marker;

import java.util.ArrayList;
import java.util.List;

public interface ModelCallback {
    interface RetrofitCallback {
        void onGetMarkersSuccess(ArrayList<Marker> items);
        void onFailure(String msg);
    }
}
