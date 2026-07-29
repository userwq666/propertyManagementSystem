import { ElMessage } from 'element-plus'
export async function batchDelete(deleteFn, ids, options = {}) {
  const { onProgress, onError, continueOnError = true } = options
  const results = { success: [], failed: [] }

  for (let i = 0; i < ids.length; i++) {
    const id = ids[i]
    try {
      await deleteFn(id)
      results.success.push(id)
      onProgress?.({ current: i + 1, total: ids.length, success: true, id })
    } catch (err) {
      results.failed.push({ id, error: err })
      onProgress?.({ current: i + 1, total: ids.length, success: false, id, error: err })
      if (!continueOnError) throw err
    }
  }

  if (results.failed.length > 0) {
    const msg = \批量删除完成：成功 \，失败 \\
    if (!continueOnError) throw new Error(msg)
    console.warn(msg)
  } else {
    ElMessage.success(\批量删除成功 \ 条\)
  }
  return results
}
