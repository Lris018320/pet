import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/front/Login.vue'),
    meta: { title: '登录', guest: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/front/Register.vue'),
    meta: { title: '注册', guest: true }
  },
  {
    path: '/',
    component: () => import('@/views/front/Layout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/front/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'recognition',
        name: 'Recognition',
        component: () => import('@/views/front/Recognition.vue'),
        meta: { title: 'AI 识别' }
      },
      {
        path: 'chat',
        name: 'Chat',
        component: () => import('@/views/front/Chat.vue'),
        meta: { title: 'AI 问答', requireAuth: true }
      },
      {
        path: 'archive',
        name: 'ArchiveList',
        component: () => import('@/views/front/ArchiveList.vue'),
        meta: { title: '我的档案', requireAuth: true }
      },
      {
        path: 'archive/:id',
        name: 'ArchiveDetail',
        component: () => import('@/views/front/ArchiveDetail.vue'),
        meta: { title: '档案详情', requireAuth: true }
      },
      {
        path: 'info',
        name: 'InfoList',
        component: () => import('@/views/front/InfoList.vue'),
        meta: { title: '宠物资讯', public: true }
      },
      {
        path: 'info/:id',
        name: 'InfoDetail',
        component: () => import('@/views/front/InfoDetail.vue'),
        meta: { title: '资讯详情', public: true }
      },
      {
        path: 'breed',
        name: 'BreedList',
        component: () => import('@/views/front/BreedList.vue'),
        meta: { title: '品种科普', public: true }
      },
      {
        path: 'breed/:id',
        name: 'BreedDetail',
        component: () => import('@/views/front/BreedDetail.vue'),
        meta: { title: '品种详情', public: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/front/Profile.vue'),
        meta: { title: '个人中心', requireAuth: true }
      },
      {
        path: 'my-records',
        name: 'MyRecords',
        component: () => import('@/views/front/MyRecords.vue'),
        meta: { title: '我的记录', requireAuth: true }
      },
      {
        path: 'adoption',
        name: 'AdoptionList',
        component: () => import('@/views/front/AdoptionList.vue'),
        meta: { title: '领养寄养', public: true }
      },
      {
        path: 'adoption/publish',
        name: 'AdoptionPublish',
        component: () => import('@/views/front/AdoptionPublish.vue'),
        meta: { title: '发布领养寄养', requireAuth: true }
      },
      {
        path: 'adoption/:id',
        name: 'AdoptionDetail',
        component: () => import('@/views/front/AdoptionDetail.vue'),
        meta: { title: '领养寄养详情', public: true }
      },
      {
        path: 'community',
        name: 'CommunityList',
        component: () => import('@/views/front/CommunityList.vue'),
        meta: { title: '宠物社区', public: true }
      },
      {
        path: 'community/publish',
        name: 'PostPublish',
        component: () => import('@/views/front/PostPublish.vue'),
        meta: { title: '发帖', requireAuth: true }
      },
      {
        path: 'community/:id',
        name: 'CommunityDetail',
        component: () => import('@/views/front/CommunityDetail.vue'),
        meta: { title: '帖子详情', public: true }
      },
      {
        path: 'mall',
        name: 'MallList',
        component: () => import('@/views/front/MallList.vue'),
        meta: { title: '宠物商城', public: true }
      },
      {
        path: 'mall/:id',
        name: 'MallDetail',
        component: () => import('@/views/front/MallDetail.vue'),
        meta: { title: '商品详情', public: true }
      },
      {
        path: 'cart',
        name: 'Cart',
        component: () => import('@/views/front/Cart.vue'),
        meta: { title: '购物车', requireAuth: true }
      },
      {
        path: 'checkout',
        name: 'Checkout',
        component: () => import('@/views/front/Checkout.vue'),
        meta: { title: '确认订单', requireAuth: true }
      },
      {
        path: 'order',
        name: 'OrderList',
        component: () => import('@/views/front/OrderList.vue'),
        meta: { title: '我的订单', requireAuth: true }
      },
      {
        path: 'order/:id',
        name: 'OrderDetail',
        component: () => import('@/views/front/OrderDetail.vue'),
        meta: { title: '订单详情', requireAuth: true }
      }
    ]
  },
  // ====== 管理后台路由 ======
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: () => import('@/views/admin/AdminLogin.vue'),
    meta: { title: '管理员登录', guest: true }
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requireAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/AdminDashboard.vue'),
        meta: { title: '数据看板', requireAdmin: true }
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('@/views/admin/AdminUser.vue'),
        meta: { title: '用户管理', requireAdmin: true }
      },
      {
        path: 'pet',
        name: 'AdminPet',
        component: () => import('@/views/admin/AdminPet.vue'),
        meta: { title: '宠物管理', requireAdmin: true }
      },
      {
        path: 'info',
        name: 'AdminInfo',
        component: () => import('@/views/admin/AdminInfo.vue'),
        meta: { title: '资讯管理', requireAdmin: true }
      },
      {
        path: 'knowledge',
        name: 'AdminKnowledge',
        component: () => import('@/views/admin/AdminKnowledge.vue'),
        meta: { title: '知识记录', requireAdmin: true }
      },
      {
        path: 'recognition',
        name: 'AdminRecognition',
        component: () => import('@/views/admin/AdminRecognition.vue'),
        meta: { title: '识别记录', requireAdmin: true }
      },
      {
        path: 'adoption',
        name: 'AdminAdoption',
        component: () => import('@/views/admin/AdminAdoption.vue'),
        meta: { title: '领养寄养管理', requireAdmin: true }
      },
      {
        path: 'community',
        name: 'AdminCommunity',
        component: () => import('@/views/admin/AdminCommunity.vue'),
        meta: { title: '社区管理', requireAdmin: true }
      },
      {
        path: 'product',
        name: 'AdminProduct',
        component: () => import('@/views/admin/AdminProduct.vue'),
        meta: { title: '商品管理', requireAdmin: true }
      },
      {
        path: 'order',
        name: 'AdminOrder',
        component: () => import('@/views/admin/AdminOrder.vue'),
        meta: { title: '订单管理', requireAdmin: true }
      }
    ]
  },
  // ====== 商家端路由 ======
  {
    path: '/merchant/login',
    name: 'MerchantLogin',
    component: () => import('@/views/merchant/MerchantLogin.vue'),
    meta: { title: '商家登录', guest: true }
  },
  {
    path: '/merchant',
    component: () => import('@/views/merchant/MerchantLayout.vue'),
    redirect: '/merchant/dashboard',
    meta: { requireMerchant: true },
    children: [
      {
        path: 'dashboard',
        name: 'MerchantDashboard',
        component: () => import('@/views/merchant/MerchantDashboard.vue'),
        meta: { title: '商家看板', requireMerchant: true }
      },
      {
        path: 'product',
        name: 'MerchantProduct',
        component: () => import('@/views/merchant/MerchantProduct.vue'),
        meta: { title: '商品管理', requireMerchant: true }
      },
      {
        path: 'order',
        name: 'MerchantOrder',
        component: () => import('@/views/merchant/MerchantOrder.vue'),
        meta: { title: '订单管理', requireMerchant: true }
      }
    ]
  },
  {
    path: '/mobile/capture',
    name: 'MobileCapture',
    component: () => import('@/views/front/MobileCapture.vue'),
    meta: { title: '手机拍摄', public: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫：登录态拦截
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? to.meta.title + ' - 宠知AI' : '宠知AI'
  const userStore = useUserStore()
  const isLogin = !!userStore.token
  const role = userStore.userInfo?.role

  // 已登录访问登录/注册页 -> 跳首页
  if (to.meta.guest && isLogin) {
    if (to.path === '/admin/login') {
      if (role === 'admin') return next('/admin/dashboard')
      return next()
    }
    if (to.path === '/merchant/login') {
      if (role === 'merchant') return next('/merchant/dashboard')
      return next()
    }
    return next(role === 'admin' ? '/admin/dashboard' : (role === 'merchant' ? '/merchant/dashboard' : '/home'))
  }

  // 后台路由守卫：要求管理员身份
  if (to.meta.requireAdmin) {
    if (!isLogin) {
      return next({ path: '/admin/login', query: { redirect: to.fullPath } })
    }
    if (role !== 'admin') {
      return next('/home')
    }
  }

  // 商家路由守卫：要求商家身份
  if (to.meta.requireMerchant) {
    if (!isLogin) {
      return next({ path: '/merchant/login', query: { redirect: to.fullPath } })
    }
    if (role !== 'merchant') {
      return next('/home')
    }
  }

  // 未登录访问受保护页（除登录/注册/公开页外）-> 跳登录
  if (!to.meta.guest && !to.meta.public && !to.meta.requireAdmin && !to.meta.requireMerchant && !isLogin && to.path !== '/home') {
    return next('/login')
  }
  next()
})

export default router
