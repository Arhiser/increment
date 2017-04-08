package com.arhiser.increment.network.api;


public interface SalonsAPI {
    /*

    @Headers("Connection: close")
    @FormUrlEncoded
    @POST("/customer/auth/send_me_sms_code")
    Observable<String> requestLoginSmsCode(@Field("phone") String key);

    @POST("/customer/auth/just_let_me_in")
    Observable<LoginResult> customerLoginByPhone(@QueryMap Map<String, String> fields);

    @PUT("/customer")
    Observable<Customer> putCustomer(@Query("session_key") String sessionKey, @Body JsonObject customerJson);

    @GET("/customer/masters/by_any")
    Observable<ArrayList<MarketMaster>> getMarketplaceMasters(@QueryMap Map<String, String> fields, @Query("filters[time_for_service][slots][]") List<String> slots, @Query("filters[time_for_service][dates][]") List<String> dates);

    @GET("/common/masters/profile/{moiprofi_nickname}")
    Observable<Master> getMasterPublic(@Path("moiprofi_nickname") String nickName, @QueryMap Map<String, String> fields);

    @GET("/customer/contacts")
    Observable<ContactsResult> getContactsCustomer(@QueryMap Map<String, String> fields);

    @GET("/customer/masters/{id}/free_slots_by_duration")
    Observable<ArrayList<Integer>> getMasterFreeSlotsCustomer(@Path("id") int masterId, @QueryMap Map<String, String> fields);

    @GET("/customer/masters/{id}/free_slots_for_period_by_duration")
    Observable<Map<Date, ArrayList<Integer>>> getMasterFreeSlotsForPeriodCustomer(@Path("id") int masterId, @QueryMap Map<String, String> fields);

    @GET("/customer/masters/{id}/reviews")
    Observable<ArrayList<Review>> getReviews(@Path("id") int masterId);

    @POST("/customer/contacts")
    Observable<Contact> postMasterById(@QueryMap Map<String, String> fields);

    @POST("/customer/events")
    Observable<Event> postEventCustomer(@Query("session_key") String sessionKey, @Body JsonObject event);

    @PUT("/customer/events/{id}")
    Observable<Event> putEventCustomer(@Path("id") int id, @Query("session_key") String sessionKey, @Body JsonObject event);

    @POST("/customer/events/{id}/accept")
    Observable<Event> postAcceptEventCustomer(@Path("id") int id, @Query("session_key") String sessionKey);

    @POST("/customer/events/{id}/reject")
    Observable<Event> postRejectEventCustomer(@Path("id") int id, @Query("session_key") String sessionKey);

    @POST("/customer/events/{id}/do")
    Observable<Event> postDoEventCustomer(@Path("id") int id, @Query("session_key") String sessionKey);

    @POST("/customer/events/{id}/cancel")
    Observable<Event> postCancelEventCustomer(@Path("id") int id, @Query("session_key") String sessionKey);

    @GET("/customer/events")
    Observable<EventsResult> getEventsCustomerWithPagination(@QueryMap Map<String, String> fields);

    @GET("/customer/events/{id}")
    Observable<Event> getEventCustomer(@Path("id") int eventId, @Query("session_key") String sessionKey);

    @GET("/customer/events/{id}/reviews")
    Observable<List<Review>> getEventReviewsCustomer(@Path("id") int eventId, @Query("session_key") String sessionKey);

    @POST("/customer/reviews")
    Observable<Review> postReviewCustomer(@Query("event_id") int eventId, @Query("body") String body, @Query("rating") int rating, @Query("session_key") String sessionKey);

    @PUT("/customer/reviews/{id}")
    Observable<Review> putReviewCustomer(@Path("id") int reviewId, @Query("body") String body, @Query("rating") int rating, @Query("session_key") String sessionKey);

    @GET("/customer/masters/{id}/available_masters")
    Observable<List<Master>> getEmployeesCustomer(@Path("id") int masterId, @Query("session_key") String key);

    @PUT("/customer/contacts/{id}")
    Observable<Contact> putContact(@Path("id") String path, @QueryMap Map<String, String> fields);

    @GET("/customer/events/is_there_any_by_master")
    Observable<Boolean> haveAnyEventWithMaster(@Query("master_id") int masterId, @Query("session_key") String key);

    @GET("/customer/me")
    Observable<LoginResult> getMeCustomer(@Query("session_key") String sessionKey);

    @Multipart
    @PUT("/customer")
    Observable<Customer> putCustomer(@Query("session_key") String sessionKey, @Part("customer[avatar]")
            TypedFile photo, @PartMap Map<String, String> fields);

    @Headers("Connection: close")
    @POST("/customer/verify")
    Observable<Object> sendPhoneVerification(@QueryMap Map<String, String> fields);

    @Headers("Connection: close")
    @POST("/customer/resend_verify")
    Observable<Object> customerResendVerificationPhone(@QueryMap Map<String, String> fields);

    @Multipart
    @POST("/images")
    Observable<Photo> postImage(@PartMap Map<String, String> fields, @Part("image[photo]") TypedFile photo);
    */
}





