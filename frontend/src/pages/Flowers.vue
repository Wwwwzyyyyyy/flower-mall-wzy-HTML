<script setup>
import {ref,onMounted} from 'vue'
import {api,run} from '../lib'
const flowers=ref([]),q=ref(''),detail=ref(null)
async function load(){flowers.value=await api.get('/flower-language/list',{params:{q:q.value}})}
async function show(id){detail.value=await api.get('/flower-language/'+id)}
onMounted(()=>run(load))
</script>
<template><section class="page-intro wrap"><span class="eyebrow">THE LANGUAGE OF FLOWERS</span><h1>读懂一朵花的心事。</h1><p>十种花卉，十种温柔的表达。花语随文化与地区而异，请结合对方喜好选择。</p></section><section class="wrap section"><form class="search-field flower-search" @submit.prevent="run(load)"><Icon name="search"/><input v-model="q" placeholder="搜索花卉名称" aria-label="搜索花卉"/><button class="text-button">搜索</button></form><div class="flower-grid"><button class="flower-card" v-for="f in flowers" :key="f.id" @click="run(()=>show(f.id))"><img :src="f.image" :alt="f.name+'花语插画示意'"/><div><span class="eyebrow">FLOWER No. {{String(f.id).padStart(2,'0')}}</span><h2>{{f.name}}</h2><p>{{f.meaning}}</p><span class="text-link">了解花语 <Icon name="arrow" :size="16"/></span></div></button></div><p v-if="!flowers.length" class="empty">未找到相关花卉。</p></section><el-dialog :model-value="!!detail" @close="detail=null" width="640px" align-center><template #header><h2>{{detail?.name}} · 花语札记</h2></template><div v-if="detail" class="flower-modal"><img :src="detail.image" alt="花卉插画示意"/><h3>{{detail.meaning}}</h3><h4>颜色与寓意</h4><p>{{detail.colors}}</p><h4>文化小记</h4><p>{{detail.culture}}</p><h4>适合的时刻</h4><p>{{detail.scenes}}</p><h4>相关花礼</h4><div v-for="p in detail.products" :key="p.id"><RouterLink :to="'/product/'+p.id" @click="detail=null" class="text-link">{{p.name}} <Icon name="arrow" :size="15"/></RouterLink></div></div></el-dialog></template>
