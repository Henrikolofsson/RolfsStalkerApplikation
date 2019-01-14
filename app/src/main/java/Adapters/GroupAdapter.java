package Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.Holder>{
    private LayoutInflater inflater;
    private List<String> content;
    private Controller controller;

    public GroupAdapter(Context context){
        this(context, new ArrayList<String>());
    }

    public GroupAdapter(Context context, List<String> content){
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.content = content;
    }

    public void setContent(List<String> content){
        this.content = content;
        super.notifyDataSetChanged();
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.fragment_individual_group, viewGroup, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.tvGroupName.setText(content.get(position));
    }

    @Override
    public int getItemCount() {
        return content.size();
    }


    public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvGroupName;

        public Holder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = (TextView) itemView.findViewById(R.id.tvGroupName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(!controller.getRegistered()) {
                controller.registerGroup(tvGroupName.getText().toString());
                Toast.makeText(v.getContext(), controller.getUsername() + " registered group: " + tvGroupName.getText().toString(), Toast.LENGTH_SHORT).show();
                controller.setMainFragment();
            } else {
                Toast.makeText(v.getContext(), "You are already in a group.", Toast.LENGTH_SHORT).show();            }
        }
    }

}
