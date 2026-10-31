<script setup>
import {addCart,run,yuan} from '../lib'
defineProps({product:Object})
</script>
<template><article class="product-card"><RouterLink :to="'/product/'+product.id" class="product-image"><img :src="product.cover" :alt="product.name" loading="lazy"/><span class="image-label" v-if="product.stock===0">暂时售罄</span><span class="image-label" v-else-if="product.stock<=10">仅剩 {{product.stock}} 束</span><span class="image-label" v-else>当日新鲜 · 用心花艺</span><span class="image-arrow"><Icon name="arrow"/></span></RouterLink><div class="product-info"><span class="eyebrow">{{product.category_name||'FLEUR COLLECTION'}}</span><RouterLink :to="'/product/'+product.id"><h3>{{product.name}}</h3></RouterLink><p>{{product.specification}}</p><div class="price-row"><span class="price"><small>¥</small>{{yuan(product.price).split('.')[0]}}<small>.{{yuan(product.price).split('.')[1]}}</small></span><button class="round-button" :disabled="!product.stock||product.status===0" @click="run(()=>addCart(product))" :aria-label="'将'+product.name+'加入购物袋'"><Icon name="bag" :size="20"/></button></div></div></article></template>

