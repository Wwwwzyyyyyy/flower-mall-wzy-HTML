package cn.flower.mall;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import static cn.flower.mall.Store.m;
import static cn.flower.mall.MallService.*;
@Component
class Bootstrap implements CommandLineRunner {
 final Store db;final MallService s;final ObjectMapper json;final String adminPassword;final boolean demo;
 Bootstrap(Store db,MallService s,ObjectMapper json,@Value("${app.bootstrap-admin-password}")String password,@Value("${app.seed-demo}")boolean demo){this.db=db;this.s=s;this.json=json;this.adminPassword=password;this.demo=demo;}
 @Override @Transactional public void run(String...args)throws Exception {
   if(db.one("SELECT id FROM account WHERE role='ADMIN'",m())==null){check(adminPassword.length()>=8&&adminPassword.length()<=72,"首次运行请配置8至72位 ADMIN_PASSWORD");db.add("account",m("username","admin","password_hash",new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(adminPassword),"nickname","花礼掌柜","role","ADMIN"));}
   if(demo&&db.one("SELECT id FROM account WHERE username='demo'",m())==null){long uid=db.add("account",m("username","demo","password_hash",new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("Demo123!"),"nickname","小花同学","phone","13800000000","role","USER"));db.add("address",m("user_id",uid,"receiver","小花","phone","13800000000","province","浙江省","city","杭州市","district","西湖区","detail","示例地址，请下单前修改","is_default",1));}
   if(db.one("SELECT id FROM category LIMIT 1",m())!=null)return;
   var categories=List.of("浪漫告白","生日惊喜","温暖问候","毕业季","开业庆典","日常悦己");for(String name:categories)db.add("category",m("name",name));
   var data=json.readTree(getClass().getResourceAsStream("/demo.json"));
   if(demo)for(var node:data.path("products")){Map<String,Object> p=json.convertValue(node,Map.class);long pid=db.add("product",p);db.add("product_image",m("product_id",pid,"image_url",p.get("cover"),"image_type","cover","sort",0));s.stockLog(pid,"入库",num(p.get("stock")),0,num(p.get("stock")),"演示商品初始化",1);}
   for(var node:data.path("flowers")){Map<String,Object> f=json.convertValue(node,Map.class);long fid=db.add("flower_language",f);if(demo)db.add("product_flower_language",m("product_id",((fid-1)%6)+1,"flower_id",fid));}
 }
}

