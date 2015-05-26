package com.likanhp.smartassistant.util;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Likan on 5/25/2015.
 */
public class LoggerUtils {
    public static final Marker FATAL = MarkerFactory.getMarker("FATAL");

    public static void fatalAndExit(Logger logger, String format, Object... objects) {
        if (objects.length == 0 || !(objects[objects.length - 1] instanceof Throwable)) {
            List<Object> args = new ArrayList<>(Arrays.asList(objects));
            args.add(new Exception(""));
            objects = args.toArray();
        }
        logger.error(LoggerUtils.FATAL, format, objects);
        System.exit(1);
    }
}
