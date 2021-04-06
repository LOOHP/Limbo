package com.loohp.limbo.events;

public interface Cancellable {
	
	public void setCancelled(boolean cancelled);
	
	public boolean isCancelled();

}
