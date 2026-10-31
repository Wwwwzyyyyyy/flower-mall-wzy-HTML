import axios from 'axios'
import {defineStore} from 'pinia'
import {ElMessage} from 'element-plus'
export const api=axios.create({baseURL:'/api',timeout:35000})
const store=role=>role==='admin'?sessionStorage:localStorage
api.interceptors.request.use(c=>{let role=c.url.startsWith('/admin/')?'admin':'user';let t=store(role).getItem(role+'Token');if(t)c.headers.Authorization='Bearer '+t;return c})
api.interceptors.response.use(r=>r.data,e=>{const message=e.response?.data?.message||'连接失败，请确认后端已启动';ElMessage.error(message);if(e.response?.status===401){let role=e.config?.url?.startsWith('/admin/')?'admin':'user';store(role).removeItem(role+'Token');if(role==='admin'&&location.hash.startsWith('#/admin')&&!location.hash.startsWith('#/admin/login'))location.hash='/admin/login'}return Promise.reject(e)})
export const useAccount=defineStore('account',{state:()=>({user:JSON.parse(localStorage.getItem('userProfile')||'null'),admin:JSON.parse(sessionStorage.getItem('adminProfile')||'null'),cartCount:0}),actions:{save(role,data){store(role).setItem(role+'Token',data.token);store(role).setItem(role+'Profile',JSON.stringify(data.user));this[role]=data.user},logout(role='user'){store(role).removeItem(role+'Token');store(role).removeItem(role+'Profile');this[role]=null;this.cartCount=0},async refreshCart(){if(!localStorage.getItem('userToken'))return;try{let items=await api.get('/cart/list');this.cartCount=items.reduce((a,i)=>a+i.quantity,0)}catch{this.cartCount=0}}}})
export const yuan=v=>Number(v||0).toFixed(2)
export const date=v=>v?String(v).replace('T',' ').slice(0,19):'—'
export const statuses=['待支付','待商家接单','已接单','制作中','配送中','已完成','已取消','已拒绝']
export const refundStatuses=['','待审核','退款处理中','退款成功','已拒绝','退款失败']
export const notify=message=>ElMessage.success(message)
export async function upload(file,purpose){if(!['image/jpeg','image/png','image/webp'].includes(file.type)||file.size>5*1024*1024){ElMessage.error('请选择5MB以内的JPG、PNG或WEBP图片');throw Error('invalid image')}let form=new FormData();form.append('file',file);let path=purpose==='product'?'/admin/product/image/upload':purpose==='avatar'?'/user/avatar/upload':'/review/image/upload';return api.post(path,form)}
export async function addCart(p,quantity=1){if(!localStorage.getItem('userToken')){ElMessage.info('请先登录后再加入购物袋');location.hash='/login';return}await api.post('/cart/add',{product_id:p.id,quantity});notify('已加入购物袋');await useAccount().refreshCart()}
export function rememberGreeting(content){localStorage.setItem('orderGreeting',content);notify('已保存到花卡，结算时将自动填入')}
export const run=fn=>Promise.resolve().then(fn).catch(()=>{})
