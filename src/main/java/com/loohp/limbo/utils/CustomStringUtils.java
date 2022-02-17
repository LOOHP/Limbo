/*
 * This file is part of Limbo.
 *
 * Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2022. Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.loohp.limbo.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomStringUtils {
	
	public static boolean arrayContains(String compare, String[] args, boolean IgnoreCase) {
		return IgnoreCase ? Arrays.asList(args).stream().anyMatch(each -> each.equalsIgnoreCase(compare)) : Arrays.asList(args).stream().anyMatch(each -> each.equals(compare));
	}
	
	public static boolean arrayContains(String compare, String[] args) {
		return arrayContains(compare, args, true);
	}
	
	public static String[] splitStringToArgs(String str) {
		List<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();

		boolean insideQuote = false;

		for (char c : str.toCharArray()) {
		    if (c == '"') {
		        insideQuote = !insideQuote;
		    } else if (c == ' ' && !insideQuote) {
		    	if (sb.length() > 0) {
					tokens.add(sb.toString());
				}
		        sb.delete(0, sb.length());
		    } else {
		        sb.append(c);
		    }
		}
		tokens.add(sb.toString());
		
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public static int getIndexOfArg(String str, int ordinal) {
		StringBuilder sb = new StringBuilder();

		boolean insideQuote = false;
		
		int pos = 0;
		int found = 0;
		for (char c : str.toCharArray()) {
		    if (c == '"') {
		        insideQuote = !insideQuote;
		    } else if (c == ' ' && !insideQuote) {
		    	if (sb.length() > 0) {
					found++;
				}
		        sb.delete(0, sb.length());
		    } else {
		        sb.append(c);
		    }
		    if (found == ordinal) {
		    	return pos;
		    }
		    pos++;
		}
		
		return -1;
	}
	
}
