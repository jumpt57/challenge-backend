package services;

import com.google.common.annotations.VisibleForTesting;
import com.typesafe.config.Config;
import models.Account;
import models.AuthenticateResponse;
import models.GetAccountsResponse;
import play.libs.Json;
import play.libs.ws.WSClient;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static play.mvc.Http.Status.OK;

/**
 * Bridge is Bankin's SaaS API. This service is where the calls to the API should be implemented.
 * <p>
 * The "doSomething" method doesn't actually do anything yet and needs to be modified to fit the exercice's needs.
 */
public class BridgeClient {

    // these are hardcoded for simplicity's sake
    static final String USER_EMAIL = "user1@mail.com";
    static final String USER_PASSWORD = "a!Strongp#assword1";

    private static final String BANKIN_VERSION = "Bankin-Version";
    private static final String AUTHORIZATION = "Authorization";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String BEARER = "Bearer ";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private final WSClient wsClient;
    private final String baseUrl;
    private final String apiVersion;
    private final String apiClientId;
    private final String apiClientSecret;

    @Inject
    public BridgeClient(WSClient wsClient, Config config) {
        this.wsClient = wsClient;
        this.baseUrl = config.getString("bankin.api.baseUrl");
        this.apiVersion = config.getString("bankin.api.version");
        this.apiClientId = config.getString("bankin.api.app.clientId");
        this.apiClientSecret = config.getString("bankin.api.app.clientSecret");
    }

    public double sumOfAccounts() {

        double sum = this.listAccounts().stream()
                .map(account -> account.balance)
                .mapToDouble(Double::doubleValue)
                .sum();

        BigDecimal bd = BigDecimal.valueOf(sum);

        return bd.setScale(-2, RoundingMode.UP).doubleValue();
    }

    @VisibleForTesting
    List<Account> listAccounts() {

        AuthenticateResponse accessToken = authenticateUser(USER_EMAIL, USER_PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("Not Authenticated !"));

        GetAccountsResponse accountsResponse = wsClient.url(baseUrl + "/accounts")
                .addHeader(BANKIN_VERSION, apiVersion)
                .addHeader(AUTHORIZATION, BEARER + accessToken.accessToken)
                .addQueryParameter(CLIENT_ID, apiClientId)
                .addQueryParameter(CLIENT_SECRET, apiClientSecret)
                .get()
                .thenApply(response -> Json.fromJson(response.asJson(), GetAccountsResponse.class))
                .toCompletableFuture()
                .join();

        return Optional.ofNullable(accountsResponse.accounts)
                .orElse(Collections.emptyList());
    }

    /**
     * This method is "complete" and doesn't need editing, except if you feel some things could be improved (there
     * is no trap)
     */
    @VisibleForTesting
    Optional<AuthenticateResponse> authenticateUser(String email, String password) {

        return wsClient.url(baseUrl + "/authenticate")
                .addHeader(BANKIN_VERSION, apiVersion)
                .addQueryParameter(CLIENT_ID, apiClientId)
                .addQueryParameter(CLIENT_SECRET, apiClientSecret)
                .addQueryParameter(EMAIL, email)
                .addQueryParameter(PASSWORD, password)
                .post("")
                .thenApply(response -> {
                    if (response.getStatus() == OK) {
                        return Optional.of(Json.fromJson(response.asJson(), AuthenticateResponse.class));
                    }
                    return Optional.<AuthenticateResponse>empty();
                })
                .toCompletableFuture()
                .join();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getApiClientId() {
        return apiClientId;
    }

    public String getApiClientSecret() {
        return apiClientSecret;
    }
}
