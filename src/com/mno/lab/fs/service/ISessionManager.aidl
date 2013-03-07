package com.mno.lab.fs.service;

interface ISessionManager{
	void initSession();
    void connect(String session, boolean randomGen);
    void sendMessage(String message);
    String getSessionName();
}