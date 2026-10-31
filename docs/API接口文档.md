# API接口文档

基础路径 `/api`。成功直接返回JSON对象、数组或分页对象 `{items,total,page,size}`；失败HTTP状态码400/401/403/404/409/429/500，内容 `{message:"错误原因"}`。普通列表默认page=1、size=10（商品默认12），后端最大每页50；花语、购物袋与地址为有业务数量限制的小列表。

鉴权请求头 `Authorization: Bearer <token>`。用户和商家独立登录获取令牌；普通用户不能访问 `/admin/**`，商家不能执行普通用户购物接口。Key、密码哈希与token_version不会返回到用户管理接口。

公开接口：health、注册/登录、GET product、category、flower-language、review/product。其余需要对应角色登录。ID若不属于本人返回403或404。

## 核心请求例子

```json
POST /api/user/register
{"username":"flower_user","password":"Demo123!","nickname":"小花"}

POST /api/user/login
{"username":"flower_user","password":"Demo123!"}

POST /api/address/add
{"receiver":"示例姓名","phone":"13800000000","province":"浙江省","city":"杭州市","district":"西湖区","detail":"示例街道1号","is_default":1}

POST /api/cart/add
{"product_id":1,"quantity":1}

POST /api/order/create
{"request_key":"每次结算生成的唯一UUID","address_id":1,"items":[{"product_id":1,"quantity":1}],"greeting":"愿你每天都有好心情","remark":"示例备注"}

POST /api/payment/mock/1
{}

POST /api/refund/apply
{"order_id":1,"amount":"50.00","reason":"购买错误","remark":"申请部分模拟退款"}

PUT /api/admin/refund/approve/1
{}

PUT /api/admin/refund/reject/1
{"reason":"已完成制作，请沟通履约情况"}

POST /api/review/add
{"order_item_id":1,"rating":5,"content":"花很漂亮","images":["/uploads/reviews/images/实际已上传的文件.jpg"]}

POST /api/review/1/report
{"reason":"请填写具体的违规处理依据"}

POST /api/ai/greeting/generate
{"scene":"生日祝福","receiver":"好朋友","style":"温柔浪漫","extra":"毕业后第一次相聚"}

PUT /api/admin/stock/add
{"product_id":1,"quantity":10,"remark":"花材补货"}

POST /api/admin/product
{"category_id":1,"name":"心意花束","price":"199.00","description":"花束介绍","specification":"19枝玫瑰","cover":"/images/bouquet-1.svg","images":[],"stock":20,"warning_stock":10,"status":1}
```

上传接口为multipart/form-data，字段名 `file`，返回 `{url}`。URL必须与当前用户/用途的上传记录匹配。商品封面、详情数组只接受安全的本地演示图或本商家上传图。修改商品使用相同字段但路径为PUT /admin/product/{id}；已有商品stock字段被忽略，改库存必须走库存接口。

订单action：accept/reject/status；status请求体 `{status:3}`，仅允许严格相邻状态。reject需要 `{reason}`。支付失败请使用独立 `/payment/mock/{id}/fail`，不能传入任意业务状态。

常用查询参数：商品 `q,category,page,size`；订单 `q,status(-1表示全部),page,size`；评价 `q,rating(0表示全部),page,size`；用户 `q,page,size`；账单 `start,end(yyyy-MM-dd),page,size`。开始与结束日期均包含当天。

AI返回 `{id,content,source}`，source为AI或TEMPLATE。每用户3秒冷却，AI网络超时25秒。设置了Key但外部服务失败时返回明确错误，不会无提示改用模板。

## 接口清单

表中路径省略共同前缀 `/api`；花括号内`action:...`为可选路径段约束。

