package org.uoc.pfc.eventual.config;

public class AppConst {

    public interface Security {
	public static final String CSRF_TOKEN	   = "X-CSRF-Token";
	public static final int    CSRF_TOKEN_DURATION  = 1800000;
	public static final int    PURGE_TOKEN_INTERVAL = 1800000;
    }

}
