import { sources } from "@fingerprintjs/fingerprintjs";
import Request from "./Request";
//单服务版本
const Api = {
	sourcePath:"/api/file/getResource?sourceName=",
    checkCode: "/checkCode",
    login: "/login",
	uploadImage:"/file/uploadImage",
   
    loadCategory: "/category/loadCategory",
    saveCategory:"/category/saveCategory",
	deleteCategory: "/category/deleteCategory",
	changeCategorySort:"/category/changeSort",

	//视频
    loadVideo: "/videoInfo/loadVideoList",
    loadVideoPList: "/videoInfo/loadVideoPList",

	//互动
	loadComment:"/interact/loadComment",
    delComment: "/interact/delComment",
    //弹幕
    loadDanmu: "/interact/loadDanmu",
    delDanmu: "/interact/delDanmu",
	//用户管理
	loadUser:"/user/loadUser",
	changeStatus:"user/changeStatus",
	//视频资源
	getVideoResource:"api/file/videoResource",
	auditVideo:"videoInfo/auditVideo",
	deleteVideo:"videoInfo/deleteVideo",
	getSetting:"/setting/getSetting",
	saveSetting:"/setting/saveSetting",
	//首页统计数据
	getActualTimaStatisticsInfo:"index/getActualTimaStatisticsInfo",
	getWeekStatisticsInfo:"index/getWeekStatisticsInfo",
    
    //审核相关
    reviewList: "/video/review/list",
    reviewVideo: "/video/review",
    reviewDetail: "/video/review/detail",
    auditStats: "/video/stats",

    //视频管理相关
    videoManagementList: "/video/management/list",
    videoManagementRecommend: "/video/management/recommend",
    videoManagementDelete: "/video/management/delete",
    videoManagementStats: "/video/management/stats",
}

//上传封面
const uploadImage = async (file, createThumbnail = false) => {
    let result = await Request({
        url: Api.uploadImage,
        params: {
            file,
            createThumbnail
        },
    })
    if (!result) {
        return;
    }
    return result.data;
}

//获取复核列表
const reviewListApi = async (params) => {
    let result = await Request({
        url: Api.reviewList,
        params: params,
    })
    return result;
}

//人工复核
const reviewVideoApi = async (params) => {
    let result = await Request({
        url: Api.reviewVideo,
        params: params,
    })
    return result;
}

//获取审核统计
const auditStatsApi = async () => {
    let result = await Request({
        url: Api.auditStats,
    })
    return result;
}

//获取复核视频详情
const reviewDetailApi = async (params) => {
    let result = await Request({
        url: Api.reviewDetail,
        params: params,
    })
    return result;
}

//获取视频管理列表
const videoManagementListApi = async (params) => {
    let result = await Request({
        url: Api.videoManagementList,
        params: params,
    })
    return result;
}

//设置视频推荐状态
const videoManagementRecommendApi = async (params) => {
    let result = await Request({
        url: Api.videoManagementRecommend,
        params: params,
    })
    return result;
}

//删除视频（管理员）
const videoManagementDeleteApi = async (params) => {
    let result = await Request({
        url: Api.videoManagementDelete,
        params: params,
    })
    return result;
}

//获取视频统计数据
const videoManagementStatsApi = async () => {
    let result = await Request({
        url: Api.videoManagementStats,
    })
    return result;
}

//获取评论列表
const loadCommentApi = async (params) => {
    let result = await Request({
        url: Api.loadComment,
        params: params,
    })
    return result;
}

//删除评论
const delCommentApi = async (params) => {
    let result = await Request({
        url: Api.delComment,
        params: params,
    })
    return result;
}

//获取弹幕列表
const loadDanmuApi = async (params) => {
    let result = await Request({
        url: Api.loadDanmu,
        params: params,
    })
    return result;
}

//删除弹幕
const delDanmuApi = async (params) => {
    let result = await Request({
        url: Api.delDanmu,
        params: params,
    })
    return result;
}

//获取用户列表
const loadUserApi = async (params) => {
    let result = await Request({
        url: Api.loadUser,
        params: params,
    })
    return result;
}

//修改用户状态
const changeUserStatusApi = async (params) => {
    let result = await Request({
        url: Api.changeStatus,
        params: params,
    })
    return result;
}

export {
    Api,
    uploadImage,
    reviewListApi,
    reviewVideoApi,
    auditStatsApi,
    reviewDetailApi,
    videoManagementListApi,
    videoManagementRecommendApi,
    videoManagementDeleteApi,
    videoManagementStatsApi,
    loadCommentApi,
    delCommentApi,
    loadDanmuApi,
    delDanmuApi,
    loadUserApi,
    changeUserStatusApi,
}
