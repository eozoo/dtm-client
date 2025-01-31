package com.cowave.zoo.dtm.client;

import com.cowave.zoo.http.client.response.HttpResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.TimeZone;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@Getter
@NoArgsConstructor
public class DtmResponse {

    public static final String DTM_SUCCESS = "SUCCESS";

    public static final String DTM_FAILURE = "FAILURE";

    public static final String DTM_ONGOING = "ONGOING";

    public static final String DTM_ERROR = "ERROR";

    /**
     * 成功
     */
    public static final int HTTP_SUCCESS = 200;

    /**
     * 失败，不再重试
     */
    public static final int HTTP_FAILURE = 409;

    /**
     * 进行中，固定间隔重试
     */
    public static final int HTTP_ONGOING = 425;

    /**
     * 异常，指数退避重试
     */
    public static final int HTTP_ERROR = 500;

    public static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setTimeZone(TimeZone.getDefault());
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * dtm_result
     */
    @JsonProperty("dtm_result")
    private String dtmResult;

    /**
     * message
     */
    @JsonProperty("message")
    private String message;

    public DtmResponse(String dtmResult, String message){
        this.dtmResult = dtmResult;
        this.message = message;
    }

    public boolean dtmSuccess(){
        return DTM_SUCCESS.equals(dtmResult);
    }

    public static HttpResponse<DtmResponse> success(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_SUCCESS, httpHeaders, new DtmResponse(DTM_SUCCESS, null));
    }

    public static HttpResponse<DtmResponse> success(String message){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_SUCCESS, httpHeaders, new DtmResponse(DTM_SUCCESS, message));
    }

    public static HttpResponse<DtmResponse> failure(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_FAILURE, httpHeaders, new DtmResponse(DTM_FAILURE, null));
    }

    public static HttpResponse<DtmResponse> failure(String message){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_FAILURE, httpHeaders, new DtmResponse(DTM_FAILURE, message));
    }

    public static HttpResponse<DtmResponse> onGoing(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_ONGOING, httpHeaders, new DtmResponse(DTM_ONGOING, null));
    }

    public static HttpResponse<DtmResponse> onGoing(String message){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_ONGOING, httpHeaders, new DtmResponse(DTM_ONGOING, message));
    }

    public static HttpResponse<DtmResponse> error(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_ERROR, httpHeaders, new DtmResponse(DTM_ERROR, null));
    }

    public static HttpResponse<DtmResponse> error(String message){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_JSON);
        return new HttpResponse<>(HTTP_ERROR, httpHeaders, new DtmResponse(DTM_ERROR, message));
    }

    public static <T> T parseDtmResponse(HttpResponse<DtmResponse> httpResponse, Class<T> clazz){
        DtmResponse dtmResponse = httpResponse.getBody();
        if(!httpResponse.isSuccess()){
            if(dtmResponse != null){
                String message = dtmResponse.getMessage();
                if(StringUtils.hasText(message)){
                    int start = message.indexOf("{");
                    int end = message.indexOf("}");
                    if(start != -1 && end != -1){
                        try{
                            return MAPPER.readValue(message.substring(start, end + 1), clazz);
                        }catch (Exception e){
                            log.error("parseDtmResponse failed: " + message);
                            return null;
                        }
                    }
                }
            }
        }
        return null;
    }
}
