package justyna.hekert.bunnycare;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import justyna.hekert.bunnycare.profiles.ProfileListContent;


public class InfoProfileFragment extends Fragment implements View.OnClickListener{

    public static final int REQUEST_IMAGE_CAPTURE = 1; // request code for image capture
    private String mCurrentPhotoPath; // String used to save the path of the picture
    private ProfileListContent.Profile mDisplayedProfile;

    public InfoProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated (@Nullable Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        FragmentActivity activity = getActivity();

        Intent intent = activity.getIntent();
        if(intent !=null) {
            ProfileListContent.Profile recivedItem = intent.getParcelableExtra(MainActivity.ProfileExtra);
            if(recivedItem != null) {
                try {
                    displayProfile(recivedItem);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_profile, container, false);
    }

    public String convertAge (int ageY, int ageM, int ageW) {
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
        return(Integer.toString(resultY)+" "+getString(R.string.yearR)+" "+Integer.toString(resultM)+" "+getString(R.string.monthR)+" "+Integer.toString(resultW)+" "+getString(R.string.weekR));
    }

    public void displayProfile(ProfileListContent.Profile profile) throws ParseException {
        FragmentActivity activity = getActivity();

        (activity.findViewById(R.id.displayFragment)).setVisibility(View.VISIBLE);

        TextView InfoName= activity.findViewById(R.id.InfoName);
        TextView InfoDate = activity.findViewById(R.id.InfoDate);
        TextView InfoConverter = activity.findViewById(R.id.InfoConverter);
        TextView InfoSex= activity.findViewById(R.id.InfoSex);
        TextView InfoWeight = activity.findViewById(R.id.InfoWeight);
        TextView InfoType = activity.findViewById(R.id.InfoType);
        TextView InfoSpecialChar = activity.findViewById(R.id.InfoSpecChar);
        final ImageView InfoImage = activity.findViewById(R.id.InfoImage);

        InfoName.setText(profile.name);
        InfoDate.setText(profile.dateOfBirth);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date Birthdate = sdf.parse(profile.dateOfBirth);
        Date now = new Date();
        long diff = now.getTime() - Birthdate.getTime();
        float days = (diff / (1000*60*60*24));

        int ageY = Math.round(days/365);
        int ageM = Math.round((days % 365)/30);
        int ageW = Math.round(((days % 365) % 30)/7);

        InfoConverter.setText(convertAge(ageY, ageM, ageW));

        InfoSex.setText(profile.sex);
        InfoWeight.setText(profile.weight.toString() + " kg");
        InfoType.setText(profile.type);
        InfoSpecialChar.setText(profile.specChar);

        if(profile.picPath != null && !profile.picPath.isEmpty()){
            Handler handler = new Handler();
            InfoImage.setVisibility(View.INVISIBLE);
            handler. postDelayed(new Runnable() {
                @Override
                public void run(){
                    InfoImage.setVisibility(View.VISIBLE);
                    Bitmap cameraImage = PicUtils.decodePic(mDisplayedProfile.picPath,
                            InfoImage.getWidth(),
                            InfoImage.getHeight());
                    InfoImage.setImageBitmap(cameraImage);
                    }
                }, 200);

        } else{
            InfoImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.logo_bg));
        }
        mDisplayedProfile = profile;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFIleName = mDisplayedProfile.name + timeStamp + " ";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFIleName, ".jpg", storageDir );
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }catch (IOException ex){

            }

            if(photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(), getString(R.string.myFileprovider), photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
}
