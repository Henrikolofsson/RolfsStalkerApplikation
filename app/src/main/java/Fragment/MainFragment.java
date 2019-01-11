package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private Controller controller;
    private String username;
    private TextView tvWelcome;

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
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

}
