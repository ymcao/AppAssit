package com.appassit.widget.battery;

public interface IObserver {

	public void register();

	public void unRegister();

	public boolean isRegister();
}