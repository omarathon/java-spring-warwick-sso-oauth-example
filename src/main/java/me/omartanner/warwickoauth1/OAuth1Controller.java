package me.omartanner.warwickoauth1;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.HttpResponse;
import oauth1.OAuth1WithCallback;
import oauth1.exception.GetAccessTokenException;
import oauth1.exception.GetTemporaryTokenException;
import oauth1.exception.OAuthBackedRequestException;
import oauth1.exception.TokenMapException;
import oauth1.lib.AuthorisedResult;
import oauth1.lib.BeginResult;
import oauth1.lib.RequestMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class OAuth1Controller {
    // URL for the Attributes request (https://warwick.ac.uk/services/its/servicessupport/web/sign-on/development/reference/attributes/)
    private static final String ATTRIBUTES_URL = "https://websignon.warwick.ac.uk/oauth/authenticate/attributes";

    // URL for the Member request (https://warwick.ac.uk/services/its/servicessupport/web/tabula/api/member/retrieve-member)
    private static final String MEMBER_URL = "https://tabula.warwick.ac.uk/api/v1/member/";

    // Inject the OAuth1WithCallback instance, constructed via the Configuration Bean.
    @Autowired
    private OAuth1WithCallback oAuth1WithCallback;

    /*
     REST Endpoint to obtain the redirect URL.

     On the front-end, the user is to be redirected to the returned URL.

     After the user grants access, they shall be redirected to the specified callback URL,
     with their temporary token and verifier as a parameter to the URL, which are to be captured on the front-end.
     Then, a request to authorised is to be made with their temporary token and verifier.
    */
    @GetMapping("/oauth/begin")
    public String endpointBegin() {
        try {
            BeginResult beginResult = oAuth1WithCallback.begin();
            return beginResult.getRedirectUrl();
        }
        catch (GetTemporaryTokenException | TokenMapException e) {
            // Handle the exceptions.
            e.printStackTrace();
            return null;
        }
    }

    /*
     REST Endpoint to obtain the access token for a user, and make requests on their behalf, ultimately to return their date of birth (toy example).

     On the front-end, make a request to this endpoint after having made the request to begin, and the user has landed on the callback URL.

     After obtaining an access token for the user, we:
     - do an Attributes request on behalf of the user to obtain their University ID
     - do a Member request on behalf of the user to obtain their Member object from the Tabula API
     - return the user's Member object
    */
    @GetMapping("/oauth/authorised")
    public String endpointAuthorised(
            @RequestParam(name="oAuthToken") @Nonnull String oAuthToken,
            @RequestParam(name="verifier") @Nonnull String verifier
    )
    {
        AuthorisedResult authorisedResult;
        try {
            authorisedResult = oAuth1WithCallback.authorised(oAuthToken, verifier);
        }
        catch (GetAccessTokenException | TokenMapException e) {
            // Handle the exceptions.
            e.printStackTrace();
            return null;
        }

        // Store the user's OAuthParameters to make requests on their behalf.
        OAuthParameters oAuthParameters = authorisedResult.getOAuthParameters();

        /*
         Successfully got an access token for the user.

         Now, do an attributes request on behalf the user to obtain their university ID,
         then do a Member request on behalf of the user to obtain their Member from the Tabula API,
         then return their Member object.
        */

        // Make Attributes request (is a POST request with an empty request body).
        String attributes;
        try {
            HttpResponse response = OAuth1WithCallback.makeOAuthBackedRequest(ATTRIBUTES_URL, oAuthParameters, RequestMethod.POST, null);
            attributes = response.parseAsString();
        }
        catch (OAuthBackedRequestException | IOException e) {
            // Handle the exceptions.
            e.printStackTrace();
            return null;
        }

        // Extract their university ID from the attributes response.
        Pattern pattern = Pattern.compile("id=([0-9]*)");
        Matcher matcher = pattern.matcher(attributes);
        String uniId;
        if (matcher.find()) {
            uniId = matcher.group(1);
        }
        else {
            // No University ID match.
            return null;
        }

        // Make Member request (is a GET request with an empty request body)
        String memberUrl = MEMBER_URL + "/" + uniId;
        String member;
        try {
            HttpResponse response = OAuth1WithCallback.makeOAuthBackedRequest(memberUrl, oAuthParameters, RequestMethod.GET, null);
            member = response.parseAsString();
        }
        catch (OAuthBackedRequestException | IOException e){
            // Handle the exceptions.
            e.printStackTrace();
            return null;
        }

        // Return their Member object from the Member response.
        return member;
    }
}
