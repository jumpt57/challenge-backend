package services;

import models.Account;
import models.AuthenticateResponse;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithApplication;

import java.util.List;
import java.util.Optional;

public class BridgeClientTest extends WithApplication {

    private BridgeClient bridgeClient;

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Before
    public void setUp() {
        bridgeClient = provideApplication().injector().instanceOf(BridgeClient.class);
    }

    @Test
    public void should_load_all_properties() {
        // GIVEN
        // WHEN
        // THEN
        Assertions.assertThat(bridgeClient)
                .extracting("baseUrl", "apiVersion", "apiClientId", "apiClientSecret")
                .isNotNull();
    }

    @Test
    public void should_authenticate_user() {
        // GIVEN
        // WHEN
        Optional<AuthenticateResponse> authenticateResponse = bridgeClient.authenticateUser(BridgeClient.USER_EMAIL,
                BridgeClient.USER_PASSWORD);
        // THEN
        Assertions.assertThat(authenticateResponse)
                .isNotEmpty();
    }

    @Test
    public void should_not_authenticate_user() {
        // GIVEN
        // WHEN
        Optional<AuthenticateResponse> authenticateResponse = bridgeClient.authenticateUser("not_user", "not_a_password");
        // THEN
        Assertions.assertThat(authenticateResponse)
                .isEmpty();
    }

    @Test
    public void should_list_accounts() {
        // GIVEN
        // WHEN
        List<Account> accounts = bridgeClient.listAccounts();
        // THEN
        Assertions.assertThat(accounts)
                .hasSize(4);
    }

    @Test
    public void should_should_sum_balance_of_accounts() {
        // GIVEN
        // WHEN
        Double sum = bridgeClient.sumOfAccounts();
        // THEN
        Assertions.assertThat(sum)
                .isEqualTo(156900);
    }

}
