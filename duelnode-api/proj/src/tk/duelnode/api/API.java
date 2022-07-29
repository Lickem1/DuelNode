package tk.duelnode.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

public class API {

    @Getter
    private static final Gson gson = new GsonBuilder().create();
}
