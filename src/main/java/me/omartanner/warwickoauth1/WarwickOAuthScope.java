package me.omartanner.warwickoauth1;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
    An enum mapping Warwick's OAuth Scopes to their respective strings in the request token URL.
    More information on Warwick's OAuth Scopes: https://warwick.ac.uk/services/its/servicessupport/web/sign-on/help/oauth/apis/#scopes
 */
public enum WarwickOAuthScope {
    WEB_SIGN_ON {
        public String getUrlString() {
            return "urn:websignon.warwick.ac.uk:sso:service";
        }
    },

    TABULA {
        public String getUrlString() {
            return "urn:tabula.warwick.ac.uk:tabula:service";
        }
    },

    SITEBUILDER_READING {
        public String getUrlString() {
            return "urn:www2.warwick.ac.uk:sitebuilder2:read:service";
        }
    },

    SITEBUILDER_EDITING {
        public String getUrlString() {
            return "urn:sitebuilder.warwick.ac.uk:sitebuilder2:edit:service";
        }
    },

    SEARCH {
        public String getUrlString() {
            return "urn:search.warwick.ac.uk:search:service";
        }
    },

    FILES {
        public String getUrlString() {
            return "urn:files.warwick.ac.uk:files:service";
        }
    },

    BLOGS {
        public String getUrlString() {
            return "urn:files.warwick.ac.uk:files:service";
        }
    },

    FORUMS {
        public String getUrlString() {
            return "urn:forums.warwick.ac.uk:forums:service";
        }
    },

    EXAM_TIMETABLING {
        public String getUrlString() {
            return "urn:examtimetable.warwick.ac.uk:examtimetable:service";
        }
    },

    PRINTER_CREDITS {
        public String getUrlString() {
            return "urn:printercredits.warwick.ac.uk:printcredit:service";
        }
    };

    // Obtain the representation of the Scope in the request token URL.
    public abstract String getUrlString();

    public static String getUrlScopesParameterValue(WarwickOAuthScope[] scopes) {
        if (scopes.length == 0) return "";

        // Join the url string for each scope with a "+" delimiter as instructed in the documentation for Scopes.
        return "scope=" + Arrays.stream(scopes).map(WarwickOAuthScope::getUrlString).collect(Collectors.joining("+"));
    }
}
