package com.cowave.zoo.dtm.client.tcc;

import com.cowave.zoo.dtm.client.DtmClient;
import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.impl.BarrierParam;
import com.cowave.zoo.http.client.asserts.HttpException;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cowave.zoo.http.client.constants.HttpCode.INTERNAL_SERVER_ERROR;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tcc/barrier")
public class TccBarrierController {

    private final DtmClient dtmClient;

    @RequestMapping("/transOutTry")
    public HttpResponse<DtmResponse> transOutTry(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transOutTry(tccModel));
    }

    @RequestMapping("/transOutConfirm")
    public HttpResponse<DtmResponse> transOutConfirm(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transOutConfirm(tccModel));
    }

    @RequestMapping("/transOutCancel")
    public HttpResponse<DtmResponse> transOutCancel(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transOutCancel(tccModel));
    }

    private boolean transOutTry(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transOut try " + tccModel.getAmount());
        return true;
    }

    public boolean transOutConfirm(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transOut confirm " + tccModel.getAmount());
        return true;
    }

    public boolean transOutCancel(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transOut cancel " + tccModel.getAmount());
        return true;
    }


    @RequestMapping("/transInTry")
    public HttpResponse<DtmResponse> transInTry(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transInTry(tccModel));
    }

    @RequestMapping("/transInConfirm")
    public HttpResponse<DtmResponse> transInConfirm(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transInConfirm(tccModel));
    }

    @RequestMapping("/transInCancel")
    public HttpResponse<DtmResponse> transInCancel(BarrierParam barrierParam, @RequestBody TccModel tccModel) throws Exception {
        return dtmClient.barrier(barrierParam, op -> this.transInCancel(tccModel));
    }

    private boolean transInTry(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transIn try " + tccModel.getAmount());
        return true;
    }

    private boolean transInConfirm(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transIn confirm " + tccModel.getAmount());
        if(tccModel.getAmount() < 100){
            return true;
        }else if(tccModel.getAmount() < 10000){
            return false;
        }else{
            throw new HttpException(INTERNAL_SERVER_ERROR);
        }
    }

    private boolean transInCancel(TccModel tccModel) {
        log.info(tccModel.getUserId() + " transIn cancel " + tccModel.getAmount());
        return true;
    }
}
