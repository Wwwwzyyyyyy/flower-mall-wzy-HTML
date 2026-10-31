-- MySQL 8.0：在schema.sql建表之后执行一次。再次执行会提示索引已存在。
CREATE INDEX idx_product_status_category ON product(deleted,status,category_id);
CREATE INDEX idx_order_user_status_time ON shop_order(user_id,status,create_time);
CREATE INDEX idx_order_status_time ON shop_order(status,create_time);
CREATE INDEX idx_payment_time ON payment_record(payment_status,pay_time);
CREATE INDEX idx_refund_order_status ON refund_record(order_id,refund_status);
CREATE INDEX idx_refund_status_time ON refund_record(refund_status,refund_time);
CREATE INDEX idx_stock_product_time ON stock_record(product_id,create_time);
CREATE INDEX idx_review_product_status ON review(product_id,status,deleted);
CREATE INDEX idx_review_user ON review(user_id,deleted);
CREATE INDEX idx_address_user ON address(user_id,is_default);
CREATE INDEX idx_greeting_user_time ON greeting(user_id,create_time);
CREATE INDEX idx_upload_owner ON uploaded_file(owner_id,purpose);
