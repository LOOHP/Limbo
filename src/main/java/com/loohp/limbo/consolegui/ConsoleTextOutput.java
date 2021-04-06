package com.loohp.limbo.consolegui;

import com.loohp.limbo.Limbo;

public class ConsoleTextOutput {
	
	public static void appendText(String string) {
		if (!Limbo.noGui) {
			GUI.textOutput.setText(GUI.textOutput.getText() + string);
			GUI.scrollPane.getVerticalScrollBar().setValue(GUI.scrollPane.getVerticalScrollBar().getMaximum());
		}
	}
	
	public static void appendText(String string, boolean isWriteLine) {
		if (isWriteLine) {
			appendText(string + "\n");
		} else {
			appendText(string);
		}
	}

}
