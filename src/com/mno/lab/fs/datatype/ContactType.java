package com.mno.lab.fs.datatype;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

import net.sourceforge.cardme.engine.VCardEngine;
import net.sourceforge.cardme.vcard.VCard;
import net.sourceforge.cardme.vcard.features.PhotoFeature;
import net.sourceforge.cardme.vcard.features.TelephoneFeature;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mno.lab.fs.R;
import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.filetransfer.FileTransferStatusListener;
import com.samsung.ssl.smeshnet.filetransfer.SmeshnetFileTransfer;

public class ContactType extends DefaultType {

    /**
     * Icon for ContactType
     */
    public static Drawable mIcon = null;

    private static final String PACKAGE_NAME = "com.android.contacts";

    public static final String TYPE_IDENTIFIER = "ContactType_";

    public File fileName;

    private Bitmap pic;
    private String name;
    private String phone;

    private View mHelperView;

    public static class ContactViewHolder {

        ImageView iconImageView;
        TextView nameTextView;
        TextView phoneTextView;
    }

    private FileTransferStatusListener mFileTransferListener = new FileTransferStatusListener() {

        @Override
        public void onCancel(SmeshnetFileTransfer arg0) {
            // TODO : 취소되면
        }

        @Override
        public void onProgressUpdate(SmeshnetFileTransfer arg0, long arg1, long arg2) {
            // TODO 똑같이 업데이트?
        }

        @Override
        public void onTransferComplete(SmeshnetFileTransfer arg0, File arg1) {
            // TODO 파일 삭제 및 뷰 업데이트
            onDestory();
        }

        @Override
        public void onTransferFailed(SmeshnetFileTransfer arg0, Exception arg1) {
            // TODO 재시도 및 뷰 업데이트

        }
    };

    public ContactType(Context ctx) {
        super(ctx);
    }

    public static ContactType ParseContactUri(Context ctx, Intent intent, TRANSFER_TYPE type) throws IOException {
        if (mIcon == null) {
            mIcon = GetDefaultIcon(ctx, PACKAGE_NAME);
        }

        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        ContactType res = new ContactType(ctx);
        res.mTransferType = type;

        // Write received contact onto file
        AssetFileDescriptor afd = ctx.getContentResolver().openAssetFileDescriptor(
                uri, "r");
        res.fileName = GetCacheDir(ctx, TYPE_IDENTIFIER + UUID.randomUUID().toString());
        WriteDataToFile(afd.createInputStream(), res.fileName);

        // Parse VCard for helpView
        VCardEngine ve = new VCardEngine();
        VCard vc = ve.parse(res.fileName);

        // Get Photo
        Iterator<PhotoFeature> itPf = vc.getPhotos();
        while (itPf.hasNext()) {
            PhotoFeature pf = itPf.next();
            byte[] data = pf.getPhoto();
            res.pic = BitmapFactory.decodeByteArray(data, 0, data.length);
            break;
        }

        // Get Name
        res.name = vc.getName().getGivenName() + " " + vc.getName().getFamilyName();

        // Get Phone Number
        Iterator<TelephoneFeature> itTf = vc.getTelephoneNumbers();
        while (itTf.hasNext()) {
            res.phone = itTf.next().getTelephone();
            break;
        }

        return res;
    }

    @Override
    public void send(ManagedSession session, User user) {
        super.send(session, user);
        if (session == null || user == null) {
            Logs.Log("Cannot send via null");
            return;
        }

        session.sendFile(user, fileName.getName());
        session.getFileTransferManager().addFileTransferStatusListener(mFileTransferListener);
    }

    @Override
    public void broadcast(ManagedSession session) {
        super.broadcast(session);
        if (session == null) {
            Logs.Log("Cannot send via null");
            return;
        }

        session.broadcastFile(fileName.getName());
        session.getFileTransferManager().addFileTransferStatusListener(mFileTransferListener);
    }

    @Override
    public void onDestory() {
        if (mDestroyListener != null) {
            mDestroyListener.onDestroyListener();
        }

        if (fileName != null) {
            Logs.Log(fileName.getName() + " gets deleted : " + fileName.delete());
        }

        if (mHelperView != null) {
            mHelperView = null;
        }

        if (pic != null) {
            pic.recycle();
        }
    }

    @Override
    public Drawable getIcon() {
        return mIcon;
    }

    @Override
    public void onSelect() {
        super.onSelect();
        // TODO : action?
    }

    @Override
    public View getHelperView(LayoutInflater inflater, View convertView) {

        if (mHelperView == null) {
            LinearLayout mHelperView = (LinearLayout) inflater.inflate(R.layout.default_type_view, null);
            TextView type_tv = (TextView) mHelperView.findViewById(R.id.transfer_type_tv);

            switch (mTransferType) {
                case SEND:
                    type_tv.setText("S");
                    break;
                case RECEIVE:
                    type_tv.setText("R");
                    break;
            }

            View typeView = inflater.inflate(R.layout.contact_item, null);
            TextView nameTextView = (TextView) mHelperView.findViewById(R.id.contact_name);
            TextView phoneTextView = (TextView) mHelperView.findViewById(R.id.contact_phonenumber);
            ImageView iconImageView = (ImageView) mHelperView.findViewById(R.id.contact_pic);
            nameTextView.setText(name);
            phoneTextView.setText(phone);
            iconImageView.setImageBitmap(pic);

            mHelperView.addView(typeView);
        }

        return mHelperView;
    }

    public static String readFileAsString(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line, results = "";
        while ((line = reader.readLine()) != null) {
            results += line;
        }
        reader.close();
        return results;
    }

    @Override
    public DATA_TYPE getType() {
        return DATA_TYPE.CONTACT;
    }
}