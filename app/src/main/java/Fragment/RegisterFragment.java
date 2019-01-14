package Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private Controller controller;
    private EditText etGroupName;
    private Button btnRegisterGroup;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initializeComponents(view);
        registerListeners();
        return view;
    }

    private void initializeComponents(View view){
        etGroupName = (EditText) view.findViewById(R.id.etGroupName);
        btnRegisterGroup = (Button) view.findViewById(R.id.btnRegisterGroup);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    private void registerListeners(){
        btnRegisterGroup.setOnClickListener(new OnRegisterGroupListener());
    }

    private class OnRegisterGroupListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(!controller.getRegistered()) {
                controller.registerGroup(etGroupName.getText().toString());
                Toast.makeText(getActivity(), controller.getUsername() + " registered group: " + etGroupName.getText().toString(), Toast.LENGTH_SHORT).show();
                controller.setMainFragment();
            } else {
                Toast.makeText(getActivity(), "You are already in a group.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
