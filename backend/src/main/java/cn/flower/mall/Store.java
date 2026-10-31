package cn.flower.mall;

import java.util.*;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
interface SqlMapper {
  @SelectProvider(type=Store.SqlText.class, method="provide") List<Map<String,Object>> query(@Param("sql") String sql, @Param("p") Map<String,Object> p);
  @UpdateProvider(type=Store.SqlText.class, method="provide") int execute(@Param("sql") String sql, @Param("p") Map<String,Object> p);
  @InsertProvider(type=Store.SqlText.class, method="provide") @Options(useGeneratedKeys=true, keyProperty="p.id")
  int insert(@Param("sql") String sql, @Param("p") Map<String,Object> p);
}


/** SQL text originates only from server constants; all request values use MyBatis bindings. */
@Repository
public class Store {
  public static class SqlText { public static String provide(Map<String,Object> args) { return (String)args.get("sql"); } }
  private final SqlMapper mapper;
  public Store(SqlMapper mapper) {this.mapper=mapper;}
  public static Map<String,Object> m(Object... a) {var m=new LinkedHashMap<String,Object>(); for(int i=0;i<a.length;i+=2)m.put((String)a[i],a[i+1]); return m;}
  public List<Map<String,Object>> list(String sql, Map<String,Object> p) {return mapper.query(sql,p);}
  public Map<String,Object> one(String sql, Map<String,Object> p) {var l=list(sql,p);return l.isEmpty()?null:l.get(0);}
  public int exec(String sql, Map<String,Object> p) {return mapper.execute(sql,p);}
  private static final Set<String> TABLES=Set.of("account","category","product","product_image","flower_language","product_flower_language","cart","address","shop_order","order_item","greeting","payment_record","favorite","refund_record","stock_record","review","review_image","review_reply","uploaded_file","audit_log","review_report");
  public long add(String table, Map<String,Object> row) {
    if(!TABLES.contains(table))throw new IllegalArgumentException();
    for(String key:row.keySet())if(!key.matches("[a-z_]+"))throw new IllegalArgumentException();
    String cols=String.join(",",row.keySet());
    String vals=String.join(",",row.keySet().stream().map(k->"#{p."+k+"}").toList());
    mapper.insert("INSERT INTO "+table+" ("+cols+") VALUES ("+vals+")",row);
    return ((Number)row.get("id")).longValue();
  }
  public void update(String table,long id,Map<String,Object> row) {
    if(!TABLES.contains(table)||row.isEmpty())throw new IllegalArgumentException();
    for(String key:row.keySet())if(!key.matches("[a-z_]+"))throw new IllegalArgumentException();
    String sets=String.join(",",row.keySet().stream().map(k->k+"=#{p."+k+"}").toList());
    var p=new HashMap<>(row);p.put("_id",id);exec("UPDATE "+table+" SET "+sets+" WHERE id=#{p._id}",p);
  }
}

