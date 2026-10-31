package cn.flower.mall;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Service;
import static cn.flower.mall.Store.m;

/** 图形验证码：内存保存、5 分钟有效、一次性消费。字符去除易混淆的 0/O/1/I/L。 */
@Service
public class CaptchaService {
  private static final char[] CHARS="ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
  private static final long TTL=300000L;
  private final Map<String,Entry> codes=new ConcurrentHashMap<>();
  private final Random random=new Random();
  private record Entry(String code,long expire){}

  public Map<String,Object> create(){
    String code=randomCode(4);
    String id=UUID.randomUUID().toString().replace("-","");
    long now=System.currentTimeMillis();
    if(codes.size()>5000)codes.entrySet().removeIf(e->now>e.getValue().expire());
    codes.put(id,new Entry(code,now+TTL));
    return m("captchaId",id,"image","data:image/png;base64,"+Base64.getEncoder().encodeToString(render(code)));
  }

  public void verify(Map<String,Object> b){
    String id=Objects.toString(b.get("captchaId"),"").trim();
    String code=Objects.toString(b.get("captchaCode"),"").trim();
    if(id.isBlank()||code.isBlank())throw new BusinessException("请输入图形验证码");
    Entry e=codes.remove(id);
    if(e==null||e.expire()<System.currentTimeMillis())throw new BusinessException("验证码已失效，请点击图片刷新");
    if(!e.code().equalsIgnoreCase(code))throw new BusinessException("验证码错误");
  }

  private String randomCode(int n){
    StringBuilder sb=new StringBuilder();
    for(int i=0;i<n;i++)sb.append(CHARS[random.nextInt(CHARS.length)]);
    return sb.toString();
  }

  private byte[] render(String code){
    int w=120,h=44;
    BufferedImage img=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
    Graphics2D g=img.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
    g.setColor(new Color(0xFBF9F2));g.fillRect(0,0,w,h);
    for(int i=0;i<5;i++){g.setColor(gray(175,225));int x1=random.nextInt(w),y1=random.nextInt(h),x2=random.nextInt(w),y2=random.nextInt(h);g.drawLine(x1,y1,x2,y2);}
    for(int i=0;i<70;i++){g.setColor(gray(150,210));g.fillRect(random.nextInt(w),random.nextInt(h),1,1);}
    g.setFont(new Font("SansSerif",Font.BOLD,28));
    int x=13;
    for(char c:code.toCharArray()){
      g.setColor(gray(45,110));
      Graphics2D cg=(Graphics2D)g.create();
      cg.rotate((random.nextDouble()-0.5)*0.45,x+12,30);
      cg.drawString(String.valueOf(c),x,32);
      cg.dispose();
      x+=26;
    }
    g.dispose();
    try{ByteArrayOutputStream out=new ByteArrayOutputStream();ImageIO.write(img,"png",out);return out.toByteArray();}
    catch(Exception e){throw new IllegalStateException(e);}
  }

  private Color gray(int lo,int hi){int v=lo+random.nextInt(hi-lo);return new Color(v,v,v);}
}
