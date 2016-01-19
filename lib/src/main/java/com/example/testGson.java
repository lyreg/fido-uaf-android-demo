package com.example;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2016/1/19.
 */
public class testGson {

    class Response {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static void main(String[] args) {
        Gson gson = new Gson();

        String json = "[{\"header\":{\"upv\":{\"major\":1,\"minor\":0},\"op\":\"Reg\",\"appID\":\"https://www.head2toes.org/fidouaf/v1/public/uaf/facets\",\"serverData\":\"OWZhZjU1OTQxNzdkMDY5M2IxOGVkNjJlOTU2NDNkODYxOGI2MjU1MmIxYjU0YWVhMGE4NzdkNmFiNmJkZmVlMy5NVFExTXpFM01UZzNNalF5T1EuYzJSbS5Ta1JLYUVwRVJYZEtSV2hhVmpJMWRtTnNUWGhoVm5BMFZGWmtOVTF1VFRSbFJUQjJWVEE0\"},\"challenge\":\"JDJhJDEwJEhZV25vclMxaVp4TVd5MnM4eE0vU08\",\"username\":\"sdf\",\"policy\":{\"accepted\":[[{\"aaid\":[\"EBA0#0001\"]}],[{\"aaid\":[\"0015#0001\"]}],[{\"aaid\":[\"0012#0002\"]}],[{\"aaid\":[\"0010#0001\"]}],[{\"aaid\":[\"4e4e#0001\"]}],[{\"aaid\":[\"5143#0001\"]}],[{\"aaid\":[\"0011#0701\"]}],[{\"aaid\":[\"0013#0001\"]}],[{\"aaid\":[\"0014#0000\"]}],[{\"aaid\":[\"0014#0001\"]}],[{\"aaid\":[\"53EC#C002\"]}]]}}]";
        Response response = gson.fromJson(json, Response.class);
        System.out.println(response.toString());
    }
}
