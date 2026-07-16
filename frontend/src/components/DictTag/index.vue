<template>
  <span v-for="(item, index) in options" :key="index">
    <el-tag
      v-if="item.dictValue == value"
      :type="item.elTagType || ''"
      :class="item.elTagClass || ''"
    >
      {{ item.dictLabel }}
    </el-tag>
  </span>
</template>

<script setup>
defineProps({
  options: { type: Array, default: () => [] },
  value: { type: [String, Number, Boolean], default: '' }
})
</script>
