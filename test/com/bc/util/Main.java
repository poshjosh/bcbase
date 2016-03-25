package com.bc.util;


/**
 * @(#)Main.java   17-Jun-2015 21:59:40
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
public class Main {

    public static void main(String [] args) {
        try{
if(true) {
    System.out.println(0.05/0.3);
    System.out.println(0.2/0.3);
    System.out.println(Math.random());
    System.out.println(Math.random());
    System.out.println(Math.random());
    return;
}            
            // This is the security tool of authsvc
            SecurityTool sy = new SecurityTool("AES", "AcIcvwW2MU4sJkvBx103m6gKsePm");
System.out.println("Password: "+sy.decrypt("347d06007bce16973ec5f889790aebb1"));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
