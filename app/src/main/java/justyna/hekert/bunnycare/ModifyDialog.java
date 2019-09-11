package justyna.hekert.bunnycare;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class ModifyDialog extends DialogFragment {

    private OnModifyDialogInteractionListener mListener;

    public ModifyDialog() {
        // Required empty public constructor
    }

    static ModifyDialog newInstance(){
        return new ModifyDialog();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getString(R.string.modify_question));
        builder.setPositiveButton(getString(R.string.dialog_edit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                mListener.onDialogEditClick(ModifyDialog.this);
            }
        });

        builder.setNegativeButton(getString(R.string.dialog_delete), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogDeleteClick(ModifyDialog.this);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnModifyDialogInteractionListener) {
            mListener = (OnModifyDialogInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnModifyDialogInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnModifyDialogInteractionListener {
        void onDialogDeleteClick(DialogFragment dialog);
        void onDialogEditClick(DialogFragment dialog);
    }
}
