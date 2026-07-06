import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'layout',
      component: () => import('@/views/layout/Layout.vue'),
      children: [
        {
          path: '/',
          name: 'index',
          component: () => import('@/views/index/Index.vue'),
        },
        {
          path: '/v/:pCategoryCode',
          name: 'categoryVideo',
          component: () => import('@/views/videoList/CategoryVideo.vue'),
        },
        {
          path: '/v/:pCategoryCode/:categoryCode',
          name: 'subCategoryVideo',
          component: () => import('@/views/videoList/CategoryVideo.vue'),
        },
		{
          path: '/video/:videoId',
          name: 'videoDetail',
          component: () => import('@/views/videoDetail/VideoDetail.vue'),
        },
        {
          path: '/search',
          name: 'search',
          component: () => import('@/views/search/Search.vue'),
        },
        {
          path: '/message',
          name: 'message',
          component: () => import('@/views/ucenter/Message.vue'),
        },
        {
          path: '/history',
          name: 'history',
          component: () => import('@/views/ucenter/History.vue'),
        },
        {
          path: '/user/:userId/collection',
          name: 'userCollection',
          component: () => import('@/views/ucenter/Collection.vue'),
        },
        {
          path: '/user/:userId',
          name: 'userHome',
          component: () => import('@/views/userHome/UserHome.vue'),
        },
      ],
    },
    {
      path: '/ucenter',
      name: 'ucenter',
	  redirect:"/ucenter/home",
      component: () => import('@/views/ucenter/UcLayout.vue'),
	  children:[{
		path:'/ucenter/home',
		name:'用户中心首页',
		component:()=>import('@/views/ucenter/Home.vue')
	  },{
		path:'/ucenter/postVideo',
		name:'上传视频',
		component:()=>import('@/views/ucenter/postVideo/Post.vue')
	  },{
		path:'/ucenter/editVideo',
		name:'编辑视频',
		component:()=>import('@/views/ucenter/postVideo/Post.vue')
	  },{
		path:'/ucenter/video',
		name:'稿件管理',
		component:()=>import('@/views/ucenter/video/Video.vue')
	  },{
		path:'/ucenter/comment',
		name:'评论管理',
		component:()=>import('@/views/ucenter/comment/Comment.vue')
	  },{
		path:'/ucenter/danmu',
		name:'弹幕管理',
		component:()=>import('@/views/ucenter/danmu/Danmu.vue')
	  }]
    },
  ],
});

export default router;
