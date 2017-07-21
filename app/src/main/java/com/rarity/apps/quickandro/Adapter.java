package com.rarity.apps.quickandro;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder>{

    List<String> al_final;
    private MainActivity mainActivity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView st_mess;
        public LinearLayout l_layout;
        public RelativeLayout r_layout;

        public MyViewHolder(View view) {
            super(view);
            st_mess = (TextView) view.findViewById(R.id.mess);
            l_layout = (LinearLayout) view.findViewById(R.id.inner_layout);
            r_layout = (RelativeLayout) view.findViewById(R.id.rl);

            r_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(st_mess.getText().toString().charAt(0) == ' ') {
                        mainActivity.command.setText(st_mess.getText().toString().trim());
                        mainActivity.btn.callOnClick();
                    }
                }
            });
        }
    }

    public Adapter(List<String> alf, MainActivity mainActivity) {
        al_final = alf;
        this.mainActivity = mainActivity;
    }

    @Override
    public Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(Adapter.MyViewHolder holder, int position) //this method is for editing layout of each row at different positions
    {
        // this is to set different alignment and background of messages of different users
        try{
            if(al_final.get(position).charAt(0) == ' ') {
                holder.r_layout.setGravity(Gravity.RIGHT);
                holder.l_layout.setBackgroundResource(R.drawable.chat_right);
            }
            else {
                holder.r_layout.setGravity(Gravity.LEFT);
                holder.l_layout.setBackgroundResource(R.drawable.chat_left);
            }

            holder.st_mess.setText(al_final.get(position));
        }
        catch(Exception e){holder.st_mess.setText("");}
    }

    @Override
    public int getItemCount() {
        return al_final.size();
    }


}