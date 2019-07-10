package avishkaar.com.joystickdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class DialogAlert extends DialogFragment {
   String SSID;
   Button connect,cancel;
   EditText editText;
   interface dialogToMain{
       void nameAndPasswordToMain(String SSID, String password);
   } dialogToMain ref;
    void recieveSSID(String SSID)
    {
        this.SSID = SSID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alert_box,container,false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            ref = (dialogToMain) getActivity();
        }catch (ClassCastException e){
            e.printStackTrace();
        };

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        connect = view.findViewById(R.id.connect);
        cancel = view.findViewById(R.id.cancel);
        editText = view.findViewById(R.id.editTextDialogUserInput);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.nameAndPasswordToMain(SSID,editText.getText().toString());
                dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });






    }
}
