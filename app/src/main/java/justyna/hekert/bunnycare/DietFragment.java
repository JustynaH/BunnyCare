package justyna.hekert.bunnycare;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;


public class DietFragment extends Fragment {

    public DietFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_diet, container, false);
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        String diet_table_name = activity.get_diet_table_name();

        ListView listView = activity.findViewById(R.id.listView);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(activity);
        databaseAccess.open();
        ArrayList<HashMap<String, String>> info = databaseAccess.getInfoFrom(diet_table_name);
        databaseAccess.close();

        ListAdapter adapter = new SimpleAdapter(getContext(), info, R.layout.adapter_list_view, new String[]{"name", "parts", "comments"}, new int[]{R.id.diet_name, R.id.diet_parts, R.id.diet_comment});
        listView.setAdapter(adapter);
    }

}
