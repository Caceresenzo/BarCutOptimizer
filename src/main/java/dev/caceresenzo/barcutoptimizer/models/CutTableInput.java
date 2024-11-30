package dev.caceresenzo.barcutoptimizer.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CutTableInput {
	
	public double length;
	public int[] cutAngles;
	public int quantity;
	
	public CutTableInput() {
		this.length = -1;
		this.cutAngles = new int[] { 90, 90 };
		this.quantity = 1;
	}
	
	public double getLength() {
		return length;
	}
	
	public void setLength(double length) {
		this.length = length;
	}
	
	public int[] getCutAngles() {
		return cutAngles;
	}
	
	public void setCutAngles(int[] cutAngles) {
		this.cutAngles = cutAngles;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public Cut toCut() {
		return Cut.of(length, cutAngles[0], cutAngles[1]);
	}
	
	public static CutTableInput fromCut(Cut cut, int quantity) {
		CutTableInput cutTableInput = new CutTableInput();
		
		cutTableInput.setLength(cut.getLength());
		cutTableInput.setCutAngles(cut.getCutAngles());
		cutTableInput.setQuantity(quantity);
		
		return cutTableInput;
	}
	
	public static List<CutTableInput> createListFromBarReference(BarReference barReference) {
		List<CutTableInput> list = new ArrayList<>();
		Map<Cut, Integer> countMap = barReference.computeCutCountMap();
		
		for (Entry<Cut, Integer> entry : countMap.entrySet()) {
			Cut cut = entry.getKey();
			int quantity = entry.getValue();
			
			list.add(fromCut(cut, quantity));
		}
		
		return list;
	}
	
}