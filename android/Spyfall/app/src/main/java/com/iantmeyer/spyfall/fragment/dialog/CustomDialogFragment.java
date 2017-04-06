package com.iantmeyer.spyfall.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class CustomDialogFragment extends DialogFragment {

    private static final String TITLE = "key_title";
    private static final String MESSAGE = "key_message";
    private static final String CONFIRM = "key_confirm";
    private static final String DENY = "key_deny";
    private static final String CLOSE = "key_close";
    private static final String TEXT_GRAVITY = "key_text_gravity";

    public static class Builder {
        private final Bundle mBundle;

        public Builder() {
            mBundle = new Bundle();
        }

        public Builder setTitle(String title) {
            mBundle.putString(TITLE, title);
            return this;
        }

        public Builder setMessage(String message) {
            mBundle.putString(MESSAGE, message);
            return this;
        }

        public Builder setConfirm(String confirm) {
            mBundle.putString(CONFIRM, confirm);
            return this;
        }

        public Builder setDeny(String deny) {
            mBundle.putString(DENY, deny);
            return this;
        }

        public Builder setClose(String close) {
            mBundle.putString(CLOSE, close);
            return this;
        }

        public Builder setTextGravity(int gravity) {
            mBundle.putInt(TEXT_GRAVITY, gravity);
            return this;
        }

        public CustomDialogFragment build() {
            CustomDialogFragment f = new CustomDialogFragment();
            f.setArguments(mBundle);
            return f;
        }
    }

    public CustomDialogFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof DialogListener)) {
            throw new ClassCastException(activity.toString() + " must implement DialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView tv = (TextView) getDialog().findViewById(android.R.id.message);
        if (getArguments().containsKey(TEXT_GRAVITY)) {
            tv.setGravity(getArguments().getInt(TEXT_GRAVITY));
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final String tag = getTag();
        String title = bundle.getString(TITLE);
        String confirm = bundle.getString(CONFIRM);
        String deny = bundle.getString(DENY);
        String close = bundle.getString(CLOSE);
        String message = bundle.getString(MESSAGE);
        Spanned htmlMessage;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            htmlMessage = Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY);
        } else {
            htmlMessage = Html.fromHtml(message);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setMessage(htmlMessage);
        if (title != null && title.length() > 0) {
            builder.setTitle(title);
        }
        if (deny != null && deny.length() > 0) {
            builder.setNegativeButton(deny, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogListener) getActivity()).onDeny(tag);
                }
            });
        }
        if (confirm != null && confirm.length() > 0) {
            builder.setPositiveButton(confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ((DialogListener) getActivity()).onConfirm(tag);
                }
            });
        }
        if (close != null && close.length() > 0) {
            builder.setNeutralButton(close, null);
        }
        return builder.create();
    }
}