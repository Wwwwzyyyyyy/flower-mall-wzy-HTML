package cn.flower.mall;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication @EnableScheduling
public class FlowerApplication {
  public static void main(String[] args) { SpringApplication.run(FlowerApplication.class, args); }
}
