package com.cowave.zoo.dtm.client.tcc;

import com.cowave.zoo.dtm.client.DtmClient;
import com.cowave.zoo.dtm.client.DtmResponse;
import com.cowave.zoo.dtm.client.impl.Tcc;
import com.cowave.zoo.http.client.response.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 *
 * @author shanhuiming
 *
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api/tcc"))
@RestController
public class TccMockController {

    private final DtmClient dtmClient;

    @RequestMapping("/mock1")
    public HttpResponse<DtmResponse> mock1() throws Exception {
        return dtmClient.tcc(this::mock1Branch);
    }

    public void mock1Branch(Tcc tcc) throws Exception {
        tcc.branch("http://localhost:8081/api/tcc/barrier/transOutTry",
                "http://localhost:8081/api/tcc/barrier/transOutConfirm",
                "http://localhost:8081/api/tcc/barrier/transOutCancel",
                new TccModel("shanhm", -30));
        tcc.branch("http://localhost:8081/api/tcc/barrier/transInTry",
                "http://localhost:8081/api/tcc/barrier/transInConfirm",
                "http://localhost:8081/api/tcc/barrier/transInCancel",
                new TccModel("shanhm", 30));
    }

    @RequestMapping("/mock2")
    public HttpResponse<DtmResponse> mock2() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::mock2Branch);
    }

    public void mock2Branch(Tcc tcc) throws Exception {
        tcc.branch("http://localhost:8081/api/tcc/barrier/transOutTry",
                "http://localhost:8081/api/tcc/barrier/transOutConfirm",
                "http://localhost:8081/api/tcc/barrier/transOutCancel",
                new TccModel("shanhm", -1000));
        tcc.branch("http://localhost:8081/api/tcc/barrier/transInTry",
                "http://localhost:8081/api/tcc/barrier/transInConfirm",
                "http://localhost:8081/api/tcc/barrier/transInCancel",
                new TccModel("shanhm", 1000));
    }

    @RequestMapping("/mock3")
    public HttpResponse<DtmResponse> mock3() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::mock3Branch);
    }

    public void mock3Branch(Tcc tcc) throws Exception {
        tcc.branch("http://localhost:8081/api/tcc/barrier/transOutTry",
                "http://localhost:8081/api/tcc/barrier/transOutConfirm",
                "http://localhost:8081/api/tcc/barrier/transOutCancel",
                new TccModel("shanhm", -10000));
        tcc.branch("http://localhost:8081/api/tcc/barrier/transInTry",
                "http://localhost:8081/api/tcc/barrier/transInConfirm",
                "http://localhost:8081/api/tcc/barrier/transInCancel",
                new TccModel("shanhm", 10000));
    }
}
