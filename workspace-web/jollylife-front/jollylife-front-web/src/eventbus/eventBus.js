// src/eventbus/eventBus.js
import mitt from 'mitt'

// 创建事件总线实例
const emitter = mitt()

// 导出事件总线实例
export const mitter = emitter

// 导出常用方法（可选，方便使用）
export const emit = emitter.emit.bind(emitter)
export const on = emitter.on.bind(emitter) 
export const off = emitter.off.bind(emitter)
export const clear = emitter.all.clear.bind(emitter.all)

// 事件名称常量（可选，避免字符串拼写错误）
export const EVENT_NAMES = {
  VIDEO_UPLOAD_START: 'video:upload:start',
  VIDEO_UPLOAD_PROGRESS: 'video:upload:progress',
  VIDEO_UPLOAD_SUCCESS: 'video:upload:success',
  VIDEO_UPLOAD_ERROR: 'video:upload:error',
  USER_LOGIN: 'user:login',
  USER_LOGOUT: 'user:logout',
  NOTIFICATION_SHOW: 'notification:show',
  MODAL_OPEN: 'modal:open',
  MODAL_CLOSE: 'modal:close'
}

// 调试模式（开发环境下打印事件日志）
if (import.meta.env.DEV) {
  // 监听所有事件并打印日志
  emitter.on('*', (type, data) => {
    console.log(`[EventBus] Event: ${type}`, data)
  })
}

// 默认导出
export default mitter