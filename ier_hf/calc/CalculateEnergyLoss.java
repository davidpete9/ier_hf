package calc;

public class CalculateEnergyLoss {

  public static final int DEFAULT_ENERGY = 1;
  public static final int DEFAULT_CHARGE_SPEED = 1;
  public static final int DEFAULT_MULTIPLIER = 10;
  
  public static int calculateEnergyPerTime(int capacity, int weight) {
  	return (int)(((double)weight/capacity)*DEFAULT_MULTIPLIER+DEFAULT_ENERGY);
  }
  
  public static int getChargeTime(int requiredCapacity) {
  	return requiredCapacity*DEFAULT_CHARGE_SPEED;
  }
  
  public CalculateEnergyLoss() {}

}
