package temp;

import java.util.concurrent.TimeUnit;

public class NumberBase {
    
  public static void main(String [] args) {
      final int minutesAgo = 60;
System.out.println(""+minutesAgo+" minutes ago as timestamp: "+(System.currentTimeMillis()-TimeUnit.MINUTES.toMillis(minutesAgo)));      
System.out.println(NumberBase.convert(Integer.MAX_VALUE, 8));
System.out.println(NumberBase.convert(Integer.MAX_VALUE, 2));
  }
    
  public static StringBuilder convert(int number, int base) {
    return convert(number, base, new StringBuilder());
  }
  
  public static StringBuilder convert(int number, int base, StringBuilder appendTo) {
System.out.println("Number: "+number+", base: "+base);

    if (base < 2) {
      throw new UnsupportedOperationException("Base must be > 1");
    }
    
    if (base > 10) {
      throw new UnsupportedOperationException("Base must be <= 10");
    }
    
    if (number < base) {
        
      appendTo.setLength(0);
      
      return appendTo.append(number);
    }
    
    return convert(number / base, base, appendTo).append(number % base);
  }
}
