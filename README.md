# Spring Warwick SSO Example

Simple Spring application which uses [oauth1-hmac](https://github.com/omarathon/oauth1-hmac) to access University of Warwick data via OAuth1.0 as a consumer, for [Warwick's OAuth1.0 provider](https://warwick.ac.uk/services/its/servicessupport/web/sign-on/help/oauth/apis).

Demonstrates how to log users in, retrieve basic attributes and fetch information from a Warwick application (in this case, from [Tabula](https://github.com/UniversityofWarwick/tabula)).

This application assumes you're using the [**HMAC**](https://en.wikipedia.org/wiki/HMAC) signing strategy for the OAuth1.0 implementation. If you're using [RSA-SHA1](https://www.w3.org/PICS/DSig/RSA-SHA1_1_0.html), this application will **not** work.

## Usage

Firstly, configure [OAuth1HmacConfig](src/main/java/me/omartanner/warwickoauth1/OAuth1HmacConfig.java) by filling in the fields marked as `FILL_THIS_IN`, and change the `SCOPES` if you wish. If you are testing locally, feel free to use any reasonable callback URL (e.g. `https://google.com`).  

Now, run the application. By default, it's served from `http://localhost:8080`, and can be used like so:

1. Make a request to `/oauth/begin` to obtain a redirect URL.
1. Redirect the user to the URL obtained from **1** (or if testing, visit it yourself).
1. Once the user (or yourself, if testing) has granted access and landed on the callback URL, extract the `oauth_token` and `oauth_verifier` parameters from the URL.
1. Make a request to `/oauth/authorised` with the `oauth_token` and `oauth_verifier` from **3**, to obtain the [Member object](https://warwick.ac.uk/services/its/servicessupport/web/tabula/api/member/member-object) of the user who granted access after **2**.

## Classes

The important classes can be found in [src/main/java/me/omartanner/warwickoauth1](src/main/java/me/omartanner/warwickoauth1).   
Classes marked in **bold** are for public viewing, the rest are auxiliary:

* [**OAuth1HmacConfig**](src/main/java/me/omartanner/warwickoauth1/OAuth1HmacConfig.java) - Configuration Bean for an OAuth1WithCallback instance, from the [oauth1-hmac](https://github.com/omarathon/oauth1-hmac) library. This class creates an instance of [OAuth1WithCallback](https://omarathon.github.io/oauth1-hmac/oauth1/OAuth1WithCallback.html), which provides the OAuth1.0 functionality, and is injected elsewhere.
* [**OAuth1Controller**](src/main/java/me/omartanner/warwickoauth1/OAuth1Controller.java) - REST API controller for the `/oauth/begin` and `/oauth/authorised` endpoints. This class does most of the work. It provides the following endpoints:
    1. `/oauth/begin` - Obtain the redirect URL.
    1. `/oauth/authorised` - Obtain the access token for a user. Then, makes an [Attributes request](https://warwick.ac.uk/services/its/servicessupport/web/sign-on/development/reference/attributes/) on their behalf to obtain their University ID, and then makes a [Member request](https://warwick.ac.uk/services/its/servicessupport/web/tabula/api/member/retrieve-member) on their behalf to obtain their [Member object](https://warwick.ac.uk/services/its/servicessupport/web/tabula/api/member/member-object) from [Tabula](https://github.com/UniversityofWarwick/tabula). Finally, it returns their [Member object](https://warwick.ac.uk/services/its/servicessupport/web/tabula/api/member/member-object).
* [WarwickOAuthScope](src/main/java/me/omartanner/warwickoauth1/WarwickOAuthScope.java) - An enum mapping [Warwick's OAuth Scopes](https://warwick.ac.uk/services/its/servicessupport/web/sign-on/help/oauth/apis/#scopes) to their respective strings in the request token URL.
* [Application](src/main/java/me/omartanner/warwickoauth1/Application.java) - Spring Main class.

## Dependencies

The dependencies may be found in [pom.xml](pom.xml).

* [spring-boot-starter-web](https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-web) - Spring Web, essential for a Spring REST API application.
* [oauth1-hmac](https://github.com/omarathon/oauth1-hmac) - Provides the underlying OAuth1.0 functionality.
