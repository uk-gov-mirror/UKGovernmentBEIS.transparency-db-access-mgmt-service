package com.beis.subsidy.control.accessmanagementservice.controller.feign;

import com.beis.subsidy.control.accessmanagementservice.request.AddUserRequest;
import com.beis.subsidy.control.accessmanagementservice.response.AccessTokenResponse;
import feign.Headers;
import feign.RequestLine;
import feign.Response;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@FeignClient(name = "GraphAPIFeignClient", url = "${graphApiUrl}")
public interface GraphAPIFeignClient {

    @GetMapping(value = "/v1.0/users")
    @RequestLine("GET v1.0/users")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getAllUserProfiles(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation);

    @PostMapping(value = "/v1.0/users")
    @RequestLine("POST v1.0/users")
    Response addUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                                         @RequestBody AddUserRequest request);

    @DeleteMapping(value = "v1.0/users/{userId}")
    @RequestLine("DELETE v1.0/users")
    Response deleteUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                        @PathVariable("userId")  String userId);

    @GetMapping(value = "/v1.0/groups/{groupId}/members")
    @RequestLine("GET /v1.0/groups/{groupId}/members")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUsersByGroupId(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("groupId")  String groupId);

    @GetMapping(value = "/v1.0/users/{userId}/appRoleAssignments")
    @RequestLine("GET /v1.0/users/{userId}/appRoleAssignments")
    @Headers({"Authorization: {authorization}","Content-Type: application/json"})
    Response getUserGroupName(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorisation,
                               @PathVariable("userId")  String userId);
}