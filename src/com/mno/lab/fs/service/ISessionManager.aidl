package com.mno.lab.fs.service;

interface ISessionManager{
	void initSession();
    void connect(String session, boolean randomGen);
    void shareVia(in Intent intent);
    String getSessionName();
    boolean isConnected();
}