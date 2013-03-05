package com.mno.lab.fs.proc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.data.Data;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.DataReceivedListener;

@SuppressLint("UseSparseArrays")
public class ConfirmProtocol {

    public static final String TEST_MESSAGE = "aa";

    /**
     * UUID / Type / existence of thumbnail
     */
    private static final String CONFIRM_PROTOCOL_SEND = "CONFIRM/%s/%s/%s";
    private static final Pattern CONFIRM_PROTOCOL_RECEIVE_SEND_PATTERN = Pattern
            .compile("^CONFIRM/(\\S+)/(\\S+)/(\\S+)$");

    /**
     * UUID / answer
     */
    private static final String CONFIRM_PROTOCOL_RESPONSE = "RESONSE/%s/%s";
    private static final Pattern CONFIRM_PROTOCOL_RECEIVE_RESPONSE_PATTERN = Pattern
            .compile("^RESPONSE/(\\S+)/(\\S+)$");

    private Context mContext;

    public static enum DATA_TYPE {
        IMAGE,
        VIDEO,
        FILE,
        APP,
        MESSAGE,
        RAW
    };

    public static enum THUMBNAIL {
        HAS,
        NOT
    }

    /**
     * Pending messages
     */
    private Map<String, Intent> mWaitConfirm;
    private ManagedSession mSession;

    /**
     * Data listener via this mSession
     */
    private DataReceivedListener mDataReceivedListener = new DataReceivedListener() {

        @Override
        public void onReceivedData(Data data) {
            User from = data.getFromUser();
            if (data instanceof Message) {
                Matcher m = CONFIRM_PROTOCOL_RECEIVE_SEND_PATTERN.matcher(((Message) data).message);
                if (m.matches() && m.find()) {
                    responseConfirmMessage(from, m);
                    return;
                }

                m = CONFIRM_PROTOCOL_RECEIVE_RESPONSE_PATTERN.matcher(((Message) data).message);
                if (m.matches() && m.find()) {
                    handleConfirmResponse(from, m);
                }

                Toast.makeText(mContext, "Receive Message : " + ((Message) data).message, Toast.LENGTH_LONG).show();
            }
            // TODO Support more Data Types
        }

        @Override
        public void onReceivedData(User user, byte[] data) {
            // TODO for RAW data type
        }
    };

    public ConfirmProtocol(Context context, ManagedSession session) {
        if (context == null || session == null) {
            throw new IllegalArgumentException();
        }
        mContext = context;
        mSession = session;
        mSession.addDataReceivedListener(mDataReceivedListener);
    }

    public boolean sendData(User user, Intent intent) {
        if (!mSession.isConnected()) {
            Logs.Log("Please Join session first");
            return false;
        }

        String uuid = UUID.fromString(String.valueOf(intent.hashCode())).toString();
        if (mWaitConfirm == null) {
            mWaitConfirm = new HashMap<String, Intent>();
        } else if (mWaitConfirm.containsKey(uuid)) {
            Logs.Log("sendData : Intent + " + uuid + " already exists");
            return false;
        }

        // Put uuid to hashMap to wait reponse before send actual data.
        mWaitConfirm.put(uuid, intent);

        // Generate Message in certain format
        String confirmMessage = GenerateConfirmMessage(uuid, intent);

        // Send generated message to ask confirmation of sending actual data
        mSession.send(user, new Message(confirmMessage));
        return true;
    }

    private static String GenerateConfirmMessage(String uuid, Intent intent) {
        // TODO : distinguish types
        // TODO : Thumbnail !!
        return String.format(CONFIRM_PROTOCOL_SEND, uuid, DATA_TYPE.MESSAGE.toString(), THUMBNAIL.NOT.toString());
    }

    private static String GenerateResponseMessage(String uuid, boolean isAnswer) {
        return String.format(CONFIRM_PROTOCOL_RESPONSE, uuid, isAnswer);
    }

    private static Data GetUserData(Intent intent) {
        // TODO should support more data types
        return new Message(intent.getStringExtra(TEST_MESSAGE));
    }

    /**
     * Handles the confirm response message
     * 
     * It will search intent corresponding to the UUID then perform action such as
     * send image, video, raw files, or Application
     * @param from
     * @param m
     */
    private void handleConfirmResponse(User from, Matcher m) {
        if (Boolean.valueOf(m.group(2))) {
            String uuid = m.group(1);
            if (mWaitConfirm.containsKey(uuid)) {
                Intent intent = mWaitConfirm.get(uuid);
                mSession.send(from, GetUserData(intent));
                // TODO Should support more types
            } else {
                Logs.Log("onReceivedData : No Intent that is belong to " + uuid + " does not exists.");
            }
        }
    }

    /**
     * Response confirm message to from user
     * 
     * It will ask to user to have confirmation before receiving actual data.
     * Once a user agrees, it will send response message to from user
     * @param from
     * @param m
     */
    private void responseConfirmMessage(User from, Matcher m) {
        String uuid = m.group(1);
        String type = m.group(2);
        String thumbnail = m.group(3);

        String response = GenerateResponseMessage(uuid, true);
        mSession.send(from, new Message(response));
        // TODO Should callback view to ask it to User
    }
}