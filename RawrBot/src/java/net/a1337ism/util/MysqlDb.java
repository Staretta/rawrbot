package net.a1337ism.util;

import net.a1337ism.RawrBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MysqlDb extends Db {
    // Set up the logger stuff
    private static Logger logger = LoggerFactory.getLogger(RawrBot.class);

    public MysqlDb(String sDriverKey, String sUrlKey) {
        try {
            init(sDriverKey, sUrlKey);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }
        if (conn != null) {
            logger.debug("Connected OK using " + sDriverKey + " to " + sUrlKey);
        } else {
            logger.debug("Connection failed");
        }
    }
}