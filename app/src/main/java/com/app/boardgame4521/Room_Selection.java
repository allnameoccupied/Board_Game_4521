package com.app.boardgame4521;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Room_Selection extends AppCompatActivity {
    //custom view model
    private RoomSelection_ViewModel roomSelection_viewModel;

    public static class RoomSelection_ViewModel extends ViewModel{
        //var
        private MutableLiveData<String> msgToShow = new MutableLiveData<>();
        //func
        public void setMsgToShow(String msg){this.msgToShow.setValue(msg);}
        public LiveData<String> getMsgToShow(){return msgToShow;}
    }

    //func

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Fragment 1    (display room for user to choose)
    public static class Room_Selection_Frag1 extends Fragment {

    }
}
