## dtm-client DTM事务接入

### 功能说明

- 提供了一些DTM事务的接入操作；

### 依赖

```xml
<dependency>
    <groupId>com.cowave.commons</groupId>
    <artifactId>dtm-client</artifactId>
    <version>2.8.0</version>
</dependency>
```

### 配置

```yaml
spring:
  dtm:
    address: http://localhost:36789
```

### DTM响应说明

> 具体可以参考: com.cowave.commons.dtm.DtmResult

- HTTP 200， SUCCESS 成功
- HTTP 409， FAILURE 失败，不再重试
- HTTP 425， ONGOING 进行中，固定间隔重试
- HTTP 500， ERROR   异常，指数退避重试

> Saga将事务拆分成多个step，submit之后由DTM来顺序执行step，如果某个step失败，DTM会根据相反顺序一次调用补偿操作；

> Tcc将事务拆分为多个branch，每个branch分成3个阶段: Try-Confirm-Cancel。注册之后由DTM统一执行所有branch的Try操作，全部成功则执行所有的Confirm操作，否则执行所有的Cancel操作；

> 针对补偿或取消操作，为了防止重复执行等问题，引入了Barrier，就是通过记录比较一些标识信息来避免重复或空悬问题；
> Barrier默认是往一张表dtm_barrier(mysql语法)中记录信息，如果使用自定义的表，可以覆盖下方法: Barrier.insertBarrier；

### 使用示例

> 具体示例参考: dtm-example

#### 1. Saga

```java
@RequiredArgsConstructor
@RestController
@RequestMapping(("/api"))
public class SagaController {

    private final DtmClient dtmClient;

    @RequestMapping("/saga")
    public DtmResponse saga() throws DtmException {
        Saga saga = dtmClient.saga(UUID.randomUUID().toString());
        saga.step("http://localhost:8081/api/TransOut", "http://localhost:8081/api/TransOutCompensate", "");
        saga.step("http://localhost:8081/api/TransIn", "http://localhost:8081/api/TransInCompensate", "");
        saga.enableWaitResult();
        return saga.submit();
    }
}
```

#### 2. Tcc

```java
@Slf4j
@RequiredArgsConstructor
@RequestMapping(("/api"))
@RestController
public class TccController {

    private final DtmClient dtmClient;

    @RequestMapping("tcc/barrier")
    public DtmResponse tccBarrier() throws Exception {
        return dtmClient.tcc(UUID.randomUUID().toString(), this::barrierBranch);
    }

    public void barrierBranch(Tcc tcc) throws Exception {
        String outResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransOutTry",
                "http://localhost:8081/api/barrierTransOutConfirm",
                "http://localhost:8081/api/barrierTransOutCancel",
                new TransReq(1, -30));
        log.info("tcc branch out: " + outResponse);

        String inResponse = tcc.branch(
                "http://localhost:8081/api/barrierTransInTry",
                "http://localhost:8081/api/barrierTransInConfirm",
                "http://localhost:8081/api/barrierTransInCancel",
                new TransReq(2, 30));
        log.info("tcc branch in: " + inResponse);
    }
}
```

#### 3. Barrier

```java 
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiBarrierController {

    private final DataSource dataSource;

    @RequestMapping("barrierTransOutTry")
    public DtmResponse transOutTry(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutPrepare(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransOutConfirm")
    public Object transOutConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutSubmit(transReq));
        return DtmResponse.success();
    }

    @RequestMapping("barrierTransOutCancel")
    public Object transOutCancel(BarrierParam barrierParam, @RequestBody TransReq transReq) throws Exception {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        branchBarrier.call((barrier) -> this.transOutCancel(transReq));
        return DtmResponse.success();
    }

    // ... ...

    @RequestMapping("barrierTransInConfirm")
    public HttpResponse<String> transInConfirm(BarrierParam barrierParam, @RequestBody TransReq transReq) {
        Barrier branchBarrier = new Barrier(barrierParam, dataSource);
        try {
            branchBarrier.call((barrier) -> this.transInSubmit(transReq));
        } catch (Exception e) {
            return new HttpResponse<>(409, null, null);
        }
        return HttpResponse.success();
    }

    private void transOutPrepare(TransReq transReq) {
        log.info("user[{}] 转出准备 {}", transReq.getUserId(), transReq.getAmount());
    }

    public void transOutSubmit(TransReq transReq) {
        log.info("user[{}] 转出提交 {}", transReq.getUserId(), transReq.getAmount());
    }

    public void transOutCancel(TransReq transReq) {
        log.info("user[{}] 转出回滚 {}", transReq.getUserId(), transReq.getAmount());
    }

    // ... ...

    private void transInSubmit(TransReq transReq) {
        Asserts.isTrue(transReq.getAmount() <= 1000, "金额过大");
        log.info("user[{}] 转入提交 {}", transReq.getUserId(), transReq.getAmount());
    }
}
```

