package com.loohp.limbo.Events;

public interface Cancellable {
	
	public void setCancelled(boolean cancelled);
	
	public boolean isCancelled();

}
