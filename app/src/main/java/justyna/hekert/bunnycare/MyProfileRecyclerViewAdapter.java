package justyna.hekert.bunnycare;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import justyna.hekert.bunnycare.ProfileFragment.OnListFragmentInteractionListener;
import justyna.hekert.bunnycare.profiles.ProfileListContent.Profile;

import java.util.List;


public class MyProfileRecyclerViewAdapter extends RecyclerView.Adapter<MyProfileRecyclerViewAdapter.ViewHolder> {

    private final List<Profile> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyProfileRecyclerViewAdapter(List<Profile> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Profile profile = mValues.get(position);
        holder.mProfile = profile;
        holder.mNameView.setText(profile.name);

        final String picPath = profile.picPath;
        Context context = holder.mView.getContext();

        if(picPath != null && !picPath.isEmpty()){
            //if picPath is set
            Bitmap cameraImage = PicUtils.decodePic(profile.picPath, 60, 60);
            holder.mProfileImageView.setImageBitmap(cameraImage);
        }else {
            holder.mProfileImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.logo_bg));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentClickInteraction(holder.mProfile, position);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick (View v) {
                if (null != mListener) {
                    mListener.onListFragmentLongClickInteraction(holder.mProfile, position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mProfileImageView;
        public final TextView mNameView;
        public Profile mProfile;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mProfileImageView = view.findViewById(R.id.picture);
            mNameView = view.findViewById(R.id.name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
