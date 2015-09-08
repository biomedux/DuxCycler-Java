package com.mypcr.constant;

import java.util.ArrayList;

import com.mypcr.beans.Action;

public class ProtocolConstants {
	private static final int labels[][] = { {1, 2, 3, 4, 5, Action.AF_GOTO, 6, 7}, 
											{1, 2, 3, 4, Action.AF_GOTO, 5, 6, 7, Action.AF_GOTO, 8, 9}, };
	private static final int temps[][]  = { {50, 95, 95, 60, 72, 3, 72, 4}, 
											{95, 94, 60, 72, 2, 94, 50, 72, 5, 72, 4}, };
	private static final int times[][]  = { {1800, 900, 30, 30, 60, 40, 300, 0},
											{720, 30, 30, 30, 10, 30, 60, 30, 40, 300, 0}, };
	private static final String names[] = { "Dengue", "HPV PCR" };
	
	public static final String ext = ".pl";
	
	public static ArrayList<Action[]> getBuiltProtocols(){
		ArrayList<Action[]> list = new ArrayList<Action[]>();
		
		for(int i=0; i<labels.length; ++i){
			Action[] actions = new Action[labels[i].length];
			for(int j=0; j<actions.length; ++j){
				actions[j] = new Action(names[i] + ext);
				
				String label = (labels[i][j] == Action.AF_GOTO) ? "GOTO" : (labels[i][j] + "");
				actions[j].set(label, temps[i][j]+"", times[i][j]+"");
			}
			list.add(actions);
		}
		
		return list;
	}
}
