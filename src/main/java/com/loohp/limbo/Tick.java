package com.loohp.limbo;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Tick {
	
	private TimerTask timerTask;
	
	public Tick(Limbo instance) {
		this.timerTask = new TimerTask () {
		    @Override
		    public void run () {
		    	if (instance.isRunning()) {
		    		instance.getPlayers().forEach(each -> {
		    			if (each.clientConnection.isReady()) {
							try {
								each.playerInteractManager.update();
							} catch (IOException e) {
								e.printStackTrace();
							}
							/*
							try {
								each.getDataWatcher().update();
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
							*/
		    			}
					});
		    		instance.getWorlds().forEach(each -> {
						try {
							each.update();
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
						}
					});
		    	} else {
		    		this.cancel();
		    	}
		    }
		};
		new Timer().schedule(timerTask, 0, Math.round(1000 / instance.getServerProperties().getDefinedTicksPerSecond()));
	}
	
	public void cancel() {
		timerTask.cancel();
	}
	
}
