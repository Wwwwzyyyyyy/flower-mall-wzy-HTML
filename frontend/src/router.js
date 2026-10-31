import {createRouter,createWebHashHistory} from 'vue-router'
import {ElMessage} from 'element-plus'
const router=createRouter({history:createWebHashHistory(),scrollBehavior:()=>({top:0}),routes:[
 {path:'/',component:()=>import('./pages/Home.vue')},
 {path:'/shop',component:()=>import('./pages/Shop.vue')},
 {path:'/product/:id',component:()=>import('./pages/Product.vue')},
 {path:'/flowers',component:()=>import('./pages/Flowers.vue')},
 {path:'/ai',component:()=>import('./pages/Greeting.vue'),meta:{auth:true}},
 {path:'/cart',component:()=>import('./pages/Cart.vue'),meta:{auth:true}},
 {path:'/orders',component:()=>import('./pages/Orders.vue'),meta:{auth:true}},
 {path:'/center',component:()=>import('./pages/Center.vue'),meta:{auth:true}},
 {path:'/login',component:()=>import('./pages/Login.vue')},
 {path:'/admin/login',component:()=>import('./pages/Login.vue')},
 {path:'/admin/:section?',component:()=>import('./pages/Admin.vue'),meta:{admin:true}},
 {path:'/:pathMatch(.*)*',redirect:'/'}
]})
router.beforeEach(to=>{if(to.meta.auth&&!localStorage.getItem('userToken')){ElMessage.info('登录后继续');return{path:'/login',query:{redirect:to.fullPath}}}if(to.meta.admin&&!sessionStorage.getItem('adminToken'))return'/admin/login'})
export default router
