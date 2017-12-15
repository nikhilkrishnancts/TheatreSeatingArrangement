/**
 * 
 */
package org.springframework.theatreseating;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Nikhilkrishnan 
 * Version : 1.0 
 * Project : TheatreSeating
 *
 */
public class MainSeating {
	// Define a static logger variable so that it references the
	// Logger instance named "MyApp".
	private static final Logger logger = LogManager.getLogger(MainSeating.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MainSeating ms = new MainSeating();
		ClassLoader classLoader = ms.getClassLoader();
		// Create 2-dimensional array.
		String[][] availableSeats = new String[20][20];//In real time scenario size should be dynamic
		String[][] custReqst = new String[20][20];//In real time scenario size should be dynamic
		boolean resReq = false;
		LinkedHashMap<String, String> custReq = new LinkedHashMap<String, String>();

		try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource("input.txt").getFile()))) {

			String line;
			int i = 0;

			while ((line = br.readLine()) != null) {
				if (line.trim().equals("")) {
					resReq = true;
				}
				String sections[] = line.split(" ");

				for (int j = 0; j < sections.length; j++) {
					if (!resReq) {
						availableSeats[i][j] = sections[j];
					} else {
						if (!(line.trim().equals(""))) {
							custReqst[i][j] = sections[j];
							custReq.put(sections[0], sections[1]);
						}
					}
				}
				i = i + 1;

			}

		} catch (IOException e) {
			logger.error("Error while handling with file : "+e.getMessage());
		}
		LinkedHashMap<String, String> allocatedMap = new LinkedHashMap<String, String>();
		allocatedMap.putAll(custReq);
		Map<String, String> copyCustReq = new HashMap<String, String>();
		copyCustReq.putAll(custReq);

		Iterator entries = custReq.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) entries.next();
			if (entry.getKey() != null && entry.getValue() != null) {
				Integer seatRequested = Integer.parseInt(entry.getValue().toString());
				boolean seatAllocated = false;
				for (int row = 0; row < availableSeats.length; row++) {
					for (int col = 0; col < availableSeats[row].length && availableSeats[row][col] != null; col++) {
						if (Integer.parseInt(availableSeats[row][col]) > seatRequested) {
							int remainingVal = Integer.parseInt(availableSeats[row][col]) - seatRequested;
							boolean isInMap = custReq.containsValue(String.valueOf(remainingVal));
							boolean isInCopyMap = copyCustReq.containsValue(String.valueOf(remainingVal));
							if (isInMap && isInCopyMap) {
								String key = getKeyByValue(copyCustReq, String.valueOf(remainingVal));
								copyCustReq.remove(key);
								allocatedMap.put(entry.getKey(), "Row " + (row + 1) + " Section" + (col + 1));
								seatAllocated = true;
								Integer newVal = Integer.parseInt(availableSeats[row][col]) - seatRequested;
								availableSeats[row][col] = newVal.toString();
								break;
							}
						} else if (seatRequested == Integer.parseInt(availableSeats[row][col])) {
							allocatedMap.put(entry.getKey(), "Row " + (row + 1) + " Section" + (col + 1));
							seatAllocated = true;
							Integer newVal = Integer.parseInt(availableSeats[row][col]) - seatRequested;
							availableSeats[row][col] = newVal.toString();
							break;
						} else if (seatRequested > getUpdatedQuantity(availableSeats)) {
							allocatedMap.put(entry.getKey(), "Sorry, we can't handle your party.");
						} else if (getLargest(availableSeats) < seatRequested) {
							allocatedMap.put(entry.getKey(), "Call to split party.");
						}
					}
					if (seatAllocated) {
						break;
					}
				}

			}
		}

		for (Map.Entry<String, String> entry : allocatedMap.entrySet()) {
			//Printing the O/P both in log console
			logger.info(entry.getKey() + " " + entry.getValue());
			//If it is to test in system console the below will do
			System.out.println(entry.getKey() + " " + entry.getValue());
		}

	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (Objects.equals(value, entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/*
	 * This method will return the sum of the numbers in the array
	 * Input : Array
	 */
	private static int getUpdatedQuantity(String[][] availableSeats) {
		int sum = 0;
		for (int i = 0; i < availableSeats.length; i++) {
			for (int j = 0; j < availableSeats[i].length; j++) {
				if (availableSeats[i][j] != null) {
					sum = sum + Integer.parseInt(availableSeats[i][j]);
				}
			}
		}
		return sum;
	}

	private ClassLoader getClassLoader() {
		return getClass().getClassLoader();
	}

	/*
	 * This method will return the largest number in the 
	 * array
	 * Input : Array
	 */
	private static int getLargest(String[][] availableSeats) {
		int maxValue = 0;
		for (int i = 0; i < availableSeats.length; i++) {
			for (int j = 0; j < availableSeats[i].length; j++) {
				if (availableSeats[i][j] != null) {
					if (Integer.parseInt(availableSeats[i][j]) > maxValue) {
						maxValue = Integer.parseInt(availableSeats[i][j]);
					}
				}
			}
		}
		return maxValue;
	}

}
