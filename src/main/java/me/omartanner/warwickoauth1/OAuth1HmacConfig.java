package me.omartanner.warwickoauth1;

import oauth1.OAuth1WithCallback;
import oauth1.strategy.preset.HashMapTokenMapStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
    Configuration Bean for an OAuth1WithCallback instance, from the oauth1-hmac library.
    More information on oauth1-hmac: https://github.com/omarathon/oauth1-hmac
    Usage:
        - Fill in the "FILL_THIS_IN" fields, and change the SCOPES as you wish.
 */
@Configuration
public class OAuth1HmacConfig {
    // Fill these in.
    private static final String CONSUMER_KEY = "FILL_THIS_IN"; // provided by Warwick (https://warwick.ac.uk/services/its/servicessupport/web/sign-on/help/oauth/apis/registration/)
    private static final String CONSUMER_SECRET = "FILL_THIS_IN"; // provided by Warwick (https://warwick.ac.uk/services/its/servicessupport/web/sign-on/help/oauth/apis/registration/)
    private static final String CALLBACK_URL = "FILL_THIS_IN"; // e.g. the URL to your login page

    // Change the Scopes if you wish. These are the Scopes required for this example to work.
    private static final WarwickOAuthScope[] SCOPES = {WarwickOAuthScope.WEB_SIGN_ON, WarwickOAuthScope.TABULA};

    // These never change.
    private static final String ACCESS_TOKEN_URL = "https://websignon.warwick.ac.uk/oauth/accessToken";
    private static final String AUTHORISE_URL = "https://websignon.warwick.ac.uk/oauth/authorise?";
    private static final String REQUEST_TOKEN_URL = "https://websignon.warwick.ac.uk/oauth/requestToken?";

    // Construct an OAuth1WithCallback Configuration Bean, which shall be injected elsewhere.
    @Bean
    public OAuth1WithCallback getOAuth1WithCallback() {
        /*
         Construct and return the OAuth1WithCallback instance.
         For simplicity, we use a HashMapTokenStrategy as a strategy to store and fetch temporary tokens and their corresponding secrets.
         In production, it's recommended you implement your own TokenMapStrategy strategy, using a database.
        */
        return new OAuth1WithCallback(
                CONSUMER_KEY,
                CONSUMER_SECRET,
                REQUEST_TOKEN_URL + WarwickOAuthScope.getUrlScopesParameterValue(SCOPES),
                CALLBACK_URL,
                AUTHORISE_URL,
                ACCESS_TOKEN_URL,
                new HashMapTokenMapStrategy()
        );
    }
}
