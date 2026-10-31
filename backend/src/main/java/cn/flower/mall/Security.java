package cn.flower.mall;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import static cn.flower.mall.Store.m;

@Component
class Tokens {
  private final byte[] secret; private final ObjectMapper json;
  Tokens(@Value("${app.jwt-secret}") String secret,ObjectMapper json) {
    if(secret.length()<32)throw new IllegalStateException("请设置至少32位的 JWT_SECRET");
    this.secret=secret.getBytes(StandardCharsets.UTF_8);this.json=json;
  }
  String b64(byte[] b){return Base64.getUrlEncoder().withoutPadding().encodeToString(b);}
  byte[] sign(String v)throws Exception {var mac=Mac.getInstance("HmacSHA256");mac.init(new SecretKeySpec(secret,"HmacSHA256"));return mac.doFinal(v.getBytes(StandardCharsets.UTF_8));}
  String create(Map<String,Object> user){try {
    String raw=b64("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8))+"."+b64(json.writeValueAsBytes(m("sub",user.get("id"),"role",user.get("role"),"ver",user.get("token_version"),"exp",Instant.now().getEpochSecond()+86400)));
    return raw+"."+b64(sign(raw));
  }catch(Exception e){throw new IllegalStateException(e);}}
  Map<String,Object> read(String token){try {
    String[] p=token.split("\\.");if(p.length!=3||!MessageDigest.isEqual(sign(p[0]+"."+p[1]),Base64.getUrlDecoder().decode(p[2])))throw new Exception();
    var v=json.readValue(Base64.getUrlDecoder().decode(p[1]),Map.class);
    if(((Number)v.get("exp")).longValue()<Instant.now().getEpochSecond())throw new Exception();return v;
  }catch(Exception e){throw new BusinessException(401,"登录已过期，请重新登录");}}
}
@Component
class WebSecurity implements WebMvcConfigurer, HandlerInterceptor {
  private final Tokens tokens; private final Store db; private final String upload;
  WebSecurity(Tokens tokens,Store db,@Value("${app.upload-dir}")String upload){this.tokens=tokens;this.db=db;this.upload=upload;}
  @Override public void addInterceptors(InterceptorRegistry r){r.addInterceptor(this).addPathPatterns("/api/**");}
  @Override public void addResourceHandlers(ResourceHandlerRegistry r){r.addResourceHandler("/uploads/**").addResourceLocations(java.nio.file.Path.of(upload).toAbsolutePath().toUri().toString()+"/");}
  @Override public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler){
    res.setHeader("X-Content-Type-Options","nosniff");res.setHeader("Cache-Control","no-store");
    String path=req.getRequestURI();
    if(path.equals("/api/health")||path.equals("/api/captcha")||path.equals("/api/user/register")||path.equals("/api/user/login")||path.equals("/api/admin/login"))return true;
    if(req.getMethod().equals("GET")&&(path.startsWith("/api/product/")||path.equals("/api/category/list")||path.startsWith("/api/flower-language/")||path.startsWith("/api/review/product/")))return true;
    String h=req.getHeader("Authorization");if(h==null||!h.startsWith("Bearer "))throw new BusinessException(401,"请先登录");
    var claim=tokens.read(h.substring(7));var user=db.one("SELECT * FROM account WHERE id=#{p.id}",m("id",claim.get("sub")));
    if(user==null||((Number)user.get("token_version")).intValue()!=((Number)claim.get("ver")).intValue())throw new BusinessException(401,"请重新登录");
    if(((Number)user.get("status")).intValue()!=1)throw new BusinessException(403,"账号已被禁用");
    String role=(String)user.get("role");
    if(path.startsWith("/api/admin/")&&!role.equals("ADMIN"))throw new BusinessException(403,"需要商家管理员权限");
    if(!path.startsWith("/api/admin/")&&!role.equals("USER"))throw new BusinessException(403,"请使用普通用户账号");
    req.setAttribute("uid",((Number)user.get("id")).longValue());return true;
  }
}
class BusinessException extends RuntimeException {
  final int status; BusinessException(String msg){this(400,msg);} BusinessException(int status,String msg){super(msg);this.status=status;}
}
