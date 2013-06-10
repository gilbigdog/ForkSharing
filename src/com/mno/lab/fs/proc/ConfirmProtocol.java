package com.mno.lab.fs.proc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.mno.lab.fs.datatype.DefaultType;
import com.mno.lab.fs.utils.Logs;
import com.samsung.ssl.smeshnet.ManagedSession;
import com.samsung.ssl.smeshnet.data.Data;
import com.samsung.ssl.smeshnet.data.User;
import com.samsung.ssl.smeshnet.internal.session.BaseSession.DataReceivedListener;

@SuppressLint("UseSparseArrays")
public class ConfirmProtocol {

    public static final String TEST_MESSAGE = "aa";

    public static final String TO_USER = "destuser";

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

    public static enum THUMBNAIL {
        HAS,
        NOT
    }

    /**
     * Stores Types to wait for confirmation message from receiver
     */
    private Map<String, DefaultType> mWaitConfirm;
    private ManagedSession mSession;

    /**
     * Data listener via this mSession
     */
    private DataReceivedListener mDataReceivedListener = new DataReceivedListener() {

        @Override
        public void onReceivedData(Data data) {
            if (data instanceof Message) {
                User from = data.getFromUser();
                Matcher m = CONFIRM_PROTOCOL_RECEIVE_SEND_PATTERN.matcher(((Message) data).message);
                if (m.matches() && m.find()) {
                    responseConfirmMessage(from, m);
                    return;
                }

                m = CONFIRM_PROTOCOL_RECEIVE_RESPONSE_PATTERN.matcher(((Message) data).message);
                if (m.matches() && m.find()) {
                    handleConfirmResponse(from, m);
                    return;
                }

                // TODO It is test output
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

    public boolean sendData(User user, DefaultType type) {
        if (!mSession.isConnected()) {
            Logs.Log("Please Join session first");
            return false;
        }

        String uuid = UUID.fromString(String.valueOf(type.hashCode())).toString();
        if (mWaitConfirm == null) {
            mWaitConfirm = new HashMap<String, DefaultType>();
        } else if (mWaitConfirm.containsKey(uuid)) {
            Logs.Log("sendData : Intent + " + uuid + " already exists");
            return false;
        }

        // Put uuid to hashMap to wait reponse before send actual data.
        mWaitConfirm.put(uuid, type);

        // Generate confirm Message
        String confirmMessage = GenerateConfirmMessage(uuid, type);

        // Send generated message to ask confirmation of sending actual data
        mSession.send(user, new Message(confirmMessage));
        return true;
    }

    private static String GenerateConfirmMessage(String uuid, DefaultType type) {
        // TODO : distinguish types
        // TODO : Thumbnail !!
        return String.format(CONFIRM_PROTOCOL_SEND, uuid, type.getType(), THUMBNAIL.NOT.toString());
    }

    private static String GenerateResponseMessage(String uuid, boolean isAnswer) {
        return String.format(CONFIRM_PROTOCOL_RESPONSE, uuid, isAnswer);
    }

    /**
     * Handles the confirmation response message
     * 
     * It will search type corresponding to the UUID then send image, video, raw
     * files, or Application
     * 
     * @param from
     * @param m
     */
    private void handleConfirmResponse(User from, Matcher m) {
        if (Boolean.valueOf(m.group(2))) {
            String uuid = m.group(1);
            if (mWaitConfirm.containsKey(uuid)) {
                DefaultType type = mWaitConfirm.get(uuid);
                type.send(mSession, from);
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
     * 
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