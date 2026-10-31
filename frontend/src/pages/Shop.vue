<script setup>
import {ref,onMounted,watch} from 'vue'
import {useRoute,useRouter} from 'vue-router'
import {api,run} from '../lib'
const route=useRoute(),router=useRouter(),data=ref({items:[],total:0}),categories=ref([]),q=ref(route.query.q||''),cat=ref(Number(route.query.category)||0),page=ref(1),loading=ref(false)
async function load(){loading.value=true;try{data.value=await api.get('/product/list',{params:{q:q.value,category:cat.value,page:page.value,size:8}})}finally{loading.value=false}}
function filter(){page.value=1;router.replace({query:{q:q.value||undefined,category:cat.value||undefined}});run(load)}
onMounted(()=>run(async()=>{categories.value=await api.get('/category/list');await load()}))
watch(()=>route.query,()=>{q.value=route.query.q||'';cat.value=Number(route.query.category)||0;page.value=1;run(load)})
</script>
<template><section class="page-intro wrap"><span class="eyebrow">THE FLOWER COLLECTION</span><h1>用一束花，表达心意。</h1><p>为爱的人，也为认真生活的自己。</p></section><section class="wrap section"><div class="shop-toolbar"><div class="filter-chips"><button :class="{active:cat===0}" @click="cat=0;filter()">全部鲜花</button><button v-for="c in categories" :key="c.id" :class="{active:cat===c.id}" @click="cat=c.id;filter()">{{c.name}}</button></div><form class="search-field" @submit.prevent="filter"><Icon name="search" :size="17"/><input v-model="q" placeholder="搜索鲜花、心意…" aria-label="搜索商品"/><button class="text-button">搜索</button></form></div><div class="list-caption"><span>共 {{data.total}} 款鲜花</span><span>本地插画为演示示意，支持商家上传商品实拍</span></div><div class="product-grid" v-loading="loading"><ProductCard v-for="p in data.items" :key="p.id" :product="p"/></div><div v-if="!loading&&!data.items.length" class="empty"><Icon name="flower" :size="48"/><h3>这份心意还在准备中</h3><p>换个关键词，或看看其他分类。</p></div><Pager :page="page" :total="data.total" :size="8" @change="v=>{page=v;run(load)}"/></section></template>
