package controllers;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class AccountTest extends WithApplication {

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }

    @Test
    public void account_should_respond_http_200_for_health_check() {
        // GIVEN
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/account/sum");
        // WHEN
        Result result = route(app, request);
        // THEN
        Assertions.assertThat(result.status())
                .isEqualTo(OK);
    }

}
