package com.example.agricircle.project.Controller;
import com.example.agricircle.project.Entities.User;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class GraphQLController implements Serializable {









/*
GraphQL = https://graphql.agricircle.com/graphql
Core = https://core.agricircle.com/graphiql
REST = https://core.agricircle.com/api/v1/
 */

    private String cookieAuth;

    public String login() throws UnirestException, JSONException {

        String email = "jacno@dtu.dk";
        String password = "FMIS-systemer1";

        System.out.println("Forsøger login.....");

        HttpResponse<String> response = Unirest.post("https://graphql.agricircle.com/graphql")
                .header("accept", "*/*")
                .header("origin", "https://react.agricircle.com")
                .header("content-type", "application/json")
                .body("[{\"operationName\":\"login\",\"variables\":{\"options\":{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}},\"query\":\"mutation login($options: LoginInput!) {\\n  login(options: $options) {\\n    token\\n    cookie\\n    __typename\\n  }\\n}\\n\"}]")
                .asString();



         //System.out.println(response.getCode());
        System.out.println("response.getBody() " + response.getBody());
        /*
response.getBody()
[
    {"data":
        {"login":
            {"token":"eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyNDQ1LCJ0b2tlbiI6bnVsbCwibG9jYWxlIjoiZW4iLCJleHAiOjE1NjU2Mzc2Njh9.2Wy2asf2r5LGvlSxWpnRUZGmx8BpXut_EFbOHhXE3nk",
            "cookie":[
               "token=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyNDQ1LCJ0b2tlbiI6bnVsbCwibG9jYWxlIjoiZW4iLCJleHAiOjE1NjU2Mzc2Njh9.2Wy2asf2r5LGvlSxWpnRUZGmx8BpXut_EFbOHhXE3nk;
               domain=.agricircle.com; path=/","signed_in=1; domain=agricircle.com; path=/",
               "_AgriCircle_subdomain_session=LzBYZTRmVlk2Q3A1dGxJc0dSL3hZaVozYVF0YXkxSStVbmJna3kxWk9Xb2ZXN3l5SjJOV0FDc2I2TkJ4T2hsWWNEN0xkaGNuS3l2dXI0cXhPV1ZLeFlMWGNtbWZFeWNHL1VEd21LT0h0dUMrUHFsenVXb1M0QVB3ZTl0WjlSVmNlM01WWlBiNVhnVGVzQ1h5L1A3c0xEdnBkRjMvWGFQL0plWFVGamsyd2xzPS0taCtVOE8reHNKUU5helJRY2pDTC95dz09--e9de7e38060332aa2bc282ff1f7bfbcdd2658a93; domain=.agricircle.com; path=/"],"__typename":"LoginResponse"}}}]
         */

        //System.out.println("body: " + response.getBody());
        String str = response.getBody().substring(1, response.getBody().length() - 1); // fjerner "[]" således at det ikke længere fremstår som en liste.

        JSONArray cookie = new JSONObject(str).getJSONObject("data").getJSONObject("login").getJSONArray("cookie");

        System.out.println("cookie = " + cookie.toString(2));


        for (int n = 0; n < cookie.length(); n++) {
            for (String value : cookie.getString(n).split(";")) {
                if (value.startsWith("_AgriCircle_subdomain_session")) {
                    cookieAuth = value;//.substring(0, 337)  + ";";
                }
            }

        }

        //String strSubdomain_session = cookie.getString(2).substring(0, 337)  + ";";
        System.out.println("cookie: " + cookieAuth);
        if (cookieAuth == null){

            return null;
        }

        return cookieAuth;
    }

    public User getUser() throws UnirestException, JSONException {
        JSONArray res = getGraphQl("Get field data",
                "[{\"operationName\":\"user\",\"variables\":{},\"query\":\"query user {\\n  user {\\n    language\\n    avatarUrl\\n    name\\n    slug\\n    __typename\\n  }\\n}\\n\"}]");

        JSONObject json = res.getJSONObject(0).getJSONObject("data").getJSONObject("user");
        return new Gson().fromJson(json.toString(), User.class);

    }

    /**
     * Henter et antal GraphQL objekter fra serveren
     *
     * @param beskrivelse Tekst
     * @param query
     * @return JSON-objekt med data
     */
    private JSONArray getGraphQl(String beskrivelse, String query) throws UnirestException, JSONException {

        HttpResponse<String> response = Unirest.post("https://graphql.agricircle.com/graphql")
                .header("x-cookie", cookieAuth)
                .header("content-type", "application/json")
                .body(query)
                .asString();

        System.out.println("\n\nAction: " + beskrivelse);
        //System.out.println(response.getHeaders());
        System.out.println("Response code: " + response.getCode() + " : " + response.getHeaders());
        System.out.println("-----------------------------------");
        //System.out.println(response.getBody());
        if (response.getCode() != 200) throw new IllegalArgumentException("Fik fejl i svar");

        JSONArray spmjsonarr = new JSONArray(query);
        JSONArray svarjsonarr = new JSONArray(response.getBody());
        //for (int i = 0; i < spmjsonarr.length(); i++) {
         //   JSONObject qjson = spmjsonarr.getJSONObject(i);
          //  String gqlq = qjson.getString("query");
           // System.out.println("Spørger efter:\n" + qjson.toString(2));
            //System.out.println(gqlq.replaceAll("\\n", "\n"));

            //System.out.println(svarjsonarr.getJSONObject(i).toString(4));
        //}
        return svarjsonarr;
    }


    /** Henter alle de mulige kald/skemaet. Bemærk at svaret er MEGET stort */
    public JSONArray hentGraphQLSkema() throws UnirestException, JSONException {
        return getGraphQl("Hent GraphQL skemaet",
                "[" +
                        "{\"operationName\":\"IntrospectionQuery\",\"variables\":{},\"query\":\"query IntrospectionQuery {\\n  __schema {\\n    queryType {\\n      name\\n    }\\n    mutationType {\\n      name\\n    }\\n    subscriptionType {\\n      name\\n    }\\n    types {\\n      ...FullType\\n    }\\n    directives {\\n      name\\n      description\\n      locations\\n      args {\\n        ...InputValue\\n      }\\n    }\\n  }\\n}\\n\\nfragment FullType on __Type {\\n  kind\\n  name\\n  description\\n  fields(includeDeprecated: false) {\\n    name\\n    description\\n    args {\\n      ...InputValue\\n    }\\n    type {\\n      ...TypeRef\\n    }\\n     }\\n  inputFields {\\n    ...InputValue\\n  }\\n  interfaces {\\n    ...TypeRef\\n  }\\n  enumValues(includeDeprecated: false) {\\n    name\\n    description\\n    }\\n  possibleTypes {\\n    ...TypeRef\\n  }\\n}\\n\\nfragment InputValue on __InputValue {\\n  name\\n  description\\n  type {\\n    ...TypeRef\\n  }\\n  }\\n\\nfragment TypeRef on __Type {\\n  kind\\n  name\\n}\\n\"}" +
                        "]");
    }


    public JSONArray getFieldData() throws UnirestException, JSONException {
        return getGraphQl("Get field data",
                "[" +
                        "{\"operationName\": \"layers\", \"variables\":{\"options\":{\"companyId\":2434,\"types\":[\"application_map\"]}},\"query\":\"query layers($options: LayersInput!) {\\n  layers(options: $options) {\\n    id\\n    secret_url\\n    data\\n    display_name\\n    bbox\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"layers\", \"variables\":{\"options\":{\"companyId\":2434,\"types\":[\"soil_zone_map\"]}},  \"query\":\"query layers($options: LayersInput!) {\\n  layers(options: $options) {\\n    id\\n    secret_url\\n    data\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"crops\",  \"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query crops($options: CropsInput!) {\\n  crops(options: $options) {\\n    crop_id\\n    field_ids\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"fields\", \"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query fields($options: LayersInput!) {\\n  fields(options: $options) {\\n    id\\n    active_crop_name\\n    active_crop_image_url\\n    center_point\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"fields\", \"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query fields($options: LayersInput!) {\\n  fields(options: $options) {\\n    id\\n    shape\\n    surface\\n    display_name\\n    soil_data\\n    fertilizer_enabled\\n    center_point\\n    name\\n    rotations\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"drainages\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query drainages($options: LayersInput!) {\\n  drainages(options: $options) {\\n    id\\n    shape\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"problemAreas\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query problemAreas($options: LayersInput!) {\\n  problemAreas(options: $options) {\\n    id\\n    shape\\n    registered_at\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"applicationMaps\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query applicationMaps($options: LayersInput!) {\\n  applicationMaps(options: $options) {\\n    id\\n    shape\\n    activity_types\\n    geo_applications\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"restrictedZones\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query restrictedZones($options: LayersInput!) {\\n  restrictedZones(options: $options) {\\n    id\\n    shape\\n    __typename\\n  }\\n}\\n\"}" +
                        "]");
    }

    public JSONArray getAllFieldData() throws UnirestException, JSONException {
        return getGraphQl("Get all Field data",
                "[" +
                        //"{\"operationName\":\"user\",\"variables\":{},\"query\":\"query user {\\n  user {\\n    language\\n    avatarUrl\\n    name\\n    slug\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"crops\",\"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query crops($options: CropsInput!) {\\n  crops(options: $options) {\\n    crop_id\\n    field_ids\\n    name\\n    photo_url\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"crops\",\"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query crops($options: CropsInput!) {\\n  crops(options: $options) {\\n    crop_id\\n    field_ids\\n    __typename\\n  }\\n}\\n\"}" +
                        "{\"operationName\":\"fields\",\"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query fields($options: LayersInput!) {\\n  fields(options: $options) {\\n    id\\n    name\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"fields\",\"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query fields($options: LayersInput!) {\\n  fields(options: $options) {\\n    id\\n    shape\\n    surface\\n    display_name\\n    soil_data\\n    fertilizer_enabled\\n    center_point\\n    name\\n    rotations\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"fields\",\"variables\":{\"options\":{\"companyId\":2434,\"year\":2019}},\"query\":\"query fields($options: LayersInput!) {\\n  fields(options: $options) {\\n    id\\n    active_crop_name\\n    active_crop_image_url\\n    center_point\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"drainages\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query drainages($options: LayersInput!) {\\n  drainages(options: $options) {\\n    id\\n    shape\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"problemAreas\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query problemAreas($options: LayersInput!) {\\n  problemAreas(options: $options) {\\n    id\\n    shape\\n    registered_at\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"applicationMaps\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query applicationMaps($options: LayersInput!) {\\n  applicationMaps(options: $options) {\\n    id\\n    shape\\n    activity_types\\n    geo_applications\\n    __typename\\n  }\\n}\\n\"}" +
                        //",{\"operationName\":\"weather\",\"variables\":{\"options\":{\"parameters\":[\"t_2m:C\",\"weather_symbol_1h:idx\"],\"lat\":49,\"lng\":15,\"start_datetime\":\"now\",\"hours_range\":0}},\"query\":\"query weather($options: WeatherInput!) {\\n  weather(options: $options) {\\n    data\\n    dateGenerated\\n    status\\n    __typename\\n  }\\n}\\n\"}" +
                        "]");
    }


    public JSONArray getCompanyInfo() throws UnirestException, JSONException {
        return getGraphQl("Get Company info",
                "[{\"operationName\":\"companies\",\"variables\":{},\"query\":\"query companies {\\n  companies {\\n    id\\n    name\\n    owner_photo_url\\n    access\\n    default\\n    location {\\n      type\\n      coordinates\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\"}" +
                        ",{\"operationName\":\"user\",\"variables\":{},\"query\":\"query user {\\n  user {\\n    planningYears\\n    decimalSeparator\\n    dateFormat\\n    language\\n    slug\\n    features\\n    avatarUrl\\n    __typename\\n  }\\n}\\n\"}]");

    }

    public JSONArray getRestrictedZonesAndOperationName() throws UnirestException, JSONException {

            return getGraphQl("Restricted zones and operation name",
                    "[{\"operationName\":\"restrictedZones\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query restrictedZones($options: LayersInput!) {\\n  restrictedZones(options: $options) {\\n    id\\n    shape\\n    __typename\\n  }\\n}\\n\"}" +
                            ",{\"operationName\":\"layers\",\"variables\":{\"options\":{\"companyId\":2434,\"types\":[\"soil_zone_map\"]}},\"query\":\"query layers($options: LayersInput!) {\\n  layers(options: $options) {\\n    id\\n    secret_url\\n    data\\n    __typename\\n  }\\n}\\n\"}]");

    }

    public JSONArray getRestrictedZones() throws UnirestException, JSONException {
        return getGraphQl("RestrictedZones",
                "[{\"operationName\":\"restrictedZones\",\"variables\":{\"options\":{\"companyId\":2434}},\"query\":\"query restrictedZones($options: LayersInput!) {\\n  restrictedZones(options: $options) {\\n    id\\n    shape\\n    __typename\\n  }\\n}\\n\"}]");
    }

    public JSONArray getMapLayers() throws UnirestException, JSONException {
        return getGraphQl("Layers",
                "[{\"operationName\":\"layers\",\"variables\":{\"options\":{\"companyId\":2434,\"types\":[\"soil_zone_map\"]}},\"query\":\"query layers($options: LayersInput!) {\\n  layers(options: $options) {\\n    id\\n    secret_url\\n    data\\n    __typename\\n  }\\n}\\n\"}]");
    }

    public JSONArray getWeather() throws UnirestException, JSONException {
        return getGraphQl("Get Weather data",
                "[{\"operationName\":\"weather\",\"variables\":{\"options\":{\"parameters\":[\"t_2m:C\",\"weather_symbol_1h:idx\"],\"lat\":51.789877757438774,\"lng\":10.43254315853119,\"start_datetime\":\"now\",\"hours_range\":0}},\"query\":\"query weather($options: WeatherInput!) {\\n  weather(options: $options) {\\n    data\\n    dateGenerated\\n    status\\n    __typename\\n  }\\n}\\n\"}]");
    }





    public User getRESTUser() throws Exception {
        HttpResponse<String> response = Unirest.get("https://core.agricircle.com/api/v1/user")
                .header("accept", "application/json, text/javascript, */*; q=0.01")
                .header("cookie", cookieAuth)
                .asString();

        if (response.getCode() != 200) {
            System.out.println(response.getCode());
            System.out.println(response.getBody());
            throw new Exception("øv");
        }

        User user = new Gson().fromJson(response.getBody(), User.class);
        System.out.println(user);

        return user;
    }







}
