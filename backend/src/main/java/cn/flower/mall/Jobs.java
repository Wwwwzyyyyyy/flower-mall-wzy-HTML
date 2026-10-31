package cn.flower.mall;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import static cn.flower.mall.Store.m;
import static cn.flower.mall.MallService.id;
@Component
class Jobs {
 final Store db;final MallService service;final boolean fail;
 Jobs(Store db,MallService service,@Value("${app.mock-refund-fail}")boolean fail){this.db=db;this.service=service;this.fail=fail;}
 @Scheduled(fixedDelay=5000) public void refunds(){for(var r:db.list("SELECT id FROM refund_record WHERE refund_status=2 ORDER BY id LIMIT 50",m()))try{service.processRefund(id(r.get("id")),fail);}catch(Exception e){org.slf4j.LoggerFactory.getLogger(Jobs.class).error("Refund job failed id={}",r.get("id"),e);}}
 @Scheduled(fixedDelay=60000) public void expire(){for(var o:db.list("SELECT id,user_id FROM shop_order WHERE status=0 AND create_time<#{p.time} LIMIT 100",m("time",java.time.LocalDateTime.now().minusMinutes(30))))try{service.cancel(id(o.get("user_id")),id(o.get("id")));}catch(BusinessException ignored){}}
}
