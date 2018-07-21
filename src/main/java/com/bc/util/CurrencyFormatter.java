package com.bc.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;


/**
 * @(#)CurrencyFormatter.java   20-Aug-2015 19:27:30
 *
 * Copyright 2011 NUROX Ltd, Inc. All rights reserved.
 * NUROX Ltd PROPRIETARY/CONFIDENTIAL. Use is subject to license 
 * terms found at http://www.looseboxes.com/legal/licenses/software.html
 */

/**
 * @author   chinomso bassey ikwuagwu
 * @version  2.0
 * @since    2.0
 */
public abstract class CurrencyFormatter implements Serializable {
    
    private MathContext mathContext;
    
    public CurrencyFormatter() {  }
    
    public MathContext getMatchContext() {
        if(mathContext == null) {
            mathContext = new MathContext(14, RoundingMode.HALF_UP);
        }
        return mathContext;
    }
    
    public abstract float getRate(Locale from, Locale to);
    
    /**
     * Converts the input parameter <tt>(using the rate of the currency of #locale)</tt>
     * to return a String Object of price format eg <tt>$10.00</tt> 
     * @param price An Object which is a String Object of price format 
     * eg <tt>$10.00</tt>
     * @param from
     * @param to
     * @return A String Object of price format eg <tt>$10.00</tt>
     */
    public String convertCurrency(Object price, Locale from, Locale to) {
//Logger.getLogger(this.getClass().getName()).fine("BEFORE price: " + price);
        if (price == null) return null;
        Number number = this.priceToNumber(price, from, to);
        String output = this.numberToPrice(number, to);
//Logger.getLogger(this.getClass().getName()).fine("AFTER converted price: " + output);
        return output;
    }
    
    /**
     * Converts the input parameter <tt>(using the rate of the currency of #locale)</tt>
     * to return a String Object of price format eg <tt>$10.00</tt> 
     * @param numberObject An Object which is a String Object of price format 
     * eg <tt>$10.00</tt>
     * @param from
     * @param to
     * @return A Number format eg <tt>10.00</tt>
     */
    public Number convertNumber(Object numberObject, Locale from, Locale to) {

//Logger.getLogger(this.getClass().getName()).fine("BEFORE. Number: " + numberObject);

        if (numberObject == null) {
            return null;
        }
        
        if(from == null) {
            throw new NullPointerException();
        }
        if(to == null) {
            throw new NullPointerException();
        }

        float rate;
        
        if(from == to) {
            
            rate = 1.0f;
            
        }else {
        
            rate = this.getRate(from, to);
            
            if(rate == -1.0f) {
                throw new UnsupportedOperationException();
            }
        }

        Number number = this.getNumber(numberObject, from);
        
        if(rate == 1.0f) {
            return number;
        }

        double n = number.doubleValue();
        
        BigDecimal output = this.multiply(n, rate);

        return output;
    }
    
    /**
     * Uses the default math context
     */
    private BigDecimal multiply(double a, double b) {
        
        BigDecimal output = BigDecimal.valueOf(a).multiply(BigDecimal.valueOf(b), getMatchContext());
//Logger.getLogger(Util.class.getName()).fine(a + " multiply " + b + " = " + output);
        return output;
    }
    
    public String numberToPrice(Object number, Locale oldLocale, Locale newLocale) {
//Logger.getLogger(this.getClass().getName()).fine("BEFORE number: " + number);
        if(number == null) return null;
        // Convert the number to the new rate
        //
        number = this.convertNumber(number, oldLocale, newLocale);
        
        String price = this.numberToPrice(number, newLocale);
        
//Logger.getLogger(this.getClass().getName()).fine("AFTER converted price: " + price);
        return price;
    }
    
    /**
     * Formats the object argument to return a String Object  
     * @param numberObject An Object which is a Number or a String of number format 
     * eg <tt>"10.00000"</tt>
     * @param locale
     * @return A String Object of price format eg <tt>$10.00</tt>
     */
    public String numberToPrice(Object numberObject, Locale locale) {
//Logger.getLogger(this.getClass().getName()).fine("BEFORE number: " + number);
        if(numberObject == null) return null;

        Number number = this.getNumber(numberObject, locale);
        
        Object price = NumberFormat.getCurrencyInstance(locale).format(number);
        
//Logger.getLogger(this.getClass().getName()).fine("AFTER price: " + number);
        return price instanceof BigDecimal ? 
                ((BigDecimal)numberObject).toPlainString() : price.toString();
    }

    public Number priceToNumber(Object price, Locale oldLocale, Locale newLocale) {
//Logger.getLogger(this.getClass().getName()).fine("BEFORE price: " + price);
        if(price == null) return null;

        Number number = this.priceToNumber(price, oldLocale);

        Number fmtNumber = this.convertNumber(number, oldLocale, newLocale);

//Logger.getLogger(this.getClass().getName()).fine("AFTER converted number: " + fmtNumber);
        return fmtNumber;
    }
    
    /**
     * Formats the object argument to return a Number  
     * @param price An Object of format "$60.00"
     * @param locale
     * @return A Number object
     */
    public Number priceToNumber(Object price, Locale locale) {
//Logger.getLogger(this.getClass().getName()).fine("BEFORE price: " + price);
        if(price == null) return null;
        // If NumberFormat.getCurrencyInstance is not called with any locale
        // as argument then the call to NumberFormat.parse must have an input
        // with default currency symbol, otherwise the currency symbol must
        // be that of the locale.
        //
        Number output;
        try {
            
            output = NumberFormat.getCurrencyInstance(locale).parse(price.toString());
            
        }catch(ParseException e) {
            throw new IllegalArgumentException(e);
        }    
//Logger.getLogger(this.getClass().getName()).fine("AFTER number: " + price);
        return output;
    }

    private Number getNumber(Object oval, Locale locale) {
        Number number;
        if(oval instanceof Number) {
            number = ((Number)oval);
        }else{
            try {
                number = NumberFormat.getNumberInstance(locale).parse(oval.toString());
            }catch(ParseException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return number;
    }
}