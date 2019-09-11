package justyna.hekert.bunnycare;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import justyna.hekert.bunnycare.profiles.ProfileListContent;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ProfileFragment.OnListFragmentInteractionListener,
        ModifyDialog.OnModifyDialogInteractionListener {

    public static final String ProfileExtra = "ProfileExtra";
    public static final int REQUEST_MOD = 2;
    public static final int REQUEST_ADD = 1;

    public static final String PositionExtra = "PositionExtra";
    public static final String PicPathExtra = "PicPathExtra";

    private String diet_table_name;

    private int currentItemPosition = -1;

    public ProfileListContent.Profile currentProfile;
    private final String CURRENT_PROFILE_KEY = "CurrentProfile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        restoreFromMBM();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState != null) {
            currentProfile = savedInstanceState.getParcelable(CURRENT_PROFILE_KEY);
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_profile);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, R.string.add_action, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Intent AddIntent = new Intent(getApplicationContext(), AddProfileActivity.class);
                    startActivityForResult(AddIntent, REQUEST_ADD);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_convert) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ConverterFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_advices) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AdviceFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_vegetables) {
            diet_table_name = "vegetables";
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DietFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_fruits) {
            diet_table_name = "fruits";
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DietFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_herbs) {
            diet_table_name = "herbs";
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DietFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } else if (id == R.id.nav_help) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
        } /*else if (id == R.id.nav_share) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public String get_diet_table_name (){
        return diet_table_name;
    }

    @Override
    public void onListFragmentClickInteraction(ProfileListContent.Profile profile, int position) {
        currentProfile = profile;
        Toast.makeText(this, getString(R.string.profile_selected_msg), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent (this, InfoProfileActivity.class);
        intent.putExtra(ProfileExtra, profile);
        startActivity(intent);
    }

    private void showModifyDialog() {
        ModifyDialog.newInstance().show(getSupportFragmentManager(), getString(R.string.delete_dialog_tag));
    }

    //Delete, Edit
    @Override
    public void onListFragmentLongClickInteraction(ProfileListContent.Profile profile, int position) {
        showModifyDialog();
        currentItemPosition = position;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK){
                //update Fragment
            ((ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).notifyDataChange();
        }
        else if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(),getText(R.string.cancel_msg), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogDeleteClick(DialogFragment dialog) {
        View v = findViewById(R.id.fragment_container);
        if(v != null) {
            Snackbar.make(v, getString(R.string.delete_question), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.Yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(currentItemPosition != -1 && currentItemPosition < ProfileListContent.ITEMS.size()){
                                long itemID = ProfileListContent.removeItem(currentItemPosition);
                                MyBaseManager MBM = new MyBaseManager(getApplicationContext());
                                MBM.deleteProfile(itemID);
                                ((ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container)).notifyDataChange();
                            }
                        }
                    }).show();
        }
    }

    @Override
    public void onDialogEditClick(DialogFragment dialog) {
        if(currentItemPosition != -1 && currentItemPosition < ProfileListContent.ITEMS.size()){
            ProfileListContent.Profile ModProf = ProfileListContent.getItem(currentItemPosition);
            Intent intent = new Intent (this, ModifyProfileActivity.class);
            intent.putExtra(PositionExtra, currentItemPosition);
            intent.putExtra(ProfileExtra, ModProf);
            intent.putExtra(PicPathExtra, ModProf.picPath);
            startActivityForResult(intent, REQUEST_MOD);
            }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if(currentProfile != null)
            outState.putParcelable(CURRENT_PROFILE_KEY,currentProfile);
        super.onSaveInstanceState(outState);
    }

    public void convertClick(View view) {
        EditText ageYears = findViewById(R.id.ageYears);
        EditText ageMonths = findViewById(R.id.ageMonths);
        EditText ageWeeks = findViewById(R.id.ageWeeks);
        TextView resultTxt = findViewById(R.id.result);

        resultTxt.setText("");

        int ageY, ageM, ageW;

        if(ageYears.getText().toString().isEmpty()) ageY = 0;
        else ageY = Integer.parseInt(ageYears.getText().toString());

        if(ageMonths.getText().toString().isEmpty()) ageM = 0;
        else  ageM = Integer.parseInt(ageMonths.getText().toString());

        if(ageWeeks.getText().toString().isEmpty()) ageW = 0;
        else  ageW = Integer.parseInt(ageWeeks.getText().toString());

        boolean show_msg = false;
        String msg = "";

        //check values
        if(ageW >= 4) {
            msg = getString(R.string.WeeksMgs);
            show_msg = true;
        } else if(ageM >= 12) {
            msg = getString(R.string.MonthsMgs);
            show_msg = true;
        } else if(ageY > 16 || (ageY == 16 && (ageM > 0 || ageW > 0))) {
            msg = getString(R.string.toolong);
            show_msg = true;
        }

        if(show_msg) {
            Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        int resultY = 0, resultM = 0, resultW = 0;

        if(ageY > 0){
            if(ageY == 16){
                resultY = 110;
            }else {
                Double temp = Math.floor((6.0 * ageM) / 12);
                resultY = 21 + (ageY - 1) * 6 + temp.intValue();
                temp = (Math.floor(6.0 * ageW) / 4);
                resultM = (6 * ageM) % 12 + temp.intValue();
                resultW = (6 * ageW) % 4;
            }

        } else if (ageM > 0){
            if(ageM < 6) {
                Double temp = Math.floor((6.0 * ageW) / 12);
                resultY = 6 + (ageM-1)*2 + temp.intValue();
                resultM = (6 * ageW) % 12;
            } else if (ageM < 9) {  //between 6 to 9 months 16 - 18
                Double temp = Math.floor((8.0 * (ageM-6) + 2 * ageW) / 12);
                resultY = 16 + temp.intValue();
                resultM = (8 * (ageM-6) + 2 * ageW) % 12;
            } else {  //between 9 to 12 months   18 - 21
                resultY = 18 + (ageM-9);
                resultM = 3 * ageW;
            }
        } else if (ageW > 0) {
            switch(ageW)
            {
                case 1: resultY = 1;
                    break;
                case 2: resultY = 2;
                    break;
                case 3: resultY = 4;
                    break;
            }
        }

        resultTxt.setText(Integer.toString(resultY)+" "+getString(R.string.yearR)+" "+Integer.toString(resultM)+" "+getString(R.string.monthR)+" "+Integer.toString(resultW)+" "+getString(R.string.weekR));
    }

    private void restoreFromMBM(){
        MyBaseManager MBM = new MyBaseManager(this);

        Cursor k = MBM.giveAll();
        ProfileListContent.clearList();
        while(k.moveToNext()) {
            long position = k.getLong(0);
            String name = k.getString(1);
            String date = k.getString(2);
            Double weight = k.getDouble(3);
            String sex = k.getString(4);
            String type = k.getString(5);
            String speChar = k.getString(6);
            String picPath = k.getString(7);

            ProfileListContent.Profile profile = new ProfileListContent.Profile(position, name, date, weight, sex, type, speChar, picPath);
            ProfileListContent.addItem(profile);
        }
    }
}
