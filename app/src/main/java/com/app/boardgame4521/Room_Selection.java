package com.app.boardgame4521;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.boardgame4521.MaxUtil.util;
import com.app.boardgame4521.MaxUtil.utilGoogle;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
        setContentView(R.layout.room_selection_main);

        //get view model
        roomSelection_viewModel = new ViewModelProvider(this).get(RoomSelection_ViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.room_selection_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.room_selection_menu_item1:
                util.log("add icon pressed");
                break;
            case R.id.room_selection_menu_item2:
                if (Room_Selection_Frag1.thisFrag!=null){
                    Room_Selection_Frag1.thisFrag.updateRoomList();
                }
                util.log("refresh icon pressed");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //Fragment 1    (display room for user to choose)
    public static class Room_Selection_Frag1 extends Fragment {
        //var
        public static Room_Selection_Frag1 thisFrag = null;
        private RoomSelection_ViewModel viewModel;
        private NavController navController;
        private View view;
        public ArrayList<RS_Room> roomList = new ArrayList<>();
        public RS_RecyclerView_Adapter adapter;

        //func
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            viewModel = new ViewModelProvider(this).get(RoomSelection_ViewModel.class);
            thisFrag = this;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            view = inflater.inflate(R.layout.room_selection_frag1,container,false);
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            navController = NavHostFragment.findNavController(this);

            //set recycler view
            this.view = view;
            updateRoomList();
        }

        public void updateRoomList(){
            roomList = new ArrayList<>();
            RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.roomSelection_frag1_recycler);
            utilGoogle.getFirestore().collection("active_room").get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                roomList.add(new RS_Room(documentSnapshot));
//                                util.log("qwer");
                            }
//                            util.log(roomList.size());
                            adapter.notifyDataSetChanged();
                        }
                    });
            adapter = new RS_RecyclerView_Adapter(getActivity(),roomList);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
            adapter.setOnItemClickListener((view1, room) -> {
                util.makeToastLog(room.name);
            });
        }

        //custom room class
        public static class RS_Room{
            //var
            public String name;
            public String host;
            public int playerCount;

            //func
            public RS_Room(DocumentSnapshot roomData){
                name = roomData.getId();
                host = roomData.getString("host");
                playerCount = ((Long)roomData.get("player_count")).intValue();
            }
        }

        //custom adapter for recycler view
        public static class RS_RecyclerView_Adapter
                extends RecyclerView.Adapter<RS_RecyclerView_Adapter.RS_RecyclerView_ViewHolder>{
            private Context context;
            private ArrayList<RS_Room> roomList;

            public RS_RecyclerView_Adapter(Context context, ArrayList<RS_Room> roomList){
                this.context = context;
                this.roomList = roomList;
//                util.log(roomList.size());
            }

            @Override
            public RS_RecyclerView_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(context).inflate(R.layout.rs_recyclerview_item,parent,false);
                View view = View.inflate(context,R.layout.rs_recyclerview_item,null);
                RS_RecyclerView_ViewHolder holder = new RS_RecyclerView_ViewHolder(view);
                //set item in holder to corr. view
                holder.roomNameView = (TextView)view.findViewById(R.id.roomSelection_frag1_recycler_item_textview1);
                holder.roomHostView = (TextView)view.findViewById(R.id.roomSelection_frag1_recycler_item_textview2);
                holder.roomPlayerCountView = (TextView)view.findViewById(R.id.roomSelection_frag1_recycler_item_textview3);
                return holder;
            }

            @Override
            public void onBindViewHolder(@NonNull RS_RecyclerView_ViewHolder holder, int position) {
                RS_Room room = roomList.get(position);
                //set holder item to real data
                holder.roomNameView.setText(room.name);
                holder.roomHostView.setText(room.host);
                holder.roomPlayerCountView.setText(room.playerCount +"/4");
//                util.log("asdf");
            }

            @Override
            public int getItemCount() {
                return roomList.size();
            }

            //custom view holder for each item
            public class RS_RecyclerView_ViewHolder extends RecyclerView.ViewHolder{
                //var
                public TextView roomNameView;
                public TextView roomHostView;
                public TextView roomPlayerCountView;

                //func
                public RS_RecyclerView_ViewHolder(@NonNull View itemView) {
                    super(itemView);
                    itemView.setOnClickListener(v -> {
                        if (onItemClickListener!=null){
                            onItemClickListener.OnItemClick(itemView,roomList.get(getLayoutPosition()));
                        }
                    });
                }
            }

            //custom on click listener
            private OnItemClickListener onItemClickListener;
            public interface OnItemClickListener{
                public void OnItemClick(View view, RS_Room room);
            }
            public void setOnItemClickListener(OnItemClickListener onItemClickListener){
                this.onItemClickListener = onItemClickListener;
            }
        }
    }
}
