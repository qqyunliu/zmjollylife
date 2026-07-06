import { time } from "echarts";

import moment from "moment";
import 'moment/locale/zh-cn'; // 导入中文本地化

moment.locale('zh-cn'); // 设置为中文


const getLocalImage=(image)=>{
    return new URL('../assets/${image}', import.meta.url).href;
};
const formatDate=(timestamp)=>{
    if(!timestamp) return '';
    
    // 修复时间格式：将 "16-14-22" 转换为 "16:14:22"
    const fixedTimestamp = timestamp.replace(/-(\d{2})-(\d{2})$/, ':$1:$2');
    
    const timestampTime=moment(fixedTimestamp);
    
    // 修复：YYYYMDD -> YYYYMMDD
    const days=Number.parseInt(moment().format("YYYYMMDD"))-Number.parseInt(timestampTime.format("YYYYMMDD"));
    
    if(days==0){
        return timestampTime.format("HH:mm");
    }else if (days==1){
        return "昨天";
    }else if(days>=2&&days<7){
        return timestampTime.format("dddd")
    }
    else if(days>=7){
        return timestampTime.format("YYYY-MM-DD")
    }
}


const convertSecondsToHMS = (seconds) => {
  var hours = Math.floor(seconds / 3600);
  var minutes = Math.floor((seconds % 3600) / 60);
  var remainingSeconds = seconds % 60;
  return (hours === 0 ? "" : hours.toString().padStart(2, '0') + ":") + minutes.toString().padStart(2, '0') + ":" + remainingSeconds.toString().padStart(2, '0');
};

const getFileName=(fileName)=>{
	if(!fileName){
		return fileName;
	}
	return fileName.lastIndexOf(".")==-1 ? fileName:fileName.substring(0,fileName.lastIndexOf("."));

}

const size2Str = (limit) => {
  var size="";
  // 获取小数点后两位
  if (limit < 0.1 * 1024) {
    size = 0; // 小于 0.1KB，则转化成 0
  } else if (limit < 1024) {
    size = limit.toFixed(2) + "KB"; // 小于 1MB，则转化成 KB
  } else if (limit < 1024 * 1024) {
    size = (limit / 1024).toFixed(2) + "MB"; // 小于 1GB，则转化成 MB
  } else if (limit < 1024 * 1024 * 1024) {
    size = (limit / (1024 * 1024)).toFixed(2) + "GB"; // 其他转化成 GB
  } else {
    size = (limit / (1024 * 1024 * 1024)).toFixed(2) + "GB";
  }
  var sizeStr = size + ""; // 转换成字符串
  var index = sizeStr.indexOf("."); // 获取小数点处的索引
  var dou = sizeStr.substr(index + 1, 2); // 获取小数点后两位的值
  if (dou == "00") {
    // 判断后两位是否为 00，如果是则删除 00
    return sizeStr.substring(0, index) + sizeStr.substr(index + 3, 2);
  }
  return size;
};
export default{
    getLocalImage,
	formatDate,
	getFileName,
	size2Str,
	convertSecondsToHMS
}