package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import Adapters.GroupAdapter;
import Entities.Group;
import Entities.Member;
import Interfaces.RolfsServerListener;
import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements RolfsServerListener {
    private Controller controller;
    private String username;
    private TextView tvWelcome;
    private RecyclerView rvGroups;
    private GroupAdapter adapter;
    private ArrayList<String> groups = new ArrayList<>();

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initializeComponents(view);
        return view;
    }

    private void initializeComponents(View view) {
        tvWelcome = view.findViewById(R.id.tvWelcome);
        username = controller.getUsername();
        tvWelcome.setText("Välkommen " + username + " till Rolfs stalkerapplikation");
        rvGroups = (RecyclerView) view.findViewById(R.id.rvGroups);
        rvGroups.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new GroupAdapter(getActivity(), groups);
        adapter.setController(controller);
        rvGroups.setAdapter(adapter);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @Override
    public ArrayList<Member> memberRequest() {
        return null;
    }

    @Override
    public void groupRequest(ArrayList<String> groups) {
        this.groups = groups;
        adapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Member> onPositionsMessage() {
        return null;
    }
}
