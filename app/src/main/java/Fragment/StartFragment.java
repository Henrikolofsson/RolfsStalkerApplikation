package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {
    private Controller controller;
    private EditText etLogIn;
    private Button btnLogIn;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        initializeComponents(view);
        registerListeners();

        return view;
    }

    private void initializeComponents(View view) {
        etLogIn = view.findViewById(R.id.etLogIn);
        btnLogIn = view.findViewById(R.id.btnlogIn);
    }

    private void registerListeners(){
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.logIn(etLogIn.getText().toString());
            }
        });
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

}
