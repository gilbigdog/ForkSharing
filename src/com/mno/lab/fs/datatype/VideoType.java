package com.mno.lab.fs.datatype;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
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

public class VideoType extends DefaultType {

    public VideoType(Context ctx) {
        super(ctx);
        // TODO Auto-generated constructor stub
    }

    /**
     * Icon for ImageType
     */
    public static Drawable mIcon;

    public File fileName;

    private ImageView mHelperView;

    private static final String PACKAGE_NAME = "com.google.android.videos";

    public static final String TYPE_IDENTIFIER = "VideoType_";

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

    public static VideoType ParseVideoUri(Context ctx, Intent intent, TRANSFER_TYPE type) throws IOException {
        if (mIcon == null) {
            mIcon = GetDefaultIcon(ctx, PACKAGE_NAME);
            if (mIcon == null) {
                mIcon = ctx.getResources().getDrawable(R.drawable.ic_launcher_video);
            }
        }

        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        VideoType res = new VideoType(ctx);
        res.mTransferType = type;

        // Write received image onto file
        AssetFileDescriptor afd = ctx.getContentResolver().openAssetFileDescriptor(
                uri, "r");
        res.fileName = GetCacheDir(ctx, TYPE_IDENTIFIER + UUID.randomUUID().toString());
        WriteDataToFile(afd.createInputStream(), res.fileName);
        Logs.Log("Video stored at " + res.fileName.getAbsolutePath());
        return res;
    }

    @Override
    public void send(ManagedSession session, User user) {
        super.send(session, user);
        if (session == null || user == null) {
            Logs.Log("Cannot send via null");
            return;
        }

        session.sendFile(user, fileName.getAbsolutePath());
        session.getFileTransferManager().addFileTransferStatusListener(mFileTransferListener);
    }

    @Override
    public void broadcast(ManagedSession session) {
        super.broadcast(session);
        if (session == null) {
            Logs.Log("Cannot send via null");
            return;
        }

        session.broadcastFile(fileName.getAbsolutePath());
        session.getFileTransferManager().addFileTransferStatusListener(mFileTransferListener);
    }

    @Override
    public void onDestory() {
        if (fileName != null) {
            Logs.Log(fileName.getName() + " gets deleted : " + fileName.delete());
        }

        if (mHelperView != null) {
            mHelperView = null;
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

            ImageView typeView = new ImageView(mContext);

            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(fileName.getAbsolutePath(),
                    MediaStore.Images.Thumbnails.MICRO_KIND);

            // Resize Bitmap
            final float widthRatio = (float) mIcon.getIntrinsicWidth() / (float) thumb.getWidth();
            final float heightRatio = (float) mIcon.getIntrinsicHeight() / (float) thumb.getHeight();
            double sampleSizeInFraction = widthRatio > heightRatio ? heightRatio : widthRatio;
            Bitmap resizedThumb = Bitmap.createScaledBitmap(thumb, (int) (thumb.getWidth() * sampleSizeInFraction),
                    (int) (thumb.getHeight() * sampleSizeInFraction), false);
            thumb.recycle();
            typeView.setImageBitmap(resizedThumb);

            mHelperView.addView(typeView);
        }

        return mHelperView;
    }

    @Override
    public DATA_TYPE getType() {
        return DATA_TYPE.VIDEO;
    }
}