| 方法 | 路径 | 代码处理方法 |
|---|---|---|
| GET | `/health` | health |
| POST | `/user/register` | register |
| POST | `/user/login` | login |
| POST | `/admin/login` | adminLogin |
| GET | `/user/info` | info |
| PUT | `/user/update` | profile |
| PUT | `/user/password` | password |
| GET | `/category/list` | categories |
| GET | `/product/list` | products |
| GET | `/product/search` | products |
| GET | `/product/{id}` | product |
| GET | `/product/{id}/images` | images |
| GET | `/flower-language/list` | flowers |
| GET | `/flower-language/{id}` | flower |
| GET | `/cart/list` | cart |
| POST | `/cart/add` | addCart |
| PUT | `/cart/update` | updateCart |
| DELETE | `/cart/{id}` | deleteCart |
| GET | `/address/list` | addresses |
| POST | `/address/add` | addAddress |
| PUT | `/address/update` | updateAddress |
| DELETE | `/address/{id}` | deleteAddress |
| GET | `/favorite/list` | favorites |
| POST | `/favorite/{id}` | favorite |
| POST | `/order/create` | createOrder |
| GET | `/order/list` | orders |
| GET | `/order/{id}` | order |
| GET | `/order/status/{id}` | order |
| PUT | `/order/cancel/{id}` | cancel |
| POST | `/payment/mock/{id}` | pay |
| POST | `/payment/mock/{id}/fail` | failPay |
| GET | `/payment/{id}` | payment |
| POST | `/ai/greeting/generate` | generate |
| GET | `/ai/greeting/list` | greetings |
| GET | `/ai/greeting/{id}` | greeting |
| DELETE | `/ai/greeting/{id}` | deleteGreeting |
| POST | `/review/image/upload` | uploadReview |
| POST | `/user/avatar/upload` | uploadAvatar |
| DELETE | `/media/{id}` | deleteMedia |
| POST | `/review/add` | addReview |
| POST | `/review/{id}/report` | report |
| PUT | `/review/{id}` | updateReview |
| DELETE | `/review/{id}` | deleteReview |
| GET | `/review/product/{id}` | productReviews |
| GET | `/review/my` | myReviews |
| GET | `/review/{id}` | review |
| POST | `/refund/apply` | refundApply |
| GET | `/refund/my` | myRefunds |
| GET | `/refund/{id}` | refund |
| GET | `/admin/products` | adminProducts |
| GET | `/admin/product/{id}` | adminProduct |
| POST | `/admin/product` | addProduct |
| PUT | `/admin/product/{id}` | saveProduct |
| PUT | `/admin/product/{id}/status` | productStatus |
| DELETE | `/admin/product/{id}` | deleteProduct |
| POST | `/admin/product/image/upload` | uploadProduct |
| DELETE | `/admin/media/{id}` | adminDeleteMedia |
| DELETE | `/admin/product/image/{id}` | deleteProductImage |
| PUT | `/admin/product/image/sort` | sortImages |
| GET | `/admin/stock/list` | stockList |
| GET | `/admin/stock/warning` | stockWarning |
| GET | `/admin/stock/{id}` | stockDetail |
| PUT | `/admin/stock/{type:add\|reduce\|adjust}` | stock |
| GET | `/admin/stock/records` | stockRecords |
| GET | `/admin/orders` | adminOrders |
| GET | `/admin/order/{id}` | adminOrder |
| PUT | `/admin/order/{action:accept\|reject\|status}/{id}` | orderAction |
| GET | `/admin/refunds` | adminRefunds |
| GET | `/admin/refund/{id}` | adminRefund |
| PUT | `/admin/refund/{action:approve\|reject}/{id}` | auditRefund |
| GET | `/admin/reviews` | adminReviews |
| GET | `/admin/review/{id}` | adminReview |
| POST | `/admin/review/{id}/reply` | reply |
| PUT | `/admin/review/{id}/status` | moderate |
| GET | `/admin/statistics/overview` | overview |
| GET | `/admin/statistics/trend` | trend |
| GET | `/admin/statistics/revenue` | revenue |
| GET | `/admin/users` | users |
| GET | `/admin/user/{id}` | adminUser |
| PUT | `/admin/user/status/{id}` | userStatus |
| GET | `/admin/audit-logs` | logs |
| GET | `/admin/media` | mediaList |

## 状态及安全

订单0待支付、1待接单、2已接单、3制作中、4配送中、5完成、6取消、7拒单。支付状态独立：0未成功、1模拟成功。退款1待审、2处理中、3成功、4拒绝、5失败。

JWT有效期24小时，修改密码/禁用或恢复会更新token_version使旧令牌失效。接口不接收客户端传入的user_id作为归属依据。登录接口按IP与账号限制10分钟20次尝试。所有SQL值使用MyBatis参数绑定；SQL文本与表名来自服务端代码常量。

图片删除 `/admin/media/{id}` 中的id是uploaded_file记录ID；`/admin/product/image/{id}`中id是product_image记录ID，两者不可混淆。删除仍被历史订单引用的文件会被拒绝。评价状态审核 `{status:0或1,reason:"具体依据"}`，商家回复 `{content:"回复正文"}`。

分页查询无数据返回空items数组。无支付记录时查询支付结果可能返回空响应。接口正常目标为2秒内，不含AI；本版测试是功能集成验证，未将该目标作为负载SLA宣称。
