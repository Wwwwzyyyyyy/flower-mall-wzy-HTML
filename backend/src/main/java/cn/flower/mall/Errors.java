package cn.flower.mall;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import static cn.flower.mall.Store.m;
@RestControllerAdvice
class Errors {
 @ExceptionHandler(BusinessException.class) ResponseEntity<?> business(BusinessException e){return ResponseEntity.status(e.status).body(m("message",e.getMessage()));}
 @ExceptionHandler({IllegalArgumentException.class,org.springframework.http.converter.HttpMessageNotReadableException.class}) ResponseEntity<?> invalid(Exception e){return ResponseEntity.badRequest().body(m("message","参数格式不正确"));}
 @ExceptionHandler(DataIntegrityViolationException.class) ResponseEntity<?> conflict(Exception e){return ResponseEntity.status(409).body(m("message","数据重复或关联记录不存在，请刷新后重试"));}
 @ExceptionHandler(MaxUploadSizeExceededException.class) ResponseEntity<?> size(Exception e){return ResponseEntity.badRequest().body(m("message","单张图片不能超过5MB"));}
 @ExceptionHandler(Exception.class) ResponseEntity<?> unexpected(Exception e){org.slf4j.LoggerFactory.getLogger(Errors.class).error("Request failed",e);return ResponseEntity.internalServerError().body(m("message","服务处理失败，请稍后重试"));}
}
