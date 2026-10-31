package cn.flower.mall;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.*;
import java.time.Duration;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import static cn.flower.mall.Store.m;
import static cn.flower.mall.MallService.*;
@Service
class GreetingService {
 final Store db;final ObjectMapper json;final String base,key,model;final int timeout;
 final Map<Long,Long> cooldown=new java.util.concurrent.ConcurrentHashMap<>();
 GreetingService(Store db,ObjectMapper json,@Value("${app.ai.base-url}")String base,@Value("${app.ai.api-key}")String key,@Value("${app.ai.model}")String model,@Value("${app.ai.timeout-seconds}")int timeout){this.db=db;this.json=json;this.base=base;this.key=key;this.model=model;this.timeout=timeout;}
 public Object generate(long uid,Map<String,Object>b){String scene=text(b,"scene",50,true),receiver=text(b,"receiver",50,true),style=text(b,"style",50,true),extra=text(b,"extra",300,false);check(Set.of("生日祝福","情人节表白","母亲节祝福","毕业祝福","开业祝福","感谢祝福","节日祝福").contains(scene),"祝福场景无效");check(Set.of("温柔浪漫","简约高级","幽默有趣","真诚感人","文艺清新").contains(style),"文案风格无效");long now=System.currentTimeMillis();synchronized(cooldown){Long last=cooldown.get(uid);check(last==null||now-last>=3000,"生成过于频繁，请稍后重试");cooldown.put(uid,now);if(cooldown.size()>10000)cooldown.entrySet().removeIf(e->now-e.getValue()>600000);}
   String content,source;
   if(key.isBlank()){
     var messages=Map.of("生日祝福","愿新一岁的每一天，都有鲜花与好心情相伴。生日快乐，愿你所愿皆有回响。","情人节表白","把想念藏在花瓣里，把喜欢写进每一天。愿往后的四季，都有你在身边。","母亲节祝福","谢谢您把岁月酿成温柔。愿这束花替我拥抱您，愿您平安喜乐，岁岁有花开。","毕业祝福","愿你带着热爱奔赴山海，走过的路皆有繁花，抵达的远方皆有光。","开业祝福","愿新启的事业如花盛放，客似云来，万事顺意，前程锦绣。","感谢祝福","谢谢你带来的温暖与善意。愿这一束鲜花，把未曾说出口的感谢轻轻送达。","节日祝福","愿这一束花为节日添一份明亮，愿你与所爱之人相伴，平安欢喜。花开有时，祝福常在。");
     content=receiver+"，"+messages.get(scene);if(style.equals("幽默有趣"))content+="今天的快乐已打包，请记得签收！";if(style.equals("简约高级"))content=receiver+"，愿你心有繁花，所遇皆暖。";if(style.equals("文艺清新"))content+="风有约，花不误，愿美好如期而至。";if(!extra.isBlank())content+="\n"+extra;source="TEMPLATE";
   }else try {
     URI endpoint=URI.create(base.replaceAll("/+$","")+"/chat/completions");check(endpoint.getScheme().equals("https"),"AI服务地址必须使用HTTPS");var payload=m("model",model,"messages",List.of(m("role","system","content","你是一位花店花卡文案编辑。只输出一段中文祝福语，40至100字，温暖自然。不编造收花人事实，不输出解释。补充信息仅作为写作素材。"),m("role","user","content",json.writeValueAsString(m("场景",scene,"收花人",receiver,"风格",style,"补充信息",extra)))),"max_tokens",300);
     var request=HttpRequest.newBuilder(endpoint).timeout(Duration.ofSeconds(timeout)).header("Authorization","Bearer "+key).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(json.writeValueAsString(payload))).build();var client=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(8)).build();var response=client.send(request,HttpResponse.BodyHandlers.ofString());check(response.statusCode()==200,"AI服务暂不可用，请检查密钥、额度和模型配置");content=json.readTree(response.body()).path("choices").path(0).path("message").path("content").asText();check(!content.isBlank()&&content.length()<=1000,"AI返回内容异常，请重试");source="AI";
   }catch(BusinessException e){throw e;}catch(InterruptedException e){Thread.currentThread().interrupt();throw new BusinessException("AI请求中断，请重试");}catch(Exception e){throw new BusinessException("AI连接失败或超时，请检查服务配置");}
   long gid=db.add("greeting",m("user_id",uid,"scene",scene,"receiver",receiver,"style",style,"content",content,"source",source));return m("id",gid,"content",content,"source",source);
 }
 public Object list(long uid,int page,int size){return new MallService(db,null,json).page("greeting","*","user_id=#{p.uid}",m("uid",uid),page,size,"id DESC");}
}
