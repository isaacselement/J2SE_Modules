package j2se.modules.Helper;

import java.util.Random;

public class RandomHelper {

	/**
	 * 
	 * @param min >= 0 (inclusive)
	 * @param max > 0 (exclusive)
	 * @return
	 */
	public static int getIntRandomBetween(int min, int max) {
		 Random random = new Random();
		 int s = random.nextInt(max)%(max-min) + min;
		 return s;
	}
}
