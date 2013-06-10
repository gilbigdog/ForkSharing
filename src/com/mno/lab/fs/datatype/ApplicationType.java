package com.mno.lab.fs.datatype;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        super.send(session, user);
        CharSequence appName = mContext.getPackageManager().getApplicationLabel(appInfo);
        session.send(user, new ApplicationPkgData(appInfo.packageName, appName));

        // TODO : send APK or Package Name only?
        // showSharingSelectDialog(session, user);
    }

    @Override
    public void broadcast(ManagedSession session) {
        super.broadcast(session);
        CharSequence appName = mContext.getPackageManager().getApplicationLabel(appInfo);
        session.broadcast(new ApplicationPkgData(appInfo.packageName, appName));

        // TODO : send APK or Package Name only?
        // showSharingSelectDialog(session, null);
    }

    @Override
    public Drawable getIcon() {
        return mIcon;
    }

    @Override
    public View getHelperView(LayoutInflater inflater, View convertView) {

        if (mHelperView == null) {
            LinearLayout mHelperView = (LinearLayout) inflater.inflate(R.layout.default_type_view, null);
            TextView type_tv = (TextView) mHelperView.findViewById(R.id.transfer_type_tv);

            View typeView = inflater.inflate(R.layout.application_item, null);
            TextView nameTextView = (TextView) mHelperView.findViewById(R.id.app_name_view);
            ImageView iconImageView = (ImageView) mHelperView.findViewById(R.id.app_icon);
            switch (mTransferType) {
                case SEND:
                    type_tv.setText("S");
                    PackageManager pm = mContext.getPackageManager();
                    iconImageView.setBackground(pm.getApplicationIcon(appInfo));
                    nameTextView.setText(pm.getApplicationLabel(appInfo));
                    break;
                case RECEIVE:
                    type_tv.setText("R");
                    nameTextView.setText(appInfo.name);
                    break;
            }
            mHelperView.addView(typeView);
        }

        return mHelperView;
    }

    @Override
    public void onSelect() {
        super.onSelect();
        // TODO : action?
    }

    @Override
    public void onDestory() {
        if (appInfo != null) {
            appInfo = null;
        }

        if (mHelperView != null) {
            mHelperView = null;
        }
    }

    public static ApplicationType ParseVideoUri(Context ctx, Intent intent, TRANSFER_TYPE type) throws IOException {
        if (mIcon == null) {
            mIcon = GetDefaultIcon(ctx, PACKAGE_NAME);
        }

        ApplicationType res = new ApplicationType(ctx);
        res.mTransferType = type;

        String pkgName = intent.getStringExtra(Intent.EXTRA_TEXT);

        switch (type) {
            case SEND:
                try {
                    res.appInfo = ctx.getPackageManager().getApplicationInfo(pkgName, 0);
                    return res;
                } catch (NameNotFoundException e) {
                    Logs.Log("Cannot find " + pkgName);
                    e.printStackTrace();
                }
                break;
            case RECEIVE:
                res.appInfo = new ApplicationInfo();
                res.appInfo.packageName = pkgName;
                return res;
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
                CharSequence appName = mContext.getPackageManager().getApplicationLabel(appInfo);
                if (user == null) {
                    session.send(user, new ApplicationPkgData(appInfo.packageName, appName));
                } else {
                    session.broadcast(new ApplicationPkgData(appInfo.packageName, appName));
                }
                shareTypeDialog.dismiss();
            }
        });

        shareTypeDialog.show();
    }

    public static class ApplicationPkgData extends Data {

        private static final long serialVersionUID = -2511250282736488733L;

        // TODO : Icon 보내기
        public String pkgName;
        public String appName;

        public ApplicationPkgData(String pkgName, CharSequence appName) {
            this.pkgName = pkgName;
            this.appName = appName.toString();
        }
    }

    @Override
    public DATA_TYPE getType() {
        return DATA_TYPE.APP;
    }
}