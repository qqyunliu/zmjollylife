import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
	{
      path: '/login',
      name: 'login',
       component: () => import('../views/account/Account.vue'),
    },
    {
      path: '/',
      name: 'layout',
	  redirect:'/login',
       component: () => import('../views/layout/Layout.vue'),
	   children:[{
		path:'/home',
		name:'首页',
		component:()=> import('../views/home/Home.vue'),
	   },
	   {
		path:'/content/video-audit',
		name:'视频审核',
		component:()=> import('../views/content/VideoAudit.vue'),
	   },
	   {
		path:'/content/video',
		name:'视频管理',
		component:()=> import('../views/content/VideoManagement.vue'),
	   },
	   {
		path:'/interact/comment',
		name:'评论管理',
		component:()=> import('../views/content/CommentManagement.vue'),
	   },
	   {
		path:'/interact/delDanmu',
		name:'弹幕管理',
		component:()=> import('../views/content/DanmuManagement.vue'),
	   },
	   {
		path:'/user/userList',
		name:'用户管理',
		component:()=> import('../views/content/UserManagement.vue'),
	   }]
    }
  ]
})

export default router
