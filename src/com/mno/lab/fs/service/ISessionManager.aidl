package com.mno.lab.fs.service;

interface ISessionManager{
	void initSession();
<<<<<<< HEAD
    void connect(String session, boolean randomGen);
    void sendMessage(String message);
=======
    void connect(String session);
    void shareVia(in Intent intent);
>>>>>>> efd8a6f... New Update
    String getSessionName();
}