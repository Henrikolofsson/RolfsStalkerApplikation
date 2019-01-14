package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnregisterFragment extends Fragment {
    private Controller controller;
    private Button btnUnregister;
    private TextView tvUnregister;


    public UnregisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unregister, container, false);
        initializeComponents(view);
        registerListeners();
        return view;
    }

    private void initializeComponents(View view){
        btnUnregister = (Button) view.findViewById(R.id.btnUnregisterGroup);
        tvUnregister = (TextView) view.findViewById(R.id.tvUnregisterGroup);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    private void registerListeners(){
        btnUnregister.setOnClickListener(new UnregisterFromGroupListener());
    }

    private class UnregisterFromGroupListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            controller.unregister();
            controller.setMainFragment();
        }
    }

    @Override
    public void onResume() {
        if(controller.getUserGroup() != null){
            tvUnregister.setText("Avregistrera från grupp: " + controller.getUserGroup());
        } else {
            tvUnregister.setText("Du är inte medlem i någon grupp.");
        }
        super.onResume();
    }
}
