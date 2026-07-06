import { ElMessageBox } from 'element-plus'

const Confirm = {
    warning: (message, title = '提示') => {
        return ElMessageBox.confirm(message, title, {
            closeOnClickModal: false,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            showCancelButton: true,
            type: 'warning',
        })
    },
    info: (message, title = '提示') => {
        return ElMessageBox.confirm(message, title, {
            closeOnClickModal: false,
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            showCancelButton: true,
            type: 'info',
        })
    }
}

export default Confirm
