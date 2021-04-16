package com.loohp.limbo.commands;

import java.util.List;

public interface TabCompletor {

    List<String> tabComplete(CommandSender sender, String[] args);

}
