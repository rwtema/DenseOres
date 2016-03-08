package com.rwtema.denseores.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogHelper {
    public static Logger logger = LogManager.getLogger("denseores");

    public static boolean isDeObf = false;

    static {
        try {
            World.class.getMethod("getBlockState", BlockPos.class);
            isDeObf = true;
        } catch (Throwable ex) {
            isDeObf = false;
        }
    }

    public static void debug(Object info, Object... info2) {
        if (isDeObf) {
            String temp = "Debug: " + info;
            for (Object t : info2)
                temp = temp + " " + t;

            logger.info(info);
        }
    }


    public static void info(Object info, Object... info2) {
        String temp = "" + info;
        for (Object t : info2)
            temp = temp + " " + t;

        logger.info(info);
    }

    public static void error(String message, Throwable t) {
        logger.error(message, t);
    }

    public static void error(String s) {
        logger.error(s);
    }

    public static void describe(Object o) {
        if (o == null) info(null);
        else info(o.getClass().getSimpleName() + " " + o.toString());
    }
}
