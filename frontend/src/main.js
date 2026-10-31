import {createApp} from 'vue'
import {createPinia} from 'pinia'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import Icon from './components/Icon.vue'
import ProductCard from './components/ProductCard.vue'
import Pager from './components/Pager.vue'
const app=createApp(App)
app.use(createPinia()).use(router).use(ElementPlus,{locale:zhCn}).component('Icon',Icon).component('ProductCard',ProductCard).component('Pager',Pager).mount('#app')
