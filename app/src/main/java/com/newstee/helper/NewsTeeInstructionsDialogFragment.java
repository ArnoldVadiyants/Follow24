package com.newstee.helper;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.newstee.R;

/**
 * Created by Arnold on 21.05.2016.
 */
public class NewsTeeInstructionsDialogFragment extends DialogFragment {
    public static final String DIALOG_INSTRUCTIONS = "dialog_instructions";
    public static final String EXTRA_MSG = "message";
    public static final String EXTRA_ICON_ID = "icon_id";
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_IS_WELCOME = "is_welcome";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        int icon_id = bundle.getInt(EXTRA_ICON_ID);
        String title = bundle.getString(EXTRA_TITLE);
        String message = bundle.getString(EXTRA_MSG);
        boolean isWelcome = bundle.getBoolean(EXTRA_IS_WELCOME, false);

        View v = inflater.inflate(R.layout.instructions_layout, container);
        TextView welcomeTxt = (TextView)v.findViewById(R.id.instructions_welcome_textView);
        TextView titleTxt = (TextView)v.findViewById(R.id.instructions_title_view);
        TextView msgTxt = (TextView)v.findViewById(R.id.instructions_message_TextView);
        ImageView iconImg = (ImageView)v.findViewById(R.id.instructions_image_view);
        ImageButton dismissBtn = (ImageButton)v.findViewById(R.id.instructions_dismiss_imageButton);
        msgTxt.setText(message);
        titleTxt.setText(title);
        iconImg.setImageResource(icon_id);
        if(!isWelcome)
        {
            welcomeTxt.setVisibility(View.GONE);
        }
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
       getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // remove background dim
       getDialog().getWindow().setDimAmount(0);
        return v;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }
    /*

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



      *//*  // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View v = inflater.inflate(R.layout.instructions_layout, null);
        TextView msgTxt = (TextView)v.findViewById(R.id.instructions_message_TextView);
        ImageButton dismissBtn = (ImageButton)v.findViewById(R.id.instructions_dismiss_imageButton);
        msgTxt.setText(message);
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*//*
        Dialog dialog = new AlertDialog.Builder(getActivity()).create();

        dialog.setCanceledOnTouchOutside(false);

        return dialog;


    }*/

    public static NewsTeeInstructionsDialogFragment newInstance(int icon_id, String title, String message, boolean isWelcome) {
        Bundle args = new Bundle();
        args.putInt(EXTRA_ICON_ID, icon_id);
        args.putString(EXTRA_TITLE, title);
        args.putString(EXTRA_MSG, message);
        args.putBoolean(EXTRA_IS_WELCOME, isWelcome);
        NewsTeeInstructionsDialogFragment fragment = new NewsTeeInstructionsDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
