package justyna.hekert.bunnycare;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import justyna.hekert.bunnycare.profiles.ProfileListContent;


public class ModifyProfileFragment extends Fragment {

    public ModifyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_modify, container, false);
    }


    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();

        Intent intent = activity.getIntent();

        if(intent !=null) {
            ProfileListContent.Profile recivedItem = intent.getParcelableExtra(MainActivity.ProfileExtra);
            if(recivedItem != null) {
                setData(recivedItem);
            }
        }
    }

    public void setData(ProfileListContent.Profile profile){
        FragmentActivity activity = getActivity();

        EditText nameEditTxt = activity.findViewById(R.id.modName);
        EditText dateEditTxt = activity.findViewById(R.id.modDate);
        Spinner sexEditTxt = activity.findViewById(R.id.modSex);
        EditText weightEditTxt = activity.findViewById(R.id.modWeight);
        EditText typeEditTxt = activity.findViewById(R.id.modType);
        EditText specialCharEditTxt = activity.findViewById(R.id.modSpecChar);

        nameEditTxt.setText(profile.name);
        dateEditTxt.setText(profile.dateOfBirth);

        int sex_id;

        if(profile.sex.equals("samiec")) sex_id = 1;
        else if(profile.sex.equals("samica")) sex_id = 2;
        else sex_id = 0;

        sexEditTxt.setSelection(sex_id);
        weightEditTxt.setText(profile.weight.toString());
        typeEditTxt.setText(profile.type);
        specialCharEditTxt.setText(profile.specChar);
    }

}
