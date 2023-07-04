package ru.m210projects.Build.Net;

public class NetInfo {
	public String myip;
	public String extip;
	public String serverip;
	public String message;
	public int port;
	public int netready;
	public int plready = 1;
	public Thread waitThread;
	public boolean useUPnP;
	public int uPnPPort = -1;
	
	private String lastMessage;
	
	public void clear()
	{
		myip = extip = serverip = message = null;
		port = netready = 0;
		plready = 1;
	}
	
	public boolean waiting()
	{
		return waitThread.isAlive();
	}
	
	public boolean IsNewMessage()
	{
		boolean out = !message.equals(lastMessage);
		lastMessage = message;
		return out;
	}
}
