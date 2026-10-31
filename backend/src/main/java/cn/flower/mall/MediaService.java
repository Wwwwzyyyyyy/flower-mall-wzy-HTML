package cn.flower.mall;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;
import static cn.flower.mall.Store.m;
import static cn.flower.mall.MallService.*;

@Service
class MediaService {
 final Store db; final Path root;
 MediaService(Store db,@Value("${app.upload-dir}")String root){this.db=db;this.root=Path.of(root).toAbsolutePath().normalize();ImageIO.scanForPlugins();}
 @Transactional public Object upload(long uid,String purpose,MultipartFile file){
   check(Set.of("product","review","avatar").contains(purpose),"图片用途无效");check(!file.isEmpty()&&file.getSize()<=5*1024*1024,"图片为空或超过5MB");Path target=null;
   try(ImageInputStream input=ImageIO.createImageInputStream(file.getInputStream())){
     var readers=ImageIO.getImageReaders(input);check(readers.hasNext(),"文件并非有效图片");var reader=readers.next();BufferedImage original;
     try{String format=reader.getFormatName().toLowerCase(Locale.ROOT);check(Set.of("jpeg","jpg","png","webp").contains(format),"仅支持JPG、PNG、WEBP图片");reader.setInput(input);int width=reader.getWidth(0),height=reader.getHeight(0);check(width>0&&height>0&&(long)width*height<=30000000,"图片像素过大，最多3000万像素");original=reader.read(0);}finally{reader.dispose();}
     check(original!=null,"图片解码失败");double scale=Math.min(1,2000.0/Math.max(original.getWidth(),original.getHeight()));int w=Math.max(1,(int)(original.getWidth()*scale)),h=Math.max(1,(int)(original.getHeight()*scale));var normalized=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);var g=normalized.createGraphics();g.setColor(Color.WHITE);g.fillRect(0,0,w,h);g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);g.drawImage(original,0,0,w,h,null);g.dispose();
     String dir=switch(purpose){case "product"->"products/detail";case "review"->"reviews/images";default->"avatars";};String name=UUID.randomUUID()+".jpg";target=root.resolve(dir).resolve(name);Files.createDirectories(target.getParent());ImageIO.write(normalized,"jpg",target.toFile());String url="/uploads/"+dir+"/"+name;db.add("uploaded_file",m("owner_id",uid,"purpose",purpose,"url",url));Path written=target;TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){public void afterCompletion(int status){if(status!=STATUS_COMMITTED)try{Files.deleteIfExists(written);}catch(IOException ignored){}}});return m("url",url);
   }catch(BusinessException e){throw e;}catch(Exception e){if(target!=null)try{Files.deleteIfExists(target);}catch(IOException ignored){}throw new BusinessException("图片保存失败，请检查图片及存储目录");}
 }
 @Transactional public void delete(long uid,long fid,boolean admin){var f=db.one("SELECT * FROM uploaded_file WHERE id=#{p.id}",m("id",fid));check(f!=null,"图片不存在");check(admin||id(f.get("owner_id"))==uid,"无权删除图片");String url=f.get("url").toString();var counts=db.one("SELECT (SELECT COUNT(*) FROM product WHERE cover=#{p.url})+(SELECT COUNT(*) FROM product_image WHERE image_url=#{p.url})+(SELECT COUNT(*) FROM order_item WHERE cover=#{p.url})+(SELECT COUNT(*) FROM review_image WHERE image_url=#{p.url})+(SELECT COUNT(*) FROM account WHERE avatar=#{p.url}) AS n",m("url",url));check(id(counts.get("n"))==0,"图片正在被商品、订单、头像或评价引用，需保留");db.exec("DELETE FROM uploaded_file WHERE id=#{p.id}",m("id",fid));Path target=root.resolve(url.substring("/uploads/".length())).normalize();check(target.startsWith(root),"非法路径");TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){public void afterCommit(){try{Files.deleteIfExists(target);}catch(IOException e){org.slf4j.LoggerFactory.getLogger(MediaService.class).warn("Unable to remove unused image {}",target);}}});}
}
