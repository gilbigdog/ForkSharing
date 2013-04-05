package com.mno.lab.fs.datatype;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mno.lab.fs.R;
import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.data.Data;
import com.samsung.ssl.smeshnet.data.User;

public class ApplicationType extends DefaultType {

    /**
     * Icon for ApplicationType
     */
    public static Drawable mIcon;

    private static final String PACKAGE_NAME = "com.android.settings";

    public static final String TYPE_IDENTIFIER = ".apk";

    /**
     * Package Name of application
     */
    public ApplicationInfo appInfo;

    private View mHelperView;

    public ApplicationType(Context ctx) {
        super(ctx);
    }

    @Override
    public void send(ManagedSession session, User user) {
        showSharingSelectDialog(session, user);
    }

    @Override
    public void broadcast(ManagedSession session) {
        showSharingSelectDialog(session, null);
    }

    @Override
    public Drawable getIcon() {
        return mIcon;
    }

    @Override
    public View getHelperView(LayoutInflater inflater, View convertView) {

        if (mHelperView == null) {
            mHelperView = inflater.inflate(R.layout.application_item, null);
            TextView nameTextView = (TextView) mHelperView.findViewById(R.id.app_name_view);
            ImageView iconImageView = (ImageView) mHelperView.findViewById(R.id.app_icon);

            PackageManager pm = mContext.getPackageManager();
            iconImageView.setBackground(pm.getApplicationIcon(appInfo));
            nameTextView.setText(pm.getApplicationLabel(appInfo));
        }

        return mHelperView;
    }

    @Override
    public void onSelect(Handler callback) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDestory() {
    }

    public static ApplicationType ParseVideoUri(Context ctx, Intent intent) throws IOException {
        if (mIcon == null) {
            mIcon = GetDefaultIcon(ctx, PACKAGE_NAME);
        }

        ApplicationType res = new ApplicationType(ctx);

        String pkgName = intent.getStringExtra(Intent.EXTRA_TEXT);
        try {
            res.appInfo = ctx.getPackageManager().getApplicationInfo(pkgName, 0);
            return res;
        } catch (NameNotFoundException e) {
            Logs.Log("Cannot find " + pkgName);
            e.printStackTrace();
        }

        return null;
    }

    private void showSharingSelectDialog(final ManagedSession session, final User user) {
        if (session == null) {
            Logs.Log("Session is not ready yet to send");
            return;
        }

        LayoutInflater factory = LayoutInflater.from(mContext);
        final View shareTypeDialogView = factory.inflate(
                R.layout.dialog_app_share, null);
        final AlertDialog shareTypeDialog = new AlertDialog.Builder(mContext).create();
        shareTypeDialog.setView(shareTypeDialogView);
        shareTypeDialogView.findViewById(R.id.dialog_app_share_apk_view).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user == null) {
                    session.sendFile(user, appInfo.sourceDir);
                } else {
                    session.broadcastFile(appInfo.sourceDir);
                }
                shareTypeDialog.dismiss();
            }
        });
        shareTypeDialogView.findViewById(R.id.dialog_app_share_info_view).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user == null) {
                    session.send(user, new ApplicationPkgData(appInfo.packageName));
                } else {
                    session.broadcast(new ApplicationPkgData(appInfo.packageName));
                }
                shareTypeDialog.dismiss();
            }
        });

        shareTypeDialog.show();
    }

    public static class ApplicationPkgData extends Data {

        private static final long serialVersionUID = -2511250282736488733L;

        public String pkgName;

        public ApplicationPkgData(String pkgName) {
            this.pkgName = pkgName;
        }
    }
}