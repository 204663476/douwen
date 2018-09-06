package com.huagongzi.douwen.untils;

import java.util.Random;

/**
 * Created by 许鑫源 on 2018/8/1.
 */

public class random {
    static String type = "wxs";

    public static String getType() {
        for (int i = 0; i < 5; i++) {
            Random rand = new Random();
            int type_rand = rand.nextInt(1) + 2;

            switch (type_rand) {
                case 1:
                    type = "wxs";
                    break;
                case 2:
                    type = "waq";
                    break;
                case 3:
                    type = "gy";
                    break;
            }
        }
        return type;
    }
}