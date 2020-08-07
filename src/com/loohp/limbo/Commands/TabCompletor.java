package com.loohp.limbo.Commands;

import java.util.List;

public interface TabCompletor {
	
	public List<String> tabComplete(CommandSender sender, String[] args);

}
