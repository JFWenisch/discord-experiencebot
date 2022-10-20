package tech.wenisch.discord.experiencebot;

import io.sentry.Sentry;

public class SentryManager {
    private static final SentryManager instance = new SentryManager();
    
    private String SentryDSN;
    private boolean isActivated=false;
    private SentryManager(){
    	SentryDSN=System.getenv("SENTRY_DSN");
    	if(SentryDSN!=null)
    	{
    		if(SentryDSN.length()>0)
    		{
    			Sentry.init(options -> {
    	    		  options.setDsn(SentryDSN);
    	    		  // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
    	    		  // We recommend adjusting this value in production.
    	    		  options.setTracesSampleRate(1.0);
    	    		  // When first trying Sentry it's good to see what the SDK is doing:
    	    		 // options.setDebug(true);
    	    		  options.setRelease("discord-experience@"+Bot.class.getPackage().getImplementationVersion());
    	    		});
    			isActivated=true;
    		}
    	}
    	
    }

    public static SentryManager getInstance(){
        return instance;
    }
    public boolean isActivated()
    {
    	return isActivated;
    }
    public void handleError(Throwable e)
    {
    	if(isActivated)
    	 {
    		Sentry.captureException(e);
    	 }
    	else
    	{
    		e.printStackTrace();
    	}
    }
}
