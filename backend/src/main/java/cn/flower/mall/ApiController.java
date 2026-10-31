package cn.flower.mall;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import static cn.flower.mall.Store.m;
import static cn.flower.mall.MallService.*;

@RestController @RequestMapping("/api")
public class ApiController {
 final MallService s;final Store db;final MediaService media;final GreetingService ai;final CaptchaService captcha;
 ApiController(MallService s,Store db,MediaService media,GreetingService ai,CaptchaService captcha){this.s=s;this.db=db;this.media=media;this.ai=ai;this.captcha=captcha;}
 long uid(HttpServletRequest r){return (Long)r.getAttribute("uid");}
 Object ok(){return m("success",true);}
 @GetMapping("/health") Object health(){return m("status","UP","payment","MOCK","ai",ai.key.isBlank()?"TEMPLATE":"CONFIGURED");}
 @GetMapping("/captcha") Object captcha(){return captcha.create();}
 @PostMapping("/user/register") Object register(@RequestBody Map<String,Object>b){captcha.verify(b);return s.register(b);}
 @PostMapping("/user/login") Object login(@RequestBody Map<String,Object>b,HttpServletRequest r){captcha.verify(b);return s.login(b,"USER",r.getRemoteAddr());}
 @PostMapping("/admin/login") Object adminLogin(@RequestBody Map<String,Object>b,HttpServletRequest r){captcha.verify(b);return s.login(b,"ADMIN",r.getRemoteAddr());}
 @GetMapping("/user/info") Object info(HttpServletRequest r){return s.user(uid(r));}
 @PutMapping("/user/update") Object profile(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.profile(uid(r),b);}
 @PutMapping("/user/password") Object password(HttpServletRequest r,@RequestBody Map<String,Object>b){s.password(uid(r),b);return ok();}
 @GetMapping("/category/list") Object categories(){return db.list("SELECT * FROM category ORDER BY id",m());}
 @GetMapping({"/product/list","/product/search"}) Object products(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")long category,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="12")int size){return s.products(false,q,category,page,size);}
 @GetMapping("/product/{id}") Object product(@PathVariable long id){return s.product(id,false);}
 @GetMapping("/product/{id}/images") Object images(@PathVariable long id){return s.product(id,false).get("images");}
 @GetMapping("/flower-language/list") Object flowers(@RequestParam(defaultValue="")String q){return s.flowers(q);}
 @GetMapping("/flower-language/{id}") Object flower(@PathVariable long id){return s.flower(id);}
 @GetMapping("/cart/list") Object cart(HttpServletRequest r){return s.cart(uid(r));}
 @PostMapping("/cart/add") Object addCart(HttpServletRequest r,@RequestBody Map<String,Object>b){s.addCart(uid(r),b);return ok();}
 @PutMapping("/cart/update") Object updateCart(HttpServletRequest r,@RequestBody Map<String,Object>b){s.updateCart(uid(r),b);return ok();}
 @DeleteMapping("/cart/{id}") Object deleteCart(HttpServletRequest r,@PathVariable long id){s.removeCart(uid(r),id);return ok();}
 @GetMapping("/address/list") Object addresses(HttpServletRequest r){return s.addresses(uid(r));}
 @PostMapping("/address/add") Object addAddress(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.saveAddress(uid(r),b);}
 @PutMapping("/address/update") Object updateAddress(HttpServletRequest r,@RequestBody Map<String,Object>b){check(b.get("id")!=null,"缺少地址编号");return s.saveAddress(uid(r),b);}
 @DeleteMapping("/address/{id}") Object deleteAddress(HttpServletRequest r,@PathVariable long id){s.deleteAddress(uid(r),id);return ok();}
 @GetMapping("/favorite/list") Object favorites(HttpServletRequest r){return s.favorites(uid(r));}
 @PostMapping("/favorite/{id}") Object favorite(HttpServletRequest r,@PathVariable long id){s.favorite(uid(r),id);return ok();}
 @PostMapping("/order/create") Object createOrder(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.createOrder(uid(r),b);}
 @GetMapping("/order/list") Object orders(HttpServletRequest r,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="-1")int status,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.orders(uid(r),q,status,page,size);}
 @GetMapping({"/order/{id}","/order/status/{id}"}) Object order(HttpServletRequest r,@PathVariable long id){return s.order(id,uid(r));}
 @PutMapping("/order/cancel/{id}") Object cancel(HttpServletRequest r,@PathVariable long id){s.cancel(uid(r),id);return ok();}
 @PostMapping("/payment/mock/{id}") Object pay(HttpServletRequest r,@PathVariable long id){return s.pay(uid(r),id,true);}
 @PostMapping("/payment/mock/{id}/fail") Object failPay(HttpServletRequest r,@PathVariable long id){return s.pay(uid(r),id,false);}
 @GetMapping("/payment/{id}") Object payment(HttpServletRequest r,@PathVariable long id){s.order(id,uid(r));return db.one("SELECT * FROM payment_record WHERE order_id=#{p.id}",m("id",id));}
 @PostMapping("/ai/greeting/generate") Object generate(HttpServletRequest r,@RequestBody Map<String,Object>b){return ai.generate(uid(r),b);}
 @GetMapping("/ai/greeting/list") Object greetings(HttpServletRequest r,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return ai.list(uid(r),page,size);}
 @GetMapping("/ai/greeting/{id}") Object greeting(HttpServletRequest r,@PathVariable long id){return s.must("SELECT * FROM greeting WHERE id=#{p.id} AND user_id=#{p.uid}",m("id",id,"uid",uid(r)));}
 @DeleteMapping("/ai/greeting/{id}") Object deleteGreeting(HttpServletRequest r,@PathVariable long id){db.exec("DELETE FROM greeting WHERE id=#{p.id} AND user_id=#{p.uid}",m("id",id,"uid",uid(r)));return ok();}
 @PostMapping("/review/image/upload") Object uploadReview(HttpServletRequest r,@RequestParam MultipartFile file){return media.upload(uid(r),"review",file);}
 @PostMapping("/user/avatar/upload") Object uploadAvatar(HttpServletRequest r,@RequestParam MultipartFile file){return media.upload(uid(r),"avatar",file);}
 @DeleteMapping("/media/{id}") Object deleteMedia(HttpServletRequest r,@PathVariable long id){media.delete(uid(r),id,false);return ok();}
 @PostMapping("/review/add") Object addReview(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.saveReview(uid(r),null,b);}
 @Transactional @PostMapping("/review/{id}/report") Object report(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){s.must("SELECT id FROM review WHERE id=#{p.id} AND deleted=0 AND status=1",m("id",id));db.add("review_report",m("review_id",id,"user_id",uid(r),"reason",text(b,"reason",500,true)));return ok();}
 @PutMapping("/review/{id}") Object updateReview(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){return s.saveReview(uid(r),id,b);}
 @DeleteMapping("/review/{id}") Object deleteReview(HttpServletRequest r,@PathVariable long id){s.deleteReview(uid(r),id);return ok();}
 @GetMapping("/review/product/{id}") Object productReviews(@PathVariable long id,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.reviews(null,id,false,0,page,size,"");}
 @GetMapping("/review/my") Object myReviews(HttpServletRequest r,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.reviews(uid(r),null,false,0,page,size,"");}
 @GetMapping("/review/{id}") Object review(HttpServletRequest r,@PathVariable long id){var v=s.must("SELECT * FROM review WHERE id=#{p.id} AND user_id=#{p.uid} AND deleted=0",m("id",id,"uid",uid(r)));v.put("images",db.list("SELECT * FROM review_image WHERE review_id=#{p.id} ORDER BY sort,id",m("id",id)));return v;}
 @PostMapping("/refund/apply") Object refundApply(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.applyRefund(uid(r),b);}
 @GetMapping("/refund/my") Object myRefunds(HttpServletRequest r,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.refunds(uid(r),page,size);}
 @GetMapping("/refund/{id}") Object refund(HttpServletRequest r,@PathVariable long id){return s.refund(id,uid(r));}
 @GetMapping("/admin/products") Object adminProducts(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")long category,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.products(true,q,category,page,size);}
 @GetMapping("/admin/product/{id}") Object adminProduct(@PathVariable long id){return s.product(id,true);}
 @PostMapping("/admin/product") Object addProduct(HttpServletRequest r,@RequestBody Map<String,Object>b){return s.saveProduct(uid(r),null,b);}
 @PutMapping("/admin/product/{id}") Object saveProduct(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){return s.saveProduct(uid(r),id,b);}
 @PutMapping("/admin/product/{id}/status") Object productStatus(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){s.productStatus(uid(r),id,num(b.get("status")));return ok();}
 @DeleteMapping("/admin/product/{id}") Object deleteProduct(HttpServletRequest r,@PathVariable long id){s.deleteProduct(uid(r),id);return ok();}
 @PostMapping("/admin/product/image/upload") Object uploadProduct(HttpServletRequest r,@RequestParam MultipartFile file){return media.upload(uid(r),"product",file);}
 @DeleteMapping("/admin/media/{id}") Object adminDeleteMedia(HttpServletRequest r,@PathVariable long id){media.delete(uid(r),id,true);return ok();}
 @Transactional @DeleteMapping("/admin/product/image/{id}") Object deleteProductImage(@PathVariable long id){var img=s.must("SELECT * FROM product_image WHERE id=#{p.id}",m("id",id));check(!"cover".equals(img.get("image_type")),"封面请在商品编辑中替换");db.exec("DELETE FROM product_image WHERE id=#{p.id}",m("id",id));var f=db.one("SELECT * FROM uploaded_file WHERE url=#{p.url}",m("url",img.get("image_url")));if(f!=null){long refs=id(db.one("SELECT (SELECT COUNT(*) FROM product_image WHERE image_url=#{p.url})+(SELECT COUNT(*) FROM order_item WHERE cover=#{p.url})+(SELECT COUNT(*) FROM product WHERE cover=#{p.url}) AS n",m("url",img.get("image_url"))).get("n"));if(refs==0)media.delete(id(f.get("owner_id")),id(f.get("id")),true);}return ok();}
 @Transactional @PutMapping("/admin/product/image/sort") Object sortImages(@RequestBody Map<String,Object>b){long pid=id(b.get("product_id"));s.product(pid,true);check(b.get("ids") instanceof List<?>,"图片顺序无效");var ids=(List<?>)b.get("ids");var imgs=db.list("SELECT id FROM product_image WHERE product_id=#{p.id} AND image_type='detail'",m("id",pid));check(new HashSet<>(ids.stream().map(MallService::id).toList()).equals(new HashSet<>(imgs.stream().map(i->id(i.get("id"))).toList())),"需要提供全部详情图片编号");int i=0;for(Object v:ids)db.update("product_image",id(v),m("sort",++i));return ok();}
 @GetMapping("/admin/stock/list") Object stockList(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.stockList(q,false,page,size);}
 @GetMapping("/admin/stock/warning") Object stockWarning(@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.stockList("",true,page,size);}
 @GetMapping("/admin/stock/{id}") Object stockDetail(@PathVariable long id){return s.product(id,true);}
 @PutMapping("/admin/stock/{type:add|reduce|adjust}") Object stock(HttpServletRequest r,@PathVariable String type,@RequestBody Map<String,Object>b){s.stock(uid(r),b,type);return ok();}
 @GetMapping("/admin/stock/records") Object stockRecords(@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.stockRecords(page,size);}
 @GetMapping("/admin/orders") Object adminOrders(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="-1")int status,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.orders(null,q,status,page,size);}
 @GetMapping("/admin/order/{id}") Object adminOrder(@PathVariable long id){return s.order(id,null);}
 @PutMapping("/admin/order/{action:accept|reject|status}/{id}") Object orderAction(HttpServletRequest r,@PathVariable String action,@PathVariable long id,@RequestBody(required=false)Map<String,Object>b){s.orderAction(uid(r),id,action,b==null?m():b);return ok();}
 @GetMapping("/admin/refunds") Object adminRefunds(@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.refunds(null,page,size);}
 @GetMapping("/admin/refund/{id}") Object adminRefund(@PathVariable long id){return s.refund(id,null);}
 @PutMapping("/admin/refund/{action:approve|reject}/{id}") Object auditRefund(HttpServletRequest r,@PathVariable String action,@PathVariable long id,@RequestBody(required=false)Map<String,Object>b){s.auditRefund(uid(r),id,action.equals("approve"),b==null?m():b);return ok();}
 @GetMapping("/admin/reviews") Object adminReviews(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int rating,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.reviews(null,null,true,rating,page,size,q);}
 @GetMapping("/admin/review/{id}") Object adminReview(@PathVariable long id){var r=s.must("SELECT * FROM review WHERE id=#{p.id}",m("id",id));r.put("images",db.list("SELECT * FROM review_image WHERE review_id=#{p.id}",m("id",id)));return r;}
 @PostMapping("/admin/review/{id}/reply") Object reply(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){s.reply(uid(r),id,b);return ok();}
 @PutMapping("/admin/review/{id}/status") Object moderate(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){s.moderate(uid(r),id,b);return ok();}
 @GetMapping("/admin/statistics/overview") Object overview(){return s.overview();}
 @GetMapping("/admin/statistics/trend") Object trend(){return s.trend();}
 @GetMapping("/admin/statistics/revenue") Object revenue(@RequestParam(defaultValue="")String start,@RequestParam(defaultValue="")String end,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.revenue(start,end,page,size);}
 @GetMapping("/admin/users") Object users(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.users(q,page,size);}
 @GetMapping("/admin/user/{id}") Object adminUser(@PathVariable long id){var u=s.user(id);check("USER".equals(u.get("role")),"普通用户不存在");u.put("orders",s.orders(id,"",-1,1,50));return u;}
 @PutMapping("/admin/user/status/{id}") Object userStatus(HttpServletRequest r,@PathVariable long id,@RequestBody Map<String,Object>b){s.userStatus(uid(r),id,num(b.get("status")));return ok();}
 @GetMapping("/admin/audit-logs") Object logs(@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.page("audit_log","*","1=1",m(),page,size,"id DESC");}
 @GetMapping("/admin/media") Object mediaList(@RequestParam(defaultValue="1")int page,@RequestParam(defaultValue="10")int size){return s.page("uploaded_file","*","1=1",m(),page,size,"id DESC");}
}
