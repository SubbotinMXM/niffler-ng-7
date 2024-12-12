package guru.qa.niffler.api;

import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SpendApi {

    @POST("/internal/v2/spends")
    Call<SpendJson> addSpend(@Body SpendJson spend);
}